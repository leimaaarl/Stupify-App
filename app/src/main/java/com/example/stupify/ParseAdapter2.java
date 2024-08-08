package com.example.stupify;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ParseAdapter2 extends RecyclerView.Adapter<ParseAdapter.ViewHolder>{

    private ArrayList<ParseItem> parseItems;

    private final SelectListener selectListener;
    private final FragmentListener fragmentListener;
    private Context context;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    public ParseAdapter2(ArrayList<ParseItem> parseItems, Context context, SelectListener selectListener, FragmentListener fragmentListener) {
            this.parseItems = parseItems;
            this.context = context;
            this.selectListener = selectListener;
            this.fragmentListener = fragmentListener;


    }

    @NonNull
    @Override
    public ParseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.liked_song_list, parent, false);
        return new ParseAdapter.ViewHolder(view, selectListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ParseAdapter.ViewHolder holder, int position) {
        ParseItem parseItem = parseItems.get(position);
        holder.textView_song.setText(parseItem.getSongTitle());
        Picasso.get().load(parseItem.getImgUrl())
                .error(R.drawable.twotone_image_not_supported_24)
                .placeholder(R.drawable.outline_image_24)
                .into(holder.imageView);
        holder.imageView.setContentDescription(parseItem.getImgUrl());
        holder.textView_artist.setText(parseItem.getSongArtist());
        holder.bookmark.setContentDescription(parseItem.getSongUrl());
        holder.textView_song.setContentDescription(parseItem.getSongId());

        holder.bookmark.setOnClickListener(new View.OnClickListener() {

            @Override

                public void onClick(View v) {

                parseItems.clear();

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("Users");

                reference.child(holder.textView_song.getContentDescription().toString()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            if(parseItems.isEmpty()){
                                fragmentListener.onRestartFragment();
                            }

                            System.out.println(parseItems.toString());
                            Toast.makeText(v.getContext(), "Song deleted to bookmark.", Toast.LENGTH_SHORT).show();


                        } else {
                            Toast.makeText(v.getContext(), "Failed to delete song.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return parseItems.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView_artist, textView_song;

        ImageView bookmark;

        public ViewHolder(@NonNull View itemView, SelectListener selectListener, FragmentListener fragmentListener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.song_imageview);
            textView_artist = itemView.findViewById(R.id.song_artist);
            textView_song = itemView.findViewById(R.id.song_title);
            bookmark = itemView.findViewById(R.id.bookmark);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectListener != null){
                        int pos = getAdapterPosition();

                        if (pos !=RecyclerView.NO_POSITION){
                            selectListener.onItemclick(pos);
                        }
                    }
                }
            });


        }
    }
}





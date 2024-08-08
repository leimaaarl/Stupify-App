package com.example.stupify;


import static com.example.stupify.R.drawable.baseline_bookmark_added_24;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;




public class ParseAdapter extends RecyclerView.Adapter<ParseAdapter.ViewHolder>{

    private ArrayList<ParseItem> parseItems;

    private final SelectListener selectListener;
    private Context context;

    private FirebaseDatabase database;
    private DatabaseReference reference;




    public ParseAdapter(ArrayList<ParseItem> parseItems, Context context, SelectListener selectListener) {
        this.parseItems = parseItems;
        this.context = context;
        this.selectListener = selectListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item_list, parent, false);
        return new ViewHolder(view, selectListener);
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
        holder.bookmark.setContentDescription(parseItem.getSongUrl());



        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (holder.bookmark.getTag() != null && (int) holder.bookmark.getTag() == baseline_bookmark_added_24) {
                    // Bookmark is already added, so do nothing
                    return;
                }

                System.out.println(holder.bookmark.getTag());

                String title, artist, audio, img;

                    title = holder.textView_song.getText().toString();
                    artist = holder.textView_artist.getText().toString();
                    audio = holder.bookmark.getContentDescription().toString();
                    img = holder.imageView.getContentDescription().toString();
                    holder.bookmark.setImageResource(baseline_bookmark_added_24);
                    holder.bookmark.setTag(baseline_bookmark_added_24);

                    FirebaseUserModel user = new FirebaseUserModel(img,title,artist,audio);
                    database = FirebaseDatabase.getInstance();
                    reference = database.getReference("Users");
                    String userId = reference.push().getKey();
                    reference.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(v.getContext(), "Song added to the favorites!", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

        });

    }



    void updateData(ArrayList<ParseItem> data){
        parseItems.clear();
        parseItems.addAll(data);
        notifyDataSetChanged();
    }




    @Override
    public int getItemCount() {
        return parseItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView_artist, textView_song;

        ImageView bookmark;

        public ViewHolder(@NonNull View itemView, SelectListener selectListener) {
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

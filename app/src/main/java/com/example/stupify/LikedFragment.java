package com.example.stupify;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class LikedFragment extends Fragment implements SelectListener, FragmentListener{

    private ProgressBar progressBar;

    private DatabaseReference mDatabase;

    private TextView empty;
    RecyclerView recyclerView;
    private ArrayList<FirebaseUserModel> userList  = new ArrayList<>();

    private ArrayList<ParseItem> parseItems  = new ArrayList<>();
    private ParseAdapter2 adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_liked, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ParseAdapter2(parseItems, getContext(), this, this);
        userList = new ArrayList<>();
        recyclerView.setAdapter(adapter);
        empty = view.findViewById(R.id.emptyplaceholder);
        empty.setVisibility(View.VISIBLE);


        mDatabase = FirebaseDatabase.getInstance().getReference("Users");


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                progressBar.setVisibility(View.GONE);
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    FirebaseUserModel user = postSnapshot.getValue(FirebaseUserModel.class);

                    if (dataSnapshot.exists()){

                        if (user != null) {
                            String title, artist, audio, img;
                            title = user.songTitle;
                            artist = user.songArtist;
                            audio = user.songUrl;
                            img = user.imgUrl;

                            parseItems.add(new ParseItem( img, title.toUpperCase(), artist, audio, postSnapshot.getKey()));


                            if (getActivity() != null) {
                                try {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Notify adapter of data change
                                            empty.setVisibility(View.GONE);
                                            progressBar.setVisibility(View.GONE);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }

                        }
                    }

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("error");
            }
        });

        return view;


    }



    @Override
    public void onItemclick(int position) {

        Intent intent = new Intent(requireContext(), PlayBack.class);

        intent.putExtra("audio_url", parseItems.get(position).getSongUrl());
        intent.putExtra("song_img", parseItems.get(position).getImgUrl());
        intent.putExtra("song_title", parseItems.get(position).getSongTitle());
        intent.putExtra("song_artist", parseItems.get(position).getSongArtist());

        startActivity(intent);
    }

    @Override
    public void onRestartFragment() {

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment newFragment = new LikedFragment();

        fragmentTransaction.replace(R.id.frame, newFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
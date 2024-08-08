package com.example.stupify;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;

import retrofit2.http.Query;




public class HomeFragment extends Fragment implements SelectListener {



    public TextView placeholder;


    private Button buttonSearch;
    private EditText textSearch;
    private ImageView imageView;


    private RecyclerView recyclerView;




    private ParseAdapter adapter;
    private ArrayList<ParseItem> parseItems = new ArrayList<>();
    private ProgressBar progressBar;

    ExecutorService executor = Executors.newSingleThreadExecutor();



    interface RequestUser {


        @Headers({"X-RapidAPI-Key: [YOUR API KEY]",
                "X-RapidAPI-Host: [YOUR API HOST]"
        })

        @GET("search")
        Call<APIRequest> getData(@Query("q") String searchSong);


    }


    String filt = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);



        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ParseAdapter(parseItems, getContext(), this);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.HORIZONTAL));
        recyclerView.setAdapter(adapter);
        imageView = view.findViewById(R.id.tophits);


        placeholder = view.findViewById(R.id.placeholder);

        placeholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), Signup.class);
                startActivity(intent);
            }
        });



        buttonSearch = view.findViewById(R.id.search_btn);
        textSearch = view.findViewById(R.id.search_text);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                parseItems.clear();
                filt = textSearch.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                executor.execute(new ScrapeTask());

            }
        });


        progressBar.setVisibility(View.VISIBLE);


        executor.execute(new ScrapeTask());

        try {
            test();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        executor.shutdown();

    }



    private class ScrapeTask implements Runnable {
        @Override
        public void run() {

           scrapeMusic(filt);


        }
    }

    public void scrapeMusic(String song) {


        if (filt.isEmpty()){

            parseItems.clear();
            try {

                String url = "https://www.officialcharts.com/charts/singles-chart/";


                Document doc = Jsoup.connect(url).get();
                Elements items = doc.select("div.chart-item-content");


                for (Element item : items) {
                    String songTitle = item.select("a.chart-name").select("span:nth-child(2)").text();
                    String songUrl = item.select("button.audio-control").select("audio").attr("src");
                    String songArtist = item.select("a.chart-artist").select("span").text();
                    String songImg = item.select("div.chart-image").select("img.chart-image-large").attr("src");

                    parseItems.add(new ParseItem(songImg, songTitle.toUpperCase(), songArtist, songUrl, ""));


                    System.out.println("url: " + songImg + " | name: " + songTitle + "| artist" + songArtist + "| audio: " + songUrl);


                }


            } catch (Exception e) {
                throw new RuntimeException(e);
            }


            if(getActivity() != null){
                try{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Notify adapter of data change
                            progressBar.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }




        }else{


            parseItems.clear();


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://deezerdevs-deezer.p.rapidapi.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            RequestUser requestUser = retrofit.create(RequestUser.class);
            requestUser.getData(song).enqueue(new Callback<APIRequest>() {

                @Override
                public void onResponse(Call<APIRequest> call, Response<APIRequest> response) {


                    System.out.println(response.body().getData());

                    for (Track track : response.body().getData()){
                        Track.Album album = track.getAlbum();
                        Track.Artist artist = track.getArtist();

                            String title = track.getTitle();
                            String artistName = artist.getName();
                            String preview = track.getPreview();
                            String image = album.getCover_big();


                        System.out.println("song: "+title + " artist: " + artistName + " img: "+ image + " url: "+ preview);
                        parseItems.add(new ParseItem(image, title.toUpperCase(), artistName, preview, ""));


                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onFailure(Call<APIRequest> call, Throwable throwable) {
                    System.out.println(throwable.getMessage());
                }
            });

        }


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


}
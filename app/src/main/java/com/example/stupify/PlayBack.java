package com.example.stupify;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class PlayBack extends AppCompatActivity {

    private ImageView imagePlayPause, displayImg, back, share;
    private TextView textStart, textEnd, title, artist;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    private EditText email;


    String gmailSMTP = "smtp.gmail.com";



    int port = 587;

    String senderEmail = "[YOUR GMAIL ACCOUNT]";
    String password = "[YOUR GMAIL APP PASSWORD]";


    String smtpHost = "smtp.gmail.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play_back);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        imagePlayPause = findViewById(R.id.imagePlayPause);
        textStart = findViewById(R.id.songTimer);
        textEnd = findViewById(R.id.songDuration);
        seekBar = findViewById(R.id.seekBar);
        mediaPlayer = new MediaPlayer();

        displayImg = findViewById(R.id.displayImg);
        title = findViewById(R.id.displayTitle);
        artist = findViewById(R.id.displayArtist);



        share = findViewById(R.id.share);



        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                View view = LayoutInflater.from(PlayBack.this).inflate(R.layout.share_dialog, null);
                AlertDialog alertDialog = new MaterialAlertDialogBuilder(PlayBack.this)
                        .setView(view)
                        .setPositiveButton("Share", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                email = view.findViewById(R.id.gmail);
                                String gmailUser = email.getText().toString();

                                String songTitle = title.getText().toString();
                                String songArtist = artist.getText().toString();

                                sendGmail(gmailUser, songTitle, songArtist);

                                Toast.makeText(PlayBack.this, "Song shared to "+ gmailUser , Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                alertDialog.show();


            }
        });


        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });

        seekBar.setMax(100);

        imagePlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mediaPlayer.isPlaying()){
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    imagePlayPause.setImageResource(R.drawable.baseline_play_circle_24);
                }else{
                    mediaPlayer.start();
                    imagePlayPause.setImageResource(R.drawable.baseline_pause_circle_24);
                    updateSeekbar();
                }

            }
        });

        preparedMediaPlayer();



    }


    void preparedMediaPlayer(){
        try {

            String audio_url = getIntent().getStringExtra("audio_url");
            String songtitle = getIntent().getStringExtra("song_title");
            String songartist = getIntent().getStringExtra("song_artist");
            String songimg = getIntent().getStringExtra("song_img");


            Picasso.get().load(songimg)
                    .error(R.drawable.baseline_broken_image_24)
                    .placeholder(R.drawable.outline_image_24)
                    .into(displayImg);

            title.setText(songtitle);
            artist.setText(songartist);


            mediaPlayer.setDataSource(audio_url);
            mediaPlayer.prepare();
            textEnd.setText(milliSecondsToTimer(mediaPlayer.getDuration()));

        }catch (Exception e){
            Toast.makeText(this, "No preview available", Toast.LENGTH_SHORT).show();
        }


    }

    Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekbar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            textStart.setText(milliSecondsToTimer(currentDuration));
        }
    };

    void updateSeekbar(){
        if(mediaPlayer.isPlaying()){
            seekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
            handler.postDelayed(updater, 1000);
        }
    }




    String milliSecondsToTimer(long miliseconds) {
        String timerString = "";
        String secondsString = "";
        int hours = (int) (miliseconds / (1000 * 60 * 60));
        int minutes = (int) (miliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((miliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0){
            timerString = hours + ":";
        }
        if (seconds < 10){
            secondsString = "0" + seconds;


        }else{
            secondsString = ""+ seconds;
        }

        timerString = timerString + minutes + ":" + secondsString;
        return timerString;

    }

    public void sendGmail(String recieverEmail, String songName, String songArtist){

        try {

            String subjectString = "Song Recommendation!";
            String messageString = "Hey Check out this song I found! \n\n "+songName+ " by "+songArtist;




            Properties props = System.getProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", port);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, password);
                }
            });


            MimeMessage mimeMessage = new MimeMessage(session);

            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recieverEmail));
            mimeMessage.setSubject(subjectString);
            mimeMessage.setText(messageString);



            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            thread.start();

            Toast.makeText(PlayBack.this, "Message Sent", Toast.LENGTH_SHORT).show();

        } catch (AddressException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

}
package com.example.stupify;

public class FirebaseUserModel {

    public String imgUrl;
    public String songTitle;
    public String songArtist;

    public String songUrl;

    public FirebaseUserModel() {
    }

    public FirebaseUserModel(String imgUrl, String songTitle, String songArtist, String songUrl) {
        this.imgUrl = imgUrl;
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.songUrl = songUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }
}

package com.example.stupify;


public class ParseItem {
    private String imgUrl;
    private String songTitle;
    private String songArtist;

    private String songUrl;

    private String songId;

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public ParseItem(String imgUrl, String songTitle, String songArtist, String songUrl, String songId) {
        this.imgUrl = imgUrl;
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.songUrl = songUrl;
        this.songId = songId;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
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


}

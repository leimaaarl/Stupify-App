package com.example.stupify;

public class Track {
    private double id;

    private  String title;
    private String image;

    private String preview;
    private Artist artist;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    private Album album;
    private String type;

    public double getId() {
        return id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public class Artist {
        private double id;
        private String name;
        private String type;

        public double getId() {
            return id;
        }

        public void setId(double id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }


        // Getters and setters
    }

    public class Album {
        private double id;
        private String title;
        private String cover;

        private String cover_big;

        public double getId() {
            return id;
        }

        public void setId(double id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getCover_big() {
            return cover_big;
        }

        public void setCover_big(String cover_big) {
            this.cover_big = cover_big;
        }


// Getters and setters
    }
    // Getters and setters
}
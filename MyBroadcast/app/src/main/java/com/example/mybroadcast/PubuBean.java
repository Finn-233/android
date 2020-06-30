package com.example.mybroadcast;

public class PubuBean {
    private int image;
    private String title;
    private String autor;

    public PubuBean(int image, String title, String autor) {
        this.image = image;
        this.title = title;
        this.autor = autor;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }
}

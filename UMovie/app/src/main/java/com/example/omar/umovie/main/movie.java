package com.example.omar.umovie.main;

/**
 * Created by omar on 03/11/2016.
 */
public class movie {
    private String title;
    private String imagePosterURL;
    private String overView;
    private String RealesDate;
    private double vote_average;
    private int resorce_id;
    private String id;

    public movie(String title, String imagePosterURL, String overView, String RealesDate, double vote_average, String id) {
        this.title = title;
        this.imagePosterURL = imagePosterURL;
        this.overView = overView;
        this.RealesDate = RealesDate;
        this.vote_average = vote_average;
        this.id = id;
    }


    public movie(int resorce_id) {
        this.resorce_id = resorce_id;
    }

    public String getTitle() {
        return title;
    }

    public String getImagePosterURL() {
        return imagePosterURL;
    }

    public String getOverView() {
        return overView;
    }

    public String getRealesDate() {
        return RealesDate;
    }

    public double getVote_average() {
        return vote_average;
    }

    public String getId() {
        return id;
    }

}

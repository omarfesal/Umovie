package com.example.omar.umovie.Detail;

/**
 * Created by omar on 01/12/2016.
 */
public class review {

    String author , content;

    review(String author , String content){
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}

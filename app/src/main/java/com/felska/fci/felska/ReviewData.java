package com.felska.fci.felska;

public class ReviewData {
    String name, date, description, imageurl;
    float rate;

    public ReviewData(String name, String date, String description, String imageurl, float rate) {
        this.name = name;
        this.date = date;
        this.imageurl = imageurl;
        this.description = description;
        this.rate = rate;
    }

    public float getRate() {
        return rate;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getImageurl() {
        return imageurl;
    }
}

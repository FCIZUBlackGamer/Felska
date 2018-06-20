package com.felska.fci.felska;

public class TripData {
    String id, name, date, description, imageurl;
    String from, to;

    public TripData(String id, String name, String date, String description, String imageurl, String from, String to) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.imageurl = imageurl;
        this.description = description;
        this.from = from;
        this.to = to;
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

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getId() {
        return id;
    }
}

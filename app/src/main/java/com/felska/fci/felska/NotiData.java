package com.felska.fci.felska;

public class NotiData {
    String id, name, imageurl;

    public NotiData(String id, String name, String imageurl) {
        this.id = id;
        this.name = name;
        this.imageurl = imageurl;
    }

    public String getName() {
        return name;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getId() {
        return id;
    }
}

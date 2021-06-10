package com.example.android.thegalleryapp.models;

import android.graphics.Bitmap;

public class Item {
    public String image;
    public int color;
    public String label;
    public String url;

    /**
     * Parameterized constructor for item class
     * @param color Image color
     * @param label Image label
     * @param url   Image url
     */
    public Item( int color, String label, String url){
        this.color = color;
        this.label = label;
        this.url = url;
    }
}


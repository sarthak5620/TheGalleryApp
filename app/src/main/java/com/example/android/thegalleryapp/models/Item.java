package com.example.android.thegalleryapp.models;

import android.graphics.Bitmap;

public class Item {
    public Bitmap image;
    public int color;
    public String label;
    public String url;

    //Constructor of item class
    public Item(Bitmap image, int color, String label, String url){
        this.image = image;
        this.color = color;
        this.label = label;
        this.url = url;
    }

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


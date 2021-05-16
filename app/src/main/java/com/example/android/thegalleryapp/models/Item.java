package com.example.android.thegalleryapp.models;

import android.graphics.Bitmap;

public class Item {
    public Bitmap image;
    public int color;
    public String label;

    public Item(Bitmap image, int color, String label) {
        this.image = image;
        this.color = color;
        this.label = label;
    }
}

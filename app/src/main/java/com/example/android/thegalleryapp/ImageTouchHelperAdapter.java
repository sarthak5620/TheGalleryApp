package com.example.android.thegalleryapp;

public interface  ImageTouchHelperAdapter {
    // want to move the item..
    void onItemMove(int FromPosition, int toPosition);

    // want to dismiss the item...
    void onItemDis(int position);
}
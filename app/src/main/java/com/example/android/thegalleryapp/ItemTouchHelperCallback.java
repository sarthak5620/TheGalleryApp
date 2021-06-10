package com.example.android.thegalleryapp;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

class ImageTouchHelperCallback extends ItemTouchHelper.Callback {
    ImageTouchHelperAdapter imageTouchHelperAdapter;
    public ImageTouchHelperCallback(ImageTouchHelperAdapter imageTouchHelperAdapter){
        this.imageTouchHelperAdapter = imageTouchHelperAdapter;
    }
    /// ON Long pressed...
    @Override
    public boolean isLongPressDragEnabled() {
        return  false;
    }
    // On Swiped..
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragtheflag = ItemTouchHelper.UP|ItemTouchHelper.DOWN;
        int swaptheflag = ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragtheflag,swaptheflag);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        imageTouchHelperAdapter.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        imageTouchHelperAdapter.onItemDis(viewHolder.getAdapterPosition());
    }
}
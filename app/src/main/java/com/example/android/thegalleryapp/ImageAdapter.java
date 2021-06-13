package com.example.android.thegalleryapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.android.thegalleryapp.databinding.CardItemBinding;
import com.example.android.thegalleryapp.models.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>implements ImageTouchHelperAdapter {

    private Context context;
    private  List<Item> items, VisiblelabelItem;
    public ItemTouchHelper mainitemTouchHelper;
    public String imageUrl;
    public int index;
    public CardItemBinding itemCardBinding;
    public int mode;
    public List<ImageViewHolder> holderList = new ArrayList<>();
    public static final String MODE = "mode";


    public ImageAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
        VisiblelabelItem = items;

    }
    /*
     * Methods to be implemented to use recycler view's adapter
     */
    @NonNull
    @Override
    // Image view Holder..
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardItemBinding binding = CardItemBinding.inflate(LayoutInflater.from(context)
                , parent, false);
        return new ImageViewHolder(binding);

    }
    /// Bind view Holder...
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holderList.add(holder);
        Glide.with(context).asBitmap().load(VisiblelabelItem.get(position).url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.b.imageView1.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        holder.b.title.setText(VisiblelabelItem.get(position).label);
        holder.b.title.setBackgroundColor(VisiblelabelItem.get(position).color);
        // Item itemList = labelItem.get(position);


    }
    /*
     * get total count of items
     */
    @Override
    public int getItemCount() {

        return VisiblelabelItem.size();
    }

    /*
     * Filter method called when user enters query
     */
    public void filter(String query) {
        if (query.trim().isEmpty()) {
            VisiblelabelItem = items;
            notifyDataSetChanged();
            return;
        }
        query = query.toLowerCase();

        List<Item> filterdata = new ArrayList<>();
        for (Item item : items) {
            if (item.label.toLowerCase().contains(query)) {
                filterdata.add(item);
            }
        }
        VisiblelabelItem = filterdata;
        notifyDataSetChanged();
    }

    public void setImageAdapter(ItemTouchHelper itemTouchHelper) {
        mainitemTouchHelper = itemTouchHelper;
    }
    /*
     *Method to move items
     */
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Item formItem = items.get(fromPosition);
        items.remove(formItem);
        items.add(toPosition, formItem);
        VisiblelabelItem = items;
        notifyItemMoved(fromPosition, toPosition);
    }
    /*
     * Method to
     */
    @Override
    public void onItemDis(int position) {
        return;
    }

    /*
     * Method to sort items on the basis of query entered by user
     */
    public void SortData() {
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o1.label.toLowerCase().compareTo(o2.label.toLowerCase());
            }


        });
        VisiblelabelItem = items;
        notifyDataSetChanged();

    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnTouchListener, GestureDetector.OnGestureListener {
        CardItemBinding b;
        GestureDetector gestureDetector;

        public ImageViewHolder(@NonNull CardItemBinding b) {
            super(b.getRoot());
            this.b = b;
            gestureDetector = new GestureDetector(b.getRoot().getContext(), this);
            eventListenerHandler();

        }

        void eventListenerHandler() {
            if (mode == 0) {
                b.imageView1.setOnTouchListener(null);
                b.title.setOnTouchListener(null);
                b.title.setOnCreateContextMenuListener(this);
                b.imageView1.setOnCreateContextMenuListener(this);
            } else if (mode == 1) {
                b.title.setOnCreateContextMenuListener(null);
                b.imageView1.setOnCreateContextMenuListener(null);
                b.title.setOnTouchListener(this);
                b.imageView1.setOnTouchListener(this);
            }
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (mode == 1) {
                mainitemTouchHelper.startDrag(this);
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select ");
            menu.add(this.getAdapterPosition(), R.id.editMenuItem, 0, "Edit");
            menu.add(this.getAdapterPosition(), R.id.shareImage, 0, "Share");
            imageUrl = items.get(this.getAdapterPosition()).url;
            index = this.getAdapterPosition();
            itemCardBinding = b;

        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            return false;
        }
    }

}

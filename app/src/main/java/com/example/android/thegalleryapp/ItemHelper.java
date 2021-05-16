 package com.example.android.thegalleryapp;

 import android.content.Context;
 import android.graphics.Bitmap;
 import android.graphics.drawable.Drawable;

 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.palette.graphics.Palette;

 import com.bumptech.glide.Glide;
 import com.bumptech.glide.request.target.CustomTarget;
 import com.bumptech.glide.request.transition.Transition;
 import com.google.android.gms.tasks.OnFailureListener;
 import com.google.android.gms.tasks.OnSuccessListener;
 import com.google.mlkit.vision.common.InputImage;
 import com.google.mlkit.vision.label.ImageLabel;
 import com.google.mlkit.vision.label.ImageLabeler;
 import com.google.mlkit.vision.label.ImageLabeling;
 import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;

public class ItemHelper {
    //Constants used in project
    private OnCompleteListener listener;
    private Context context;
    private Bitmap bitmap;
    private Set<Integer> colors;
    private String rectangularImageUrl = "https://picsum.photos/%d/%d?type=",
            squareImageUrl = "https://picsum.photos/%d?type=";

    /*
    **
    * callback of fetch data function to get rectangular image
     */
    void fetchData(int x , int y ,Context context, OnCompleteListener listener){
            this.listener = listener;
            this.context = context;
            //Callback of fetch image to get image using url provided through glide
            fetchImage(
                    String.format(rectangularImageUrl,x,y)
            );
    }
    /*
     **
     * callback of fetch data function to get square image
     */
    void fetchData(int x ,Context context, OnCompleteListener listener){
        this.listener = listener;
        this.context = context;
        //Callback of fetch image to get image using url provided through glide
        fetchImage(
                String.format(squareImageUrl,x)
        );
    }
    /*
     **
     * callback of fetch image function to get image using glide
     */
    void fetchImage(String url){
        //Use glide to get image offline by using bitmap and url
        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    //Callback of function if source ready
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        bitmap = resource;
                        //extract palette from bitmap
                        extractPaletteFromBitmap();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                    //Callback if load failed
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        listener.onError("Image load failed!");
                    }
                });
    }
    /*
     **
     * Method to get Palette from bitmap consisting of various colors
     */
    private void extractPaletteFromBitmap() {
         Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                colors = getColoursFromPalette(p);
                //callback Label image to get labels for image using ML label kit from google
                labelImage();
            }
    });
    }

    /*
     **
     * Method to get labels for image we received using glide as bitmap.ML kit is used to get labels
     */
    private void labelImage() {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        labeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        List<String> strings = new ArrayList<>();
                        for (ImageLabel label : labels) {
                            strings.add(label.getText());
                        }
                        listener.onFetched(bitmap,colors,strings);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onError(e.toString());
                    }
                });

    }

    /*
     **
     * Get colors from palette we received through the image we fetched from glide.There are 6 types of colours including 3 of both types muted and vibrant
     */
    private Set<Integer> getColoursFromPalette(Palette p) {
        Set<Integer>colors = new HashSet<>();
        colors.add(p.getVibrantColor(0));
        colors.add(p.getLightMutedColor(0));
        colors.add(p.getDarkMutedColor(0));
        colors.add(p.getLightVibrantColor(0));
        colors.add(p.getDarkVibrantColor(0));
        colors.add(p.getMutedColor(0));
        colors.add(p.getVibrantColor(0));
        colors.remove(0);
        return colors;
    }
    /*
     **
     * Interface includes 2 methods which need to be implemented when used
     */
    interface OnCompleteListener{
        void onFetched(Bitmap image , Set<Integer>colors , List<String>labels);
        void onError(String error);
    }
}


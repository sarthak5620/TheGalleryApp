package com.example.android.thegalleryapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.android.thegalleryapp.databinding.AddImageFromGalleryDialogBinding;
import com.example.android.thegalleryapp.databinding.ChipColorBinding;
import com.example.android.thegalleryapp.databinding.ChipLabelBinding;
import com.example.android.thegalleryapp.models.Item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddImageFromGalleryDialog {
    private Context context;
    private AddImageFromGalleryDialogBinding b;
    private OnCompleteListener listener;
    private LayoutInflater inflater;
    private boolean isCustomLabel;
    private Bitmap image;
    private AlertDialog dialog;
    private String imageUrl;
    private Set<Integer> colors;

    //Use of show function in another class to implement all other functions of this class
    public void show(Context context,String imageUrl , OnCompleteListener listener) {
        this.context = context;
        this.imageUrl = imageUrl;
        this.listener = listener;
        //Create object and inflater for context and bind it
        if(context instanceof MainActivity){
            inflater=((MainActivity)context).getLayoutInflater();
            b= AddImageFromGalleryDialogBinding.inflate(inflater);
        }
        else {
            dialog.dismiss();
            listener.onError("Cast Exception:");
            return;
        }

        //Create and Show Dialog:
        dialog= new MaterialAlertDialogBuilder(context,R.style.CustomDialogTheme)
                .setView(b.getRoot())
                .setCancelable(false)
                .show();

        fetchImage(imageUrl);
        //callback of sharing image event
        handleShareImageEvent();
        //callback of add image button
        handleAddButton();
    }
    /*
     **
     * Handle image sharing event when button is clicked
     */
    private void handleShareImageEvent() {
        b.shareImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Glide.with(context)
                            .asBitmap()
                            .load(imageUrl)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    Bitmap icon = resource;
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("image/jpeg");

                                    ContentValues values = new ContentValues();
                                    values.put(MediaStore.Images.Media.TITLE, "title");
                                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                                    Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            values);
                                    OutputStream outputStream;
                                    try {
                                        outputStream = context.getContentResolver().openOutputStream(uri);
                                        icon.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                        outputStream.close();
                                    } catch (Exception e) {
                                        System.err.println(e.toString());
                                    }

                                    share.putExtra(Intent.EXTRA_STREAM, uri);
                                    context.startActivity(Intent.createChooser(share, "Share Image"));
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }

                            });

                } catch (Exception e) {
                    Toast.makeText(context, "App not Installed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

/*
*Fetch image function called and all asynchronous tasks run serial wise
 */
    private void fetchImage(String url) {

        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        image = resource;
                        //Callback of task 1
                        extractPaletteFromBitmap();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }
/*
*Handle add image button
 */
    private void handleAddButton() {
        b.deviceImageView.setImageBitmap(image);
        b.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int colorChipId=b.chipColor.getCheckedChipId()
                        ,LabelChipId= b.chipLabel.getCheckedChipId();

                if(colorChipId== -1 || LabelChipId== -1){
                    Toast.makeText(context,"Please choose color and label",Toast.LENGTH_SHORT).show();
                    return;
                }

                //Get Color and Label:
                String label;
                if(isCustomLabel){
                    label=b.dCustomLabelET.getText().toString().trim();
                    if(label.isEmpty()){
                        Toast.makeText(context,"Please enter custom label",Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                else {
                    label=((Chip)b.chipLabel.findViewById(LabelChipId)).getText().toString();
                }

                int color=((Chip)b.chipColor.findViewById(colorChipId)).getChipBackgroundColor().getDefaultColor();


                listener.onAddCompleted(new Item(color,label,imageUrl));

                dialog.dismiss();
            }
        });
    }
   /*
   *Palette extractor using bitmap
    */
    private void extractPaletteFromBitmap(){
        Palette.from(image).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                colors= getColorsFromPalette(p);
                //Callback of label image
                labelImage();
            }
        });
    }
    /*
    *Extracting labels of image using ML kit
     */
    private void labelImage() {
        InputImage inputImage=InputImage.fromBitmap(image,0);
        ImageLabeler labeler= ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        labeler.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        List<String> strings=new ArrayList<>();
                        for(ImageLabel label:labels){
                            strings.add(label.getText());
                        }
                        inflateColorChips(colors);
                        inflateLabelChips(strings);
                        b.deviceImageView.setImageBitmap(image);

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
    *Extract colors of image using palette
     */
    private Set<Integer> getColorsFromPalette(Palette p) {
        Set<Integer> colors = new HashSet<>();

        colors.add(p.getVibrantColor(0));
        colors.add(p.getLightVibrantColor(0));
        colors.add(p.getDarkVibrantColor(0));

        colors.add(p.getMutedColor(0));
        colors.add(p.getLightMutedColor(0));
        colors.add(p.getDarkMutedColor(0));

        colors.add(p.getVibrantColor(0));
        colors.remove(0);

        return colors;
    }

    /*
    *Handle custom input
    */

    private void handleCustomLabel() {
        ChipLabelBinding binding = ChipLabelBinding.inflate(inflater);
        binding.getRoot().setText(R.string.custom);
        b.chipLabel.addView(binding.getRoot());

        binding.getRoot().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                b.customLabelInput.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                isCustomLabel= isChecked;

            }
        });
    }
    /*
     **
     * method to inflate color chips of image we fetched
     */
    private void inflateColorChips(Set<Integer> colors) {
        for (int color : colors) {
            ChipColorBinding binding = ChipColorBinding.inflate(inflater);
            binding.getRoot().setChipBackgroundColor(ColorStateList.valueOf(color));
            b.chipColor.addView(binding.getRoot());
        }
    }
    /*
     **
     * method to inflate label chips of image we fetched
     */
    private void inflateLabelChips(List<String> labels)  {
        for (String label : labels) {
            ChipLabelBinding binding = ChipLabelBinding.inflate(inflater);
            binding.getRoot().setText(label);
            b.chipLabel.addView(binding.getRoot());
        }
        handleCustomLabel();
    }

    interface OnCompleteListener{
        void onAddCompleted(Item item);
        void onError(String error);
    }
}

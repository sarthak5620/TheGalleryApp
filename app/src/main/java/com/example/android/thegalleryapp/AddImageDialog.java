package com.example.android.thegalleryapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;

import com.example.android.thegalleryapp.databinding.ChipColorBinding;
import com.example.android.thegalleryapp.databinding.ChipLabelBinding;
import com.example.android.thegalleryapp.databinding.DialogAddImageBinding;
import com.example.android.thegalleryapp.models.Item;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.Set;

public class AddImageDialog implements ItemHelper.OnCompleteListener {
    private Context context;
    private OnCompleteListener listener;
    private DialogAddImageBinding b ;
    private LayoutInflater inflater;
    private boolean isCustomLabel;
    private Bitmap image;
    private AlertDialog dialog;

    void show(Context context,OnCompleteListener listener){
        this.context = context;
        this.listener = listener;
        if (context instanceof MainActivity){
             b = DialogAddImageBinding.inflate(
                    ((MainActivity)context).getLayoutInflater()
            );
        }
        else{
            dialog.dismiss();
            listener.onError("Cast exception");
            return;
        }
         dialog = new MaterialAlertDialogBuilder(context,R.style.CustomDialogTheme)
                .setView(b.getRoot())
                .show();

        handleDimensions();
        hideErrorEt();
    }

    private void showData(Bitmap image, Set<Integer> colors, List<String> labels) {
        this.image = image;
        b.imageLoaded.setImageBitmap(image);
        inflateColorChips(colors);
        inflateLabelChips(labels);
        handleCustomLabelInput();
        handleAddImageEvent();
        b.fetcher.setVisibility(View.GONE);
        b.mainRoot.setVisibility(View.VISIBLE);
    }

    private void handleDimensions() {
        b.materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String widthImage = b.width.getText().toString().trim();
                String heightImage = b.height.getText().toString().trim();
                //Guard code
                if (widthImage.isEmpty() && heightImage.isEmpty()){
                    b.width.setError("Please input something!");
                    return;
                }
                b.inputDialogRoot.setVisibility(View.GONE);
                b.fetcher.setVisibility(View.VISIBLE);
                hideKeyboard();
                if (widthImage.isEmpty()){
                    int height = Integer.parseInt(heightImage);
                    fetchRandomImage(height);
                }
                else if (heightImage.isEmpty()){
                    int width = Integer.parseInt(widthImage);
                    fetchRandomImage(width);
                }
                else{
                    int height = Integer.parseInt(heightImage);
                    int width = Integer.parseInt(widthImage);
                    fetchRandomImage(width ,height);
                }
            }
        });
    }

    private void handleAddImageEvent() {
        b.AddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.AddImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the chip selected IDs
                        int colorChipId = b.colorChips.getCheckedChipId();
                        int labelChipId = b.colorLabel.getCheckedChipId();

                        // Guard Code
                        if (colorChipId == -1 || labelChipId == -1) {
                            Toast.makeText(context, "Please choose color or label", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // label of the image
                        String label;
                        // Checking for the custom label
                        if (isCustomLabel) {
                            // Get the label
                            label = b.customLabelInput.getEditText().getText().toString().trim();
                            if (label.isEmpty()) {
                                // Set the error to the text field
                                b.customLabelInput.setError("Please enter custom label");
                                return;
                            }
                        } else {
                            // Get label from the chip
                            label = ((Chip) b.colorLabel.findViewById(labelChipId)).getText().toString();
                        }

                        // Get color from the chip selected
                        int color = ((Chip) b.colorChips.findViewById(colorChipId)).
                                getChipBackgroundColor().getDefaultColor();

                        // Callback when all the parameter are accepted
                        listener.OnImageAdded(new Item(image,color,label));

                        // Dismiss the dialog box
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void handleCustomLabelInput() {
        ChipLabelBinding binding = ChipLabelBinding.inflate(inflater);
        binding.getRoot().setText("Custom");
        b.colorLabel.addView(binding.getRoot());

        binding.getRoot().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    b.customLabelInput.setVisibility(isChecked ?View.VISIBLE : View.GONE);
                isCustomLabel = isChecked;
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(b.width.getWindowToken(), 0);
    }
    private void fetchRandomImage(int width , int height) {
        new ItemHelper()
                .fetchData(width, height, context, this);
    }

    private void fetchRandomImage(int x) {
        new ItemHelper()
                .fetchData(x, context, this);
    }

    private void hideErrorEt() {
        b.width.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                b.width.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void inflateLabelChips(List<String> labels) {
        for (String label : labels){
            ChipLabelBinding binding = ChipLabelBinding.inflate(inflater);
            binding.getRoot().setText(label);
            b.colorLabel.addView(binding.getRoot());
        }
    }

    private void inflateColorChips(Set<Integer>colors) {
        for (int color : colors){
            ChipColorBinding binding = ChipColorBinding.inflate(inflater);
            binding.getRoot().setChipBackgroundColor(ColorStateList.valueOf(color));
            b.colorChips.addView(binding.getRoot());
        }
    }



    public void onFetched(Bitmap image, Set<Integer> colors, List<String> labels) {
        showData(image,colors,labels);
    }


    @Override
    public void onError(String error) {
        dialog.dismiss();
        listener.onError(error);
    }
    interface OnCompleteListener{
        void OnImageAdded(Item item);
        void onError(String error);
    }
}

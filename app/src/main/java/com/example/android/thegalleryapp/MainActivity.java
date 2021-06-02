package com.example.android.thegalleryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import com.bumptech.glide.Glide;
import com.example.android.thegalleryapp.databinding.ActivityMainBinding;
import com.example.android.thegalleryapp.databinding.CardItemBinding;
import com.example.android.thegalleryapp.models.Item;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding b;
    List<Item> listOfItems = new ArrayList<>();
    List<Item> removeItem;
    int noOfImages = 0;
    private boolean isEdited;
    private boolean isAdd;
    private final int RESULT_GALLERY = 1;
    private Intent intent;
    private boolean isDialogBoxShowed ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSharedPreferences();

        setOrientation();
    }

    private void setOrientation() {
        if (this.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            isDialogBoxShowed = true;

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    /*
    *Saving data even when user changes orientation or saves an image
     */
    private void setSharedPreferences() {
        String items = getPreferences(MODE_PRIVATE).getString("ITEMS", null);
        if (items == null || items.equals("[]")) {
            return;
        }
        b.Heading.setVisibility(View.GONE);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Item>>() {
        }.getType();

        listOfItems = gson.fromJson(items, type);

        //Fetch data from caches
        for (Item item : listOfItems) {
            CardItemBinding binding = CardItemBinding.inflate(getLayoutInflater());

            Glide.with(this)
                    .asBitmap()
                    .onlyRetrieveFromCache(true)
                    .load(item.url)
                    .into(binding.imageView1);

            binding.title.setBackgroundColor(item.color);
            binding.title.setText(item.label);
            b.list.addView(binding.getRoot());

        }

        noOfImages = listOfItems.size();
    }

    /*
     *Menu methods
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Validate item id
        if (item.getItemId() == R.id.AddImage) {
            //Call dialog
            showAddImageDialog();
            return true;
        } else if (item.getItemId() == R.id.galleryImage) {
            showAddImageFromGalleryDialog();
        }
        return false;
    }
/*
* Method to add the shared image from gallery
 */
    private void showAddImageFromGalleryDialog() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choose from"),1 );
    }

/*
* Dialog to Add image
 */
    private void showAddImageDialog() {
        //Import object of class addImageDialog
        new AddImageDialog()
                //Call onComplete Listener and implement all methods of interface
                .show(this, new AddImageDialog.OnCompleteListener() {
                    @Override
                    public void OnImageAdd(Item item) {
                        //CallBack of function to inflate item
                        inflateViewForItem(item);
                    }

                    @Override
                    public void onError(String error) {
                        new MaterialAlertDialogBuilder(MainActivity.this)
                                .setTitle("Error")
                                .setMessage(error)
                                .show();
                    }
                });
    }

    /*
     *Inflate the view for item class we use as model
     */
    private void inflateViewForItem(Item item) {
        if (noOfImages == 0) {
            b.Heading.setVisibility(View.GONE);
        }

        CardItemBinding binding = CardItemBinding.inflate(getLayoutInflater());
        //Set image for app
        binding.imageView1.setImageBitmap(item.image);
        //Set labels for image
        binding.title.setText(item.label);
        //Get colors from palette and use in image
        binding.title.setBackgroundColor(item.color);

        b.list.addView(binding.getRoot());
        //Add Item
        Item newItem = new Item(item.color, item.label, item.url);

        if (listOfItems == null) {
            listOfItems = new ArrayList<Item>();
        }

        listOfItems.add(newItem);
        isAdd = true;
        noOfImages++;
    }
/*
*Save data when user goes in recent apps
 */
    @Override
    protected void onPause() {
        super.onPause();

        //Remove Item and save
        if (removeItem != null) {
            listOfItems.removeAll(removeItem);

            Gson gson = new Gson();
            String json = gson.toJson(listOfItems);

            getPreferences(MODE_PRIVATE).edit().putString("ITEMS", json).apply();

            finish();
        }
        
        if (isEdited || isAdd) {
            Gson gson = new Gson();
            String json = gson.toJson(listOfItems);
            getPreferences(MODE_PRIVATE).edit().putString("ITEMS", json).apply();
            isAdd = false;
            isEdited = false;
        }
    }
    /*
    *
    * Start on Activity for result for completion of code
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData()!=null){
            Uri imageUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            String uri = imageUri.toString();

            new AddImageFromGalleryDialog()
                    .show(this, uri, new AddImageFromGalleryDialog.OnCompleteListener() {
                @Override
                public void onAddCompleted(Item item) {
                    listOfItems.add(item);
                    inflateViewForItem(item);
                    b.Heading.setVisibility(View.GONE);

                }

                @Override
                public void onError(String error) {
                    new MaterialAlertDialogBuilder(MainActivity.this)
                            .setTitle("Error")
                            .show();


                }


            });



        }
    }
/*
    private void inflateColorChips(Set<Integer>colors) {
        for (int color : colors){
            ChipColorBinding binding = ChipColorBinding.inflate(getLayoutInflater());
            binding.getRoot().setChipBackgroundColor(ColorStateList.valueOf(color));
            b.colorChips.addView(binding.getRoot());
        }
    }
    private void inflateLabelChips(List<String> labels) {
        for (String label : labels){
            ChipLabelBinding binding = ChipLabelBinding.inflate(getLayoutInflater());
            binding.getRoot().setText(label);
            b.chip
        }
    }




    private void checkDialog() {
        DialogAddImageBinding binding = DialogAddImageBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(binding.getRoot())
                .show();
        binding.materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.inputDialogRoot.setVisibility(View.GONE);
                binding.fetcher.setVisibility(View.VISIBLE);

                new Handler()
                        .postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.fetcher.setVisibility(View.GONE);
                                binding.mainRoot.setVisibility(View.VISIBLE);
                            }
                        } ,2000);
            }
        });

        binding.AddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void refresh() {
        finish();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

   private void loadImage() {
        Glide.with(MainActivity.this)
                .asBitmap()
                .load("https://picsum.photos/1080")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        b.loader.setVisibility(View.GONE);
                        b.loadingImage.setText(R.string.loading_image_failed);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        b.loader.setVisibility(View.GONE);
                        b.loadingImage.setText(R.string.image_loaded);
                        b.ImageView.setImageBitmap(resource);
                        //labelImage(resource);
                        createPaletteAsync(resource);
                        return true;
                    }
                })
                .into(b.ImageView);
    }

    public void createPaletteAsync(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                new MaterialAlertDialogBuilder(MainActivity.this)
                        .setTitle("Labels Fetched!")
                        .setMessage(p.getSwatches().toString())
                        .show();
            }
        });
    }
 */
}
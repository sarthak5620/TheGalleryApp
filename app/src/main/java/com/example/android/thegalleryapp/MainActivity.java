package com.example.android.thegalleryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.example.android.thegalleryapp.databinding.ActivityMainBinding;
import com.example.android.thegalleryapp.databinding.CardItemBinding;
import com.example.android.thegalleryapp.models.Item;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding b ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
       // showCustomThemeDialog();
       // loadImage();
       // checkDialog();
    /*    new ItemHelper()
                .fetchData(1920, 1080, getApplicationContext(), new ItemHelper.OnCompleteListener() {
                    @Override
                    public void onFetched(Bitmap image, Set<Integer> colors, List<String> labels) {
                        b.ImageView.setImageBitmap(image);
                        inflateColorChips(colors);
                        inflateLabelChips(labels);
                    }

                    @Override
                    public void onError(String error) {
                        new MaterialAlertDialogBuilder(MainActivity.this)
                                .setTitle("Error")
                                .setMessage(error)
                                .show();
                    }
                });
        */
    }
    /*
    *Menu methods
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Validate item id
        if (item.getItemId() == R.id.AddImage) {
            //Call dialog
            showAddImageDialog();
            return true;
        }
        return false;
    }

    private void showAddImageDialog() {
        //Import object of class addImageDialog
        new AddImageDialog()
                //Call onComplete Listener and implement all methods of interface
                .show(this, new AddImageDialog.OnCompleteListener() {
                    @Override
                    public void OnImageAdded(Item item) {
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
        CardItemBinding binding = CardItemBinding.inflate(getLayoutInflater());
        //Set image for app
        binding.imageView1.setImageBitmap(item.image);
        //Set labels for image
        binding.title.setText(item.label);
        //Get colors from palette and use in image
        binding.title.setBackgroundColor(item.color);

        b.list.addView(binding.getRoot());
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
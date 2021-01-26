package com.droidninja.imageeditengine.views;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.droidninja.imageeditengine.ImageEditActivity;
import com.droidninja.imageeditengine.ImageEditor;
import com.droidninja.imageeditengine.R;
import com.droidninja.imageeditengine.utils.Utility;
import com.droidninja.imageeditengine.views.cropimage.CropImageView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.takusemba.cropme.CropLayout;
import com.takusemba.cropme.OnCropListener;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;

public class CropImg extends AppCompatActivity {

    String imagePath;
    CropLayout cropLayout;
    FloatingActionButton done_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_img);
        imagePath = getIntent().getStringExtra("imagePath");

        done_btn = findViewById(R.id.done_btn);
        cropLayout = findViewById(R.id.crop_view);
        cropLayout.setUri(Uri.parse(imagePath));
        cropLayout.addOnCropListener(new OnCropListener() {
            @Override
            public void onSuccess(@NotNull Bitmap bitmap) {

                ImageEditActivity.activity.finish();
                String path = Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_DCIM + "/" + System.currentTimeMillis() + ".jpg";
                String newpath=Utility.saveBitmap(bitmap, path);

//                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "" + System.currentTimeMillis(), null);
                try {
                    new ImageEditor.Builder(CropImg.this, newpath)
                            .setStickerAssets("stickers")
                            .open();

                } catch (Exception e) {
                    new ImageEditor.Builder(CropImg.this, newpath)
                            .setStickerAssets("stickers")
                            .open();
                }
                finish();

            }

            @Override
            public void onFailure(@NotNull Exception e) {
                Toast.makeText(CropImg.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropLayout.crop();
            }
        });
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj,
                null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

}
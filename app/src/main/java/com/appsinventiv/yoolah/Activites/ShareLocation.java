package com.appsinventiv.yoolah.Activites;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.appsinventiv.yoolah.Activites.UserManagement.LoginUser;
import com.appsinventiv.yoolah.Database.Word;
import com.appsinventiv.yoolah.Database.WordDao;
import com.appsinventiv.yoolah.Database.WordViewModel;
import com.appsinventiv.yoolah.Models.RoomModel;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.CompressImage;
import com.appsinventiv.yoolah.Utils.Constants;
import com.appsinventiv.yoolah.Utils.SharedPrefs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.droidninja.imageeditengine.ImageEditor;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;

public class ShareLocation extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private double lat;
    private double lon;

    Button share;
    private String compressedUrl;
    private RoomModel roomModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);
        share = findViewById(R.id.share);
        roomModel = (RoomModel) getIntent().getSerializableExtra("room");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("Share location");
        Intent i = new Intent(ShareLocation.this, GPSTrackerActivity.class);
//
        startActivityForResult(i, 1);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                createBitmap();
                mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {

                        Uri mapImagePath = getImageUri(bitmap);
                        Intent intent = new Intent();
                        intent.putExtra("Longitude", lon);
                        intent.putExtra("Latitude", lat);
                        intent.putExtra("mapImagePath", "" + mapImagePath);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });


            }
        });
    }


    private void uploadImage(Uri imageUri) {
//        CompressImage compressImage = new CompressImage(this);
//        compressedUrl = compressImage.compressImage(imageUri + "");
//        String messageText = lat + "," + lon;
//        Word myWordModel = new Word(
//                1, messageText,
//                Constants.MESSAGE_TYPE_LOCATION,
//                SharedPrefs.getUserModel().getName(), "",
//                SharedPrefs.getUserModel().getId(),
//                roomModel.getId(),
//                System.currentTimeMillis(),
//                "", "",
//                compressedUrl
//                , SharedPrefs.getUserModel().getPicUrl(), 0
//                , "",
//                roomModel.getCover_url(), roomModel.getTitle(), true, -1);
//        WordViewModel mWordViewModel;
//        mWordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);
//        mWordViewModel.insert(myWordModel);


//        uploadImageToServer();
    }


    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, System.currentTimeMillis() + "", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                lat = data.getDoubleExtra("Latitude", 0);
                lon = data.getDoubleExtra("Longitude", 0);
                // Add a marker in Sydney and move the camera
                LatLng sydney = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions()
                        .position(sydney)
                        .title("Your location"));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                LatLng coordinate = new LatLng(lat, lon);
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 15);
                mMap.animateCamera(yourLocation);

            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions()
//                .position(sydney)
//                .title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}

package com.appsinventiv.yoolah.Activites;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.appsinventiv.yoolah.Activites.UserManagement.LoginActivity;
import com.appsinventiv.yoolah.Activites.UserManagement.NameSignupOnly;
import com.appsinventiv.yoolah.Activites.UserManagement.RegisterActivity;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.Constants;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.google.zxing.Result;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission_group.CAMERA;


public class QrScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        getPermissions();


    }


    @Override
    public void onResume() {
        super.onResume();

        if (scannerView == null) {
            scannerView = new ZXingScannerView(this);
            setContentView(scannerView);
        }
        scannerView.setResultHandler(this);
        scannerView.startCamera();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(QrScanner.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        final String myResult = result.getText();
        Log.d("QRCodeScanner", result.getText());
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());
        if (myResult.contains("yoolah.com")) {
            if (myResult.contains("room")) {
                Constants.IS_QR = false;
                String roomId = myResult.substring(myResult.lastIndexOf("/") + 1);

                if (SharedPrefs.getUserModel() != null) {
                    Intent i = new Intent(QrScanner.this, AddUserToRoomWithRoomId.class);
                    i.putExtra("roomId", roomId);
                    startActivity(i);
                    finish();
                } else {
                    SharedPrefs.setRoomId(roomId);
                    startActivity(new Intent(QrScanner.this, NameSignupOnly.class));
                    finish();

                }
            } else {
                Constants.IS_QR = true;
                String qrId = myResult.substring(myResult.lastIndexOf("/") + 1);

                if (SharedPrefs.getUserModel() != null) {
                    Intent i = new Intent(QrScanner.this, AddUserToRoom.class);
                    i.putExtra("qrId", qrId);
                    startActivity(i);
                    finish();
                } else {
                    SharedPrefs.setQrId(qrId);
                    startActivity(new Intent(QrScanner.this, NameSignupOnly.class));
                    finish();

                }
            }
        } else {
            CommonUtils.showToast("Wrong qr code");
            finish();
        }


//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Scan Result");
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                scannerView.resumeCameraPreview(QrScanner.this);
//            }
//        });
//        builder.setNeutralButton("Visit", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(myResult));
//                startActivity(browserIntent);
//            }
//        });
//        builder.setMessage(result.getText());
//        AlertDialog alert1 = builder.create();
//        alert1.show();

    }

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.CAMERA,


        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
        }
    }


    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                } else {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
            }
        }
        return true;
    }


}


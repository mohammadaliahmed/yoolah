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
import com.appsinventiv.yoolah.Activites.UserManagement.LoginUser;
import com.appsinventiv.yoolah.Activites.UserManagement.NameSignupOnly;
import com.appsinventiv.yoolah.Activites.UserManagement.RegisterActivity;
import com.appsinventiv.yoolah.Models.UserModel;
import com.appsinventiv.yoolah.NetworkResponses.LoginResponse;
import com.appsinventiv.yoolah.NetworkResponses.RoomInfoResponse;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.Constants;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.appsinventiv.yoolah.Utils.UserClient;
import com.google.gson.JsonObject;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        if (myResult.contains("yoolah.")) {
            if (myResult.contains("mainRoom")) {
                Constants.IS_QR = false;
                String roomId = myResult.substring(myResult.lastIndexOf("/") + 1);

                if (SharedPrefs.getUserModel() != null) {
                    Intent i = new Intent(QrScanner.this, AddUserToRoomWithRoomId.class);
                    i.putExtra("roomId", roomId);
                    startActivity(i);
                    finish();
                } else {
//                    SharedPrefs.setRoomId(roomId);
//                    startActivity(new Intent(QrScanner.this, NameSignupOnly.class));
//                    finish();
                    callAdminLoginApi(roomId);

                }
            } else if (myResult.contains("user")) {
                String userId = myResult.substring(myResult.lastIndexOf("/") + 1);
                callLoginApi(userId);
            } else {
                Constants.IS_QR = true;
                String qrId = myResult.substring(myResult.lastIndexOf("/") + 1);
                checkStatusOfQR(qrId);

            }
        } else {
            CommonUtils.showToast("Wrong qr code");
            finish();
        }


    }

    private void checkStatusOfQR(String qrId) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("qr_id", qrId);
        Call<RoomInfoResponse> call = getResponse.checkQrStatus(map);
        SharedPrefs.setQrId("");
        SharedPrefs.setRoomId("");
        call.enqueue(new Callback<RoomInfoResponse>() {
            @Override
            public void onResponse(Call<RoomInfoResponse> call, Response<RoomInfoResponse> response) {
                if (response.code() == 200) {
                    int roomId = response.body().getRoomId();
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
                } else {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        CommonUtils.showToast(jObjError.getString("message"));
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RoomInfoResponse> call, Throwable t) {

            }
        });
    }

    private void callAdminLoginApi(String roomId) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", roomId);

        Call<LoginResponse> call = getResponse.loginAdmin(map);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (response.code() == 200) {
                    LoginResponse object = response.body();
                    if (object != null && object.getUser() != null) {
                        UserModel user = object.getUser();
                        CommonUtils.showToast("Logged in successfully");
                        SharedPrefs.setUserModel(user);
                        startActivity(new Intent(QrScanner.this, MainActivity.class));
                        finish();
                    } else {
                        CommonUtils.showToast(response.body().getMessage());
                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
            }
        });
    }

    private void callLoginApi(String userId) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("randomcode", userId);

        Call<LoginResponse> call = getResponse.loginWithId(map);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (response.code() == 200) {
                    LoginResponse object = response.body();
                    if (object != null && object.getUser() != null) {
                        UserModel user = object.getUser();
                        CommonUtils.showToast("Logged in successfully");
                        SharedPrefs.setUserModel(user);
                        startActivity(new Intent(QrScanner.this, MainActivity.class));
                        finish();
                    } else {
                        CommonUtils.showToast(response.body().getMessage());
                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
            }
        });
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


package com.appsinventiv.yoolah.Activites.UserManagement;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appsinventiv.yoolah.Activites.MainActivity;
import com.appsinventiv.yoolah.Models.UserModel;
import com.appsinventiv.yoolah.NetworkResponses.RoomInfoResponse;
import com.appsinventiv.yoolah.NetworkResponses.SignupResponse;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.DownloadFile;
import com.appsinventiv.yoolah.Utils.KeyboardUtils;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.appsinventiv.yoolah.Utils.UserClient;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NameSignupOnly extends AppCompatActivity {

    EditText name;
    String gender = "Male";
    Button signup;
    RelativeLayout wholeLayout;
    TextView login;

    LinearLayout afterArea;
    ImageView qrImage;
    TextView randomCode;
    Button continueTo;
    ImageView downloadQr;
    private String qrUrl;
    ImageView copyToClibboard;
    private ClipboardManager myClipboard;
    private UserModel user;
    TextView createPdf;
    private Bitmap image;

    TextView infoText;
    private File filePath;

    RelativeLayout asfdasd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_signup);
        getPermissions();
        name = findViewById(R.id.name);

        createPdf = findViewById(R.id.createPdf);
        signup = findViewById(R.id.signup);
        infoText = findViewById(R.id.infoText);
        wholeLayout = findViewById(R.id.wholeLayout);
        afterArea = findViewById(R.id.afterArea);
        qrImage = findViewById(R.id.qrImage);
        randomCode = findViewById(R.id.randomCode);
        asfdasd = findViewById(R.id.asfdasd);
        continueTo = findViewById(R.id.continueTo);
        downloadQr = findViewById(R.id.downloadQr);
        copyToClibboard = findViewById(R.id.copyToClipboard);

        copyToClibboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                try {
                    String text;

                    ClipData myClip = ClipData.newPlainText("randomCode", user.getRandomcode());
                    myClipboard.setPrimaryClip(myClip);
                    CommonUtils.showToast("Code Copied to clipboard");
                } catch (Exception e) {

                }
            }
        });
        createPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                Uri screenshotUri = Uri.parse("" + filePath);
                sharingIntent.setType("*/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                startActivity(Intent.createChooser(sharingIntent, "Share File using"));
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().length() == 0) {
                    name.setError("Enter name");
                } else {
                    singupNow();
                }


            }
        });

        downloadQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadFile.fromUrl(qrUrl, qrUrl);
            }
        });
        continueTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NameSignupOnly.this, MainActivity.class));
                finish();
            }
        });
    }

    private void createPDF() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();

        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();


        canvas.drawBitmap(image, 10, 50, null);

        paint.setColor(Color.BLACK);
        canvas.drawText("Your code: " + user.getRandomcode(), 80, 50, paint);

        //canvas.drawt
        // finish the page
        document.finishPage(page);
        File directory_path = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + "/yoolah/");
        if (!directory_path.exists()) {
            directory_path.mkdirs();
        }
        String targetPdf = directory_path + user.getRandomcode() + ".pdf";
        filePath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + "/yoolah/" + user.getRandomcode() + ".pdf");
        try {
            document.writeTo(new FileOutputStream(filePath));

        } catch (IOException e) {
            Log.e("main", "error " + e.toString());
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
    }


    private void singupNow() {
        String key = Long.toHexString(Double.doubleToLongBits(Math.random()));

        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);


        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("name", name.getText().toString());
        map.addProperty("username", key);
        map.addProperty("email", key + "@gmail.com");
        map.addProperty("password", key);
        map.addProperty("gender", gender);
        Call<SignupResponse> call = getResponse.register(map);

        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {

                    SignupResponse object = response.body();
                    if (object != null && object.getUser() != null) {
                        user = object.getUser();
                        name.setFocusable(false);
                        CommonUtils.showToast("Registration successful");
                        SharedPrefs.setUserModel(user);
                        createQRImg();
//                        startActivity(new Intent(NameSignupOnly.this, MainActivity.class));
//                        finish();
                        KeyboardUtils.forceCloseKeyboard(asfdasd);
                        afterArea.setVisibility(View.VISIBLE);
                        signup.setVisibility(View.GONE);
                        continueTo.setVisibility(View.VISIBLE);
                        qrUrl = AppConfig.BASE_URL_QR + user.getRandomcode() + "qrcode.png";
                        Glide.with(NameSignupOnly.this).load(AppConfig.BASE_URL_QR + user.getRandomcode() + "qrcode.png")
                                .into(qrImage);
                        randomCode.setText(user.getRandomcode());
                        infoText.setText("Dear " + user.getName() + ", welcome. please copy your ID safely which is needed later in case of login");


                    } else {
                        response.body().getMessage();
                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                wholeLayout.setVisibility(View.GONE);
                CommonUtils.showToast(t.getMessage());
            }
        });

    }

    private void createQRImg() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(AppConfig.BASE_URL_QR + user.getRandomcode() + "qrcode.png");

                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    createPDF();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();


    }

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,


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

                }
            }
        }
        return true;
    }

}

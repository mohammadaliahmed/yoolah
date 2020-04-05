package com.appsinventiv.yoolah.Activites.UserManagement;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.appsinventiv.yoolah.Activites.MainActivity;
import com.appsinventiv.yoolah.Activites.Splash;
import com.appsinventiv.yoolah.Models.MessageModel;
import com.appsinventiv.yoolah.Models.UserModel;
import com.appsinventiv.yoolah.NetworkResponses.NewMessageResponse;
import com.appsinventiv.yoolah.NetworkResponses.UserProfileResponse;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.CompressImage;
import com.appsinventiv.yoolah.Utils.CompressImageToThumbnail;
import com.appsinventiv.yoolah.Utils.Constants;
import com.appsinventiv.yoolah.Utils.GifSizeFilter;
import com.appsinventiv.yoolah.Utils.Glide4Engine;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.appsinventiv.yoolah.Utils.UserClient;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfile extends AppCompatActivity {
    private static final int REQUEST_CODE_CHOOSE = 23;

    RelativeLayout wholeLayout;
    EditText name, email, phone;
    RadioButton male, female;
    Button update, logout;
    String gender;
    CircleImageView image;
    private List<Uri> mSelected = new ArrayList<>();
    private String compressedUrl;
    private String liveUrl;
    private UserModel model;
    boolean ultaAccount=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        getPermissions();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("My profile");
        wholeLayout = findViewById(R.id.wholeLayout);
        logout = findViewById(R.id.logout);
        update = findViewById(R.id.update);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        image = findViewById(R.id.image);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefs.logout();
                Intent i = new Intent(MyProfile.this, Splash.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initMatisse();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().length() == 0) {
                    name.setError("Enter name");
                } else {
                    if (mSelected.size() > 0) {
                        uploadImageToServer();
                    } else {
                        updateProfile();
                    }
                }
            }
        });


        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()) {
                    if (b) {
                        gender = "Male";
                    }
                }
            }
        });
        female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()) {
                    if (b) {
                        gender = "Female";
                    }
                }
            }
        });

        getDataFromServer();


    }

    private void getDataFromServer() {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        Call<UserProfileResponse> call = getResponse.userProfile(map);
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {
                    model = response.body().getUserModel();
                    if (model != null) {
                        SharedPrefs.setUserModel(model);
                        name.setText(model.getName());
                        if (model.getEmail().startsWith("some")) {
                            email.setEnabled(true);
                            ultaAccount=true;
                        } else {
                            email.setText(model.getEmail());
                            email.setEnabled(false);

                        }
                        phone.setText(model.getPhone());
                        SharedPrefs.setUserModel(model);
                        Glide.with(MyProfile.this).load(AppConfig.BASE_URL_Image + model.getThumbnailUrl())
                                .placeholder(R.drawable.ic_profile_plc).into(image);

                        if (model.getGender() != null) {
                            if (model.getGender().equalsIgnoreCase("male")) {
                                male.setChecked(true);
                                gender = "Male";
                            } else if (model.getGender().equalsIgnoreCase("female")) {
                                female.setChecked(true);
                                gender = "Female";

                            }
                        } else {
                            male.setChecked(true);
                            gender = "Male";
                        }
                    }

                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {

                CommonUtils.showToast(t.getMessage());

            }
        });
    }

    private void initMatisse() {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(1)
                .showSingleMediaType(true)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new Glide4Engine())
                .forResult(REQUEST_CODE_CHOOSE);
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


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 23) {
            if (data != null) {
                mSelected = Matisse.obtainResult(data);
                CompressImage compressImage = new CompressImage(this);
                compressedUrl = compressImage.compressImage("" + mSelected.get(0));
                Glide.with(MyProfile.this).load(mSelected.get(0)).into(image);
//                uploadImageToServer();

            }

        }
    }

    private void uploadImageToServer() {
        wholeLayout.setVisibility(View.VISIBLE);
        CommonUtils.showToast("Uploading image...");
        // create upload service client
        File file = new File(compressedUrl);

        UserClient service = AppConfig.getRetrofit().create(UserClient.class);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("photo", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        // finally, execute the request
        Call<ResponseBody> call = service.uploadFile(fileToUpload, filename);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {

                    try {
                        liveUrl = response.body().string();
                        updateProfile();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    CommonUtils.showToast(response.body().getUrl());
                } else {
//                    CommonUtils.showToast(response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                CommonUtils.showToast(t.getMessage());
            }
        });


    }

    private void updateProfile() {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        if(ultaAccount) {
            map.addProperty("email", email.getText().toString());
        }
        map.addProperty("name", name.getText().toString());
        map.addProperty("gender", gender);
        map.addProperty("phone", phone.getText().toString());
        if (mSelected.size() > 0) {
            map.addProperty("thumbnailUrl", liveUrl);
        } else {
            map.addProperty("thumbnailUrl", model.getThumbnailUrl());

        }

        Call<UserProfileResponse> call = getResponse.updateProfile(map);
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                wholeLayout.setVisibility(View.GONE);

                if (response.code() == 200) {
                    CommonUtils.showToast("Profile Saved");
                    if (response.body().getUserModel() != null) {
                        SharedPrefs.setUserModel(response.body().getUserModel());
                    }


                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {

                CommonUtils.showToast(t.getMessage());

            }
        });

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
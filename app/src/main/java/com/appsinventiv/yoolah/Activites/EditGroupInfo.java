package com.appsinventiv.yoolah.Activites;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.yoolah.Adapters.ParticipantsAdapter;
import com.appsinventiv.yoolah.Models.UserModel;
import com.appsinventiv.yoolah.NetworkResponses.RoomInfoResponse;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.CompressImage;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.appsinventiv.yoolah.Utils.UserClient;
import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.gson.JsonObject;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditGroupInfo extends AppCompatActivity {
    private static final int REQUEST_CODE_CHOOSE = 23;

    RecyclerView recyclerview;
    TextView title, subtitle;
    ImageView back, image;

    int roomId;
    ParticipantsAdapter adapter;
    private List<UserModel> userList = new ArrayList<>();
    private ArrayList<String> mSelected = new ArrayList<>();
    private String compressedUrl;
    private String liveUrl;
    RelativeLayout wholeLayout;
    CardView leave;
    boolean left = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group_info);

        roomId = getIntent().getIntExtra("roomId", 0);
        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);
        back = findViewById(R.id.back);
        wholeLayout = findViewById(R.id.wholeLayout);
        leave = findViewById(R.id.leave);
        image = findViewById(R.id.image);


        recyclerview = findViewById(R.id.recyclerview);
        adapter = new ParticipantsAdapter(this, userList, false, new ParticipantsAdapter.ParticipantsAdapterCallbacks() {
            @Override
            public void onDelete(UserModel model) {
                showDeleteAlert("Do you want to remove this participant?", model);
            }
        });

        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerview.setAdapter(adapter);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getRoomInfo();
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initmatisse();
            }
        });

        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                left = true;
                showDeleteAlert("Leave group?", SharedPrefs.getUserModel());
            }
        });


    }

    private void showDeleteAlert(String msg, UserModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage(msg);

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteParticiapnt(model);

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteParticiapnt(UserModel model) {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("roomId", roomId);
        map.addProperty("userId", model.getId());
        Call<RoomInfoResponse> call = getResponse.removeParticipant(map);
        call.enqueue(new Callback<RoomInfoResponse>() {
            @Override
            public void onResponse(Call<RoomInfoResponse> call, Response<RoomInfoResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {
                    CommonUtils.showToast("Done");
                    if (left) {
                        Intent i = new Intent(EditGroupInfo.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    } else {
                        finish();
                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<RoomInfoResponse> call, Throwable t) {
                wholeLayout.setVisibility(View.GONE);
                CommonUtils.showToast(t.getMessage());

            }
        });
    }

    private void initmatisse() {
        Options options = Options.init()
                .setRequestCode(23)                                           //Request code for activity results
                .setCount(1)                                                   //Number of images to restict selection count
                .setExcludeVideos(true)                                       //Option to exclude videos
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                ;                                       //Custom Path For media Storage

        Pix.start(this, options);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 23) {
            mSelected = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            CompressImage compressImage = new CompressImage(this);
            compressedUrl = compressImage.compressImage(mSelected.get(0));
            Glide.with(EditGroupInfo.this).load(mSelected.get(0)).into(image);
            uploadImageToServer();

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
                        updateGroupPic();

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

    private void updateGroupPic() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        map.addProperty("roomId", roomId);
        map.addProperty("coverUrl", liveUrl);
        Call<ResponseBody> call = getResponse.updateCoverUrl(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {
                    CommonUtils.showToast("Updated cover");
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }

    private void getRoomInfo() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("roomId", roomId);
        map.addProperty("userId", SharedPrefs.getUserModel().getId());
        Call<RoomInfoResponse> call = getResponse.getRoomInfo(map);
        call.enqueue(new Callback<RoomInfoResponse>() {
            @Override
            public void onResponse(Call<RoomInfoResponse> call, Response<RoomInfoResponse> response) {
                if (response.code() == 200) {
                    RoomInfoResponse object = response.body();
                    if (object.getRoom() != null) {
                        try {
                            Glide.with(EditGroupInfo.this).load(AppConfig.BASE_URL_Image + object.getRoom().getCover_url()).placeholder(R.drawable.placeholder).into(image);
                        } catch (Exception e) {

                        }
                        title.setText(object.getRoom().getTitle());
                        subtitle.setText(object.getRoom().getSubtitle());
                        if (object.getUsers() != null && object.getUsers().size() > 0) {
                            adapter.setItemList(object.getUsers());
                        }
                        if (object.getRoom().getUserid() == SharedPrefs.getUserModel().getId()) {
                            adapter.setAdmin(true);
                        }

                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<RoomInfoResponse> call, Throwable t) {

            }
        });
    }
}




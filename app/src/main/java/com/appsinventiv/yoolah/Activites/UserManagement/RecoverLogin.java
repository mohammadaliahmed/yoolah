package com.appsinventiv.yoolah.Activites.UserManagement;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.appsinventiv.yoolah.Activites.MainActivity;
import com.appsinventiv.yoolah.Activites.QrScanner;
import com.appsinventiv.yoolah.Database.Word;
import com.appsinventiv.yoolah.Database.WordViewModel;
import com.appsinventiv.yoolah.Models.RoomModel;
import com.appsinventiv.yoolah.Models.UserModel;
import com.appsinventiv.yoolah.NetworkResponses.LoginResponse;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.Constants;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.appsinventiv.yoolah.Utils.UserClient;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecoverLogin extends AppCompatActivity {
    RelativeLayout wholeLayout;


    ImageView scan;
    EditText userId;
    ImageView go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_login);
        wholeLayout = findViewById(R.id.wholeLayout);
        scan = findViewById(R.id.scan);
        userId = findViewById(R.id.userId);
        go = findViewById(R.id.go);


        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callLoginApi(userId.getText().toString());
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RecoverLogin.this, QrScanner.class);
                startActivity(i);
            }
        });


    }

    private void callLoginApi(String userId) {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("randomcode", userId);

        Call<LoginResponse> call = getResponse.loginWithId(map);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {
                    LoginResponse object = response.body();
                    if (object != null && object.getUser() != null) {
                        UserModel user = object.getUser();
                        List<RoomModel> rooms = object.getRooms();
                        CommonUtils.showToast("Logged in successfully");
                        SharedPrefs.setUserModel(user);
                        if (rooms != null && rooms.size() > 0) {
                            insertMessage(rooms);
                        }

                        startActivity(new Intent(RecoverLogin.this, MainActivity.class));
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
                wholeLayout.setVisibility(View.GONE);
                CommonUtils.showToast(t.getMessage());
            }
        });
    }

    private void insertMessage(List<RoomModel> rooms) {
        WordViewModel mWordViewModel;
        mWordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);
        HashMap<Integer, RoomModel> map = new HashMap<>();
        for (RoomModel room : rooms) {
            Word myWordModel = new Word(
                    1,
                    "Group Created",
                    Constants.MESSAGE_TYPE_BUBBLE,
                    SharedPrefs.getUserModel().getName(),
                    "", SharedPrefs.getUserModel().getId(),
                    room.getId(),
                    System.currentTimeMillis(),
                    "", "", ""
                    , "", 0, "", room.getCover_url()
                    , room.getTitle(),
                    true, 0);
            mWordViewModel.insert(myWordModel);

            map.put(room.getId(), room);

        }
        SharedPrefs.setRoomDetails(map);


    }


}

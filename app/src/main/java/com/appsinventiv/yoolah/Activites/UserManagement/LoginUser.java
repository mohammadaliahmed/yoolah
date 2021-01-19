package com.appsinventiv.yoolah.Activites.UserManagement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.yoolah.Activites.AddUserToRoom;
import com.appsinventiv.yoolah.Activites.MainActivity;
import com.appsinventiv.yoolah.Activites.QrScanner;
import com.appsinventiv.yoolah.Database.Word;
import com.appsinventiv.yoolah.Database.WordViewModel;
import com.appsinventiv.yoolah.Models.RoomModel;
import com.appsinventiv.yoolah.Models.UserModel;
import com.appsinventiv.yoolah.NetworkResponses.ApiResponse;
import com.appsinventiv.yoolah.NetworkResponses.LoginResponse;
import com.appsinventiv.yoolah.NetworkResponses.RoomDetailsResponse;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.Constants;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.appsinventiv.yoolah.Utils.UserClient;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginUser extends AppCompatActivity {

    EditText email, password;
    RelativeLayout wholeLayout;
    Button login;


    ImageView scan;
    EditText groupId;
    ImageView go;
    public static LoginUser loginUserActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);
        loginUserActivity = this;
        groupId = findViewById(R.id.groupId);
        go = findViewById(R.id.go);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        scan = findViewById(R.id.scan);
        login = findViewById(R.id.login);
        wholeLayout = findViewById(R.id.wholeLayout);


        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupId.getText().length() == 0) {
                    groupId.setError("Cant be empty");
                } else {
                    callAddToGroupApi();
                }
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginUser.this, QrScanner.class);
                startActivity(i);
            }
        });

        wholeLayout = findViewById(R.id.wholeLayout);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginUser.this, RecoverLogin.class);
                startActivity(i);

            }
        });
    }

    private void callAddToGroupApi() {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("code", groupId.getText().toString());
        Call<ApiResponse> call = getResponse.getFirstTimeGroups(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                wholeLayout.setVisibility(View.GONE);

                if (response.code() == 200) {
                    if (response.body().getCode() == 200) {
                        List<RoomModel> rooms = response.body().getRooms();
                        RoomModel room = response.body().getRoom();
                        UserModel userModel = response.body().getUser();
                        if (userModel != null) {
                            SharedPrefs.setUserModel(userModel);
                            if (rooms != null && rooms.size() > 0) {
                                HashMap<Integer, RoomModel> roomsMap = new HashMap<>();
                                for (RoomModel roomm : rooms) {
                                    WordViewModel mWordViewModel;
                                    mWordViewModel = ViewModelProviders.of(LoginUser.this).get(WordViewModel.class);
                                    Word myWordModel = new Word(
                                            1,
                                            "Group Created",
                                            Constants.MESSAGE_TYPE_BUBBLE,
                                            SharedPrefs.getUserModel().getName(),
                                            "", SharedPrefs.getUserModel().getId(),
                                            roomm.getId(),
                                            System.currentTimeMillis(),
                                            "", "", ""
                                            , "", 0, "", roomm.getCover_url()
                                            , roomm.getTitle(),
                                            true, 0);
                                    mWordViewModel.insert(myWordModel);

                                    roomsMap.put(roomm.getId(), roomm);

                                }
                                SharedPrefs.setRoomDetails(roomsMap);
                                startActivity(new Intent(LoginUser.this, MainActivity.class));

                            }
                        } else {
                            if (room != null) {
                                SharedPrefs.setRoomId("" + room.getId());
                                startActivity(new Intent(LoginUser.this, NameSignupOnly.class));
                                finish();
                            }
                        }
                    } else {
                        CommonUtils.showToast(response.body().getMessage());
                    }


                } else if (response.code() == 404) {
                    CommonUtils.showToast("Wrong code");
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
                wholeLayout.setVisibility(View.GONE);

            }
        });


    }

    private void loginNow() {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<LoginResponse> call = getResponse.loginUser(
                AppConfig.API_USERNAME, AppConfig.API_PASSOWRD,
                email.getText().toString(),
                password.getText().toString()

        );
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                wholeLayout.setVisibility(View.GONE);

                if (response.code() == 200) {
                    LoginResponse object = response.body();
                    if (object != null && object.getUser() != null) {
                        UserModel user = object.getUser();
                        CommonUtils.showToast("Logged in");
                        SharedPrefs.setUserModel(user);
                        startActivity(new Intent(LoginUser.this, MainActivity.class));
                        finish();
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
}

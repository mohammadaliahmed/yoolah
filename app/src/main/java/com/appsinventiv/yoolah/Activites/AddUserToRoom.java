package com.appsinventiv.yoolah.Activites;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.appsinventiv.yoolah.Activites.UserManagement.LoginActivity;
import com.appsinventiv.yoolah.Activites.UserManagement.LoginUser;
import com.appsinventiv.yoolah.Database.Word;
import com.appsinventiv.yoolah.Database.WordViewModel;
import com.appsinventiv.yoolah.Models.RoomModel;
import com.appsinventiv.yoolah.NetworkResponses.RoomDetailsResponse;
import com.appsinventiv.yoolah.NetworkResponses.RoomInfoResponse;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.Constants;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.appsinventiv.yoolah.Utils.UserClient;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUserToRoom extends AppCompatActivity {


    String qrId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_to_room);
        qrId = getIntent().getStringExtra("qrId");
        addUserToRoom();
        if (LoginUser.loginUserActivity != null) {
            LoginUser.loginUserActivity.finish();
        }
    }

    private void addUserToRoom() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("qr_id", qrId);
        map.addProperty("user_id", SharedPrefs.getUserModel().getId());
        Call<RoomInfoResponse> call = getResponse.addUserToRoom(map);
        SharedPrefs.setQrId("");
        SharedPrefs.setRoomId("");
        call.enqueue(new Callback<RoomInfoResponse>() {
            @Override
            public void onResponse(Call<RoomInfoResponse> call, Response<RoomInfoResponse> response) {
                if (response.code() == 200) {
                    int roomId = response.body().getRoomId();
                    RoomModel room = response.body().getRoom();
                    Intent i = new Intent(AddUserToRoom.this, ChattingScreen.class);
                    insertRoomMesg(room);
                    i.putExtra("roomId", roomId);
                    startActivity(i);
                    finish();
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

    private void insertRoomMesg(RoomModel room) {
        WordViewModel mWordViewModel;
        mWordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);
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
                true);
        mWordViewModel.insert(myWordModel);

    }


}

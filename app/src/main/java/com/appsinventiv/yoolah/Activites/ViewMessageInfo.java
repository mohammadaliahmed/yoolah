package com.appsinventiv.yoolah.Activites;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.appsinventiv.yoolah.Activites.UserManagement.LoginUser;
import com.appsinventiv.yoolah.Adapters.ParticipantsAdapter;
import com.appsinventiv.yoolah.Database.Word;
import com.appsinventiv.yoolah.Models.UserModel;
import com.appsinventiv.yoolah.NetworkResponses.AllRoomMessagesResponse;
import com.appsinventiv.yoolah.NetworkResponses.MessageInfoResponse;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.Constants;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.appsinventiv.yoolah.Utils.UserClient;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewMessageInfo extends AppCompatActivity {


    RecyclerView recyclerview;
    int messageId, roomId;
    ParticipantsAdapter adapter;
    private List<UserModel> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_info);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("Message Info");
        recyclerview = findViewById(R.id.recyclerview);
        messageId = getIntent().getIntExtra("messageId", 0);
        roomId = getIntent().getIntExtra("roomId", 0);
        adapter = new ParticipantsAdapter(this, itemList, false, new ParticipantsAdapter.ParticipantsAdapterCallbacks() {
            @Override
            public void onDelete(UserModel model) {

            }
        });
        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerview.setAdapter(adapter);

        getDataFromServer();


    }

    private void getDataFromServer() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("messageId", messageId);
        map.addProperty("roomId", roomId);
        Call<MessageInfoResponse> call = getResponse.getReadMessages(map);
        call.enqueue(new Callback<MessageInfoResponse>() {
            @Override
            public void onResponse(Call<MessageInfoResponse> call, Response<MessageInfoResponse> response) {
                if (response.code() == 200) {
                    Word message = response.body().getMessageObject();
                    List<UserModel> users = response.body().getUsers();
                    if (users != null && users.size() > 0) {
                        adapter.setItemList(users);

                    }



                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<MessageInfoResponse> call, Throwable t) {

            }
        });
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

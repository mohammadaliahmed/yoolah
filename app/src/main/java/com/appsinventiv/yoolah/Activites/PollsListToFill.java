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

import com.appsinventiv.yoolah.Activites.UserManagement.LoginActivity;
import com.appsinventiv.yoolah.Adapters.PollToFillAdapter;
import com.appsinventiv.yoolah.Adapters.PollsListAdapter;
import com.appsinventiv.yoolah.Models.PollModel;
import com.appsinventiv.yoolah.NetworkResponses.PollAnswersResponse;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.CommonUtils;
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

public class PollsListToFill extends AppCompatActivity {


    RecyclerView recyclerview;
    PollToFillAdapter adapter;
    private List<PollModel> itemList = new ArrayList<>();
    int roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polls_to_fill);
        recyclerview = findViewById(R.id.recyclerview);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle(getResources().getString(R.string.list_of_polls));
        recyclerview = findViewById(R.id.recyclerview);
        adapter = new PollToFillAdapter(this, itemList);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerview.setAdapter(adapter);
        roomId = getIntent().getIntExtra("roomId", 0);


        getDataFromServer();


    }

    private void getDataFromServer() {
        itemList.clear();
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("userId", SharedPrefs.getUserModel().getId());
        map.addProperty("roomId", roomId);
        Call<PollAnswersResponse> call = getResponse.getAllPollsToFill(map);
        call.enqueue(new Callback<PollAnswersResponse>() {
            @Override
            public void onResponse(Call<PollAnswersResponse> call, Response<PollAnswersResponse> response) {
                if (response.code() == 200) {
                    List<PollModel> object = response.body().getPolls();
                    if (object != null && object.size() > 0) {
                        itemList = object;
                        adapter.setItemList(itemList);
                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<PollAnswersResponse> call, Throwable t) {

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

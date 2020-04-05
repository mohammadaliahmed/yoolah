package com.appsinventiv.yoolah.Activites;

import android.content.DialogInterface;
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
import android.widget.ImageView;

import com.appsinventiv.yoolah.Activites.UserManagement.LoginActivity;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListOfPolls extends AppCompatActivity {


    RecyclerView recyclerview;
    PollsListAdapter adapter;
    private List<PollModel> itemList = new ArrayList<>();
    int roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_polls);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("List of polls");
        recyclerview = findViewById(R.id.recyclerview);
        adapter = new PollsListAdapter(this, itemList, new PollsListAdapter.PollsAdapterCallback() {
            @Override
            public void onDeleteClicked(PollModel model) {
                showDeleteAlert(model);
            }
        });
        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerview.setAdapter(adapter);
        roomId = getIntent().getIntExtra("roomId", 0);

        getDataFromServer();


    }

    private void showDeleteAlert(PollModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Delete poll? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                callDeleteApi(model);
            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void callDeleteApi(PollModel model) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("pollId", model.getId());
        Call<ResponseBody> call = getResponse.deletePoll(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    CommonUtils.showToast("Poll Deleted");
                    finish();
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());

            }
        });

    }

    private void getDataFromServer() {
        itemList.clear();
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("userId", SharedPrefs.getUserModel().getId());
        map.addProperty("roomId", roomId);
        Call<PollAnswersResponse> call = getResponse.getAllPolls(map);
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
        getMenuInflater().inflate(R.menu.list_of_polls_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }
        int id = item.getItemId();

        if (id == R.id.action_add) {

            Intent i = new Intent(ListOfPolls.this, CreatePollActivity.class);
            i.putExtra("roomId", roomId);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }


}

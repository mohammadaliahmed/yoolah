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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.appsinventiv.yoolah.Activites.UserManagement.LoginActivity;
import com.appsinventiv.yoolah.Models.UserModel;
import com.appsinventiv.yoolah.NetworkResponses.AllRoomMessagesResponse;
import com.appsinventiv.yoolah.NetworkResponses.RoomInfoResponse;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.Constants;
import com.appsinventiv.yoolah.Utils.NotificationAsync;
import com.appsinventiv.yoolah.Utils.NotificationObserver;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.appsinventiv.yoolah.Utils.UserClient;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePollActivity extends AppCompatActivity implements NotificationObserver {

    EditText title, question, option1, option2, option3, option4;
    Button submit;

    RelativeLayout wholeLayout;
    int roomId;
    Button add;
    ImageView deleteOption4, deleteOption3;
    LinearLayout option3Layout, option4Layout;
    int counter = 0;
    private List<UserModel> particiapantsList = new ArrayList<>();
    private String pollId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("Create Poll");

        roomId = getIntent().getIntExtra("roomId", 0);
        deleteOption4 = findViewById(R.id.deleteOption4);
        deleteOption3 = findViewById(R.id.deleteOption3);
        option3Layout = findViewById(R.id.option3Layout);
        option4Layout = findViewById(R.id.option4Layout);
        title = findViewById(R.id.title);
        question = findViewById(R.id.question);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        wholeLayout = findViewById(R.id.wholeLayout);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        submit = findViewById(R.id.submit);
        add = findViewById(R.id.add);

        getRoomInfo();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (counter > 2) {
                    add.setVisibility(View.GONE);
                } else {
                    if (counter == 0) {
                        counter++;
                        option3Layout.setVisibility(View.VISIBLE);
                    } else if (counter == 1) {
                        counter++;
                        option4Layout.setVisibility(View.VISIBLE);
                        add.setVisibility(View.GONE);
                    }
                }

            }
        });

        deleteOption3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter--;
                option3Layout.setVisibility(View.GONE);
                add.setVisibility(View.VISIBLE);
            }
        });
        deleteOption4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter--;
                option4Layout.setVisibility(View.GONE);
                add.setVisibility(View.VISIBLE);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.getText().length() == 0) {
                    title.setError("Enter title");
                } else if (question.getText().length() == 0) {
                    question.setError("Enter question");
                } else if (option1.getText().length() == 0) {
                    option1.setError("Enter option 1 ");
                } else if (option2.getText().length() == 0) {
                    option2.setError("Enter option 2 ");
                } else {
                    showAlert();
                }
            }
        });


    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Submit Poll? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                submitPoll();
            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void submitPoll() {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("title", title.getText().toString());
        map.addProperty("question", question.getText().toString());
        map.addProperty("option1", option1.getText().toString());
        map.addProperty("option2", option2.getText().toString());
        map.addProperty("option3", option3.getText().toString());
        map.addProperty("option4", option4.getText().toString());
        map.addProperty("userid", SharedPrefs.getUserModel().getId());
        map.addProperty("roomId", roomId);
        Call<ResponseBody> call = getResponse.createPoll(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {
                    CommonUtils.showToast("Poll Submitted");
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.body().string());
                        pollId = jObjError.getString("pollId");
                        sendNotification();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    finish();
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
                wholeLayout.setVisibility(View.GONE);

            }
        });

    }

    private void sendNotification() {
        for (UserModel user : particiapantsList) {
            if (!user.getId().equals(SharedPrefs.getUserModel().getId())) {
                NotificationAsync notificationAsync = new NotificationAsync(CreatePollActivity.this);
                String NotificationTitle = "Admin Created new Poll ";
                String NotificationMessage = "Please fill the poll";
                notificationAsync.execute(
                        "ali",
                        user.getFcmKey(),
                        NotificationTitle,
                        NotificationMessage,
                        "poll", "" + pollId,""
                );
            }
        }
        finish();

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
                        if (object.getUsers() != null && object.getUsers().size() > 0) {
                            particiapantsList = object.getUsers();
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


    @Override
    public void onSuccess(String chatId) {

    }

    @Override
    public void onFailure() {

    }
}

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
import android.widget.TextView;

import com.appsinventiv.yoolah.Activites.UserManagement.LoginActivity;
import com.appsinventiv.yoolah.Models.PollAnswer;
import com.appsinventiv.yoolah.Models.PollModel;
import com.appsinventiv.yoolah.NetworkResponses.AllRoomMessagesResponse;
import com.appsinventiv.yoolah.NetworkResponses.GetPollResponse;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.appsinventiv.yoolah.Utils.UserClient;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FillPoll extends AppCompatActivity {

    TextView title, question, voteCount;
    TextView option1, option2, option3, option4;
    int pollId;
    private boolean answered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_poll);
        title = findViewById(R.id.title);
        question = findViewById(R.id.question);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        voteCount = findViewById(R.id.voteCount);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle(getResources().getString(R.string.fill_poll));
        pollId = getIntent().getIntExtra("pollId", 0);

        getDataFromServer();


        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert(1);
            }
        });
        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert(2);
            }
        });
        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert(3);
            }
        });
        option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert(4);
            }
        });


    }

    private void showAlert(int option) {
        if (!answered) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alert");
            builder.setMessage("Submit answer?");

            // add the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    submitAnswer(option);

                }
            });
            builder.setNegativeButton("Cancel", null);

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    private void submitAnswer(int option) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("pollId", pollId);
        map.addProperty("userId", SharedPrefs.getUserModel().getId());
        map.addProperty("option", option);
        Call<ResponseBody> call = getResponse.submitAnswer(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    CommonUtils.showToast("Answer submitted");
                    getDataFromServer();

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
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", pollId);
        map.addProperty("userId", SharedPrefs.getUserModel().getId());
        Call<GetPollResponse> call = getResponse.getPoll(map);

        call.enqueue(new Callback<GetPollResponse>() {
            @Override
            public void onResponse(Call<GetPollResponse> call, Response<GetPollResponse> response) {
                if (response.code() == 200) {
                    PollModel object = response.body().getPollModel();
                    if (object != null) {

                        int optionOneCount = 0;
                        int optionTwoCount = 0;
                        int optionThreeCount = 0;
                        int optionFourCount = 0;
                        for (PollAnswer answer : object.getAnswers()) {
                            if (answer.getOption() == 1) {
                                optionOneCount++;
                            } else if (answer.getOption() == 2) {
                                optionTwoCount++;
                            } else if (answer.getOption() == 3) {
                                optionThreeCount++;
                            } else if (answer.getOption() == 4) {
                                optionFourCount++;
                            }
                        }
                        title.setText(object.getTitle());
                        question.setText(object.getQuestion());

                        if (object.getOption1() != null) {
                            option1.setText(object.getOption1());
                        }
                        if (object.getOption2() != null) {
                            option2.setText(object.getOption2());
                        }
                        if (object.getOption3() != null) {
                            option3.setVisibility(View.VISIBLE);
                            option3.setText(object.getOption3());
                        }
                        if (object.getOption4() != null) {
                            option4.setVisibility(View.VISIBLE);
                            option4.setText(object.getOption4());
                        }
                        if (response.body().getPollAnswer() != null) {
                            PollAnswer answer = response.body().getPollAnswer();
                            answered = true;
                            voteCount.setVisibility(View.VISIBLE);
                            voteCount.setText(getResources().getString(R.string.total_votes)+": " + object.getAnswers().size());
                            if (answer.getOption() == 1) {
                                option1.setBackground(getResources().getDrawable(R.drawable.curved_corners_filled));
                                option1.setTextColor(getResources().getColor(R.color.colorWhite));
                            } else if (answer.getOption() == 2) {
                                option2.setBackground(getResources().getDrawable(R.drawable.curved_corners_filled));
                                option2.setTextColor(getResources().getColor(R.color.colorWhite));
                            } else if (answer.getOption() == 3) {
                                option3.setBackground(getResources().getDrawable(R.drawable.curved_corners_filled));
                                option3.setTextColor(getResources().getColor(R.color.colorWhite));
                            } else if (answer.getOption() == 4) {
                                option4.setBackground(getResources().getDrawable(R.drawable.curved_corners_filled));
                                option4.setTextColor(getResources().getColor(R.color.colorWhite));
                            }
                            option1.setText(object.getOption1() + "\n" + optionOneCount + " votes");
                            option2.setText(object.getOption2() + "\n" + optionTwoCount + " votes");
                            option3.setText(object.getOption3() + "\n" + optionThreeCount + " votes");
                            option4.setText(object.getOption4() + "\n" + optionFourCount + " votes");

                        }
                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<GetPollResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
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

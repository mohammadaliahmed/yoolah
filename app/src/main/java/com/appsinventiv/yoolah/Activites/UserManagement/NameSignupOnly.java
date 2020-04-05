package com.appsinventiv.yoolah.Activites.UserManagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.yoolah.Activites.MainActivity;
import com.appsinventiv.yoolah.Models.UserModel;
import com.appsinventiv.yoolah.NetworkResponses.RoomInfoResponse;
import com.appsinventiv.yoolah.NetworkResponses.SignupResponse;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.appsinventiv.yoolah.Utils.UserClient;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NameSignupOnly extends AppCompatActivity {

    EditText name;
    String gender = "Male";
    Button signup;
    RelativeLayout wholeLayout;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_signup);

        name = findViewById(R.id.name);

        signup = findViewById(R.id.signup);
        wholeLayout = findViewById(R.id.wholeLayout);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().length() == 0) {
                    name.setError("Enter name");
                } else {
                    singupNow();
                }


            }
        });
    }

    private void singupNow() {
        String key = "some" + Long.toHexString(Double.doubleToLongBits(Math.random()));
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);


        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("name", name.getText().toString());
        map.addProperty("username", key);
        map.addProperty("email", key + "@gmail.com");
        map.addProperty("password", key);
        map.addProperty("gender", gender);
        Call<SignupResponse> call = getResponse.register(map);

        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {

                    SignupResponse object = response.body();
                    if (object != null && object.getUser() != null) {
                        UserModel user = object.getUser();
                        CommonUtils.showToast("Registration successful");
                        SharedPrefs.setUserModel(user);
                        startActivity(new Intent(NameSignupOnly.this, MainActivity.class));
                        finish();


                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                wholeLayout.setVisibility(View.GONE);
                CommonUtils.showToast(t.getMessage());
            }
        });

    }
}

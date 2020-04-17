package com.appsinventiv.yoolah.Activites.UserManagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.appsinventiv.yoolah.Activites.MainActivity;
import com.appsinventiv.yoolah.Activites.QrScanner;
import com.appsinventiv.yoolah.Models.UserModel;
import com.appsinventiv.yoolah.NetworkResponses.LoginResponse;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.appsinventiv.yoolah.Utils.UserClient;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    RelativeLayout wholeLayout;
    TextView register;
    Button login;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setElevation(0);


        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);



        wholeLayout = findViewById(R.id.wholeLayout);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().length() == 0) {
                    email.setError("Enter email");
                } else if (password.getText().length() == 0) {
                    password.setError("Enter password");
                } else {
                    loginNow();
                }

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
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }else{
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
}

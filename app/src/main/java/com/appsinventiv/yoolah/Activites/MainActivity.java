package com.appsinventiv.yoolah.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.appsinventiv.yoolah.Activites.UserManagement.LoginActivity;
import com.appsinventiv.yoolah.Activites.UserManagement.MyProfile;
import com.appsinventiv.yoolah.Adapters.ChatListAdapter;
import com.appsinventiv.yoolah.Models.MessageModel;
import com.appsinventiv.yoolah.Models.UserMessages;
import com.appsinventiv.yoolah.Models.UserModel;
import com.appsinventiv.yoolah.NetworkResponses.LoginResponse;
import com.appsinventiv.yoolah.NetworkResponses.UserMessagesResponse;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.Constants;
import com.appsinventiv.yoolah.Utils.MyFirebaseMessagingService;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.appsinventiv.yoolah.Utils.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recycler;
    ChatListAdapter adapter;
    private List<UserMessages> itemList = new ArrayList<>();

    TextView complete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("newMsg"));


        if (getSupportActionBar() != null) {

            getSupportActionBar().setElevation(0);
        }
        complete = findViewById(R.id.complete);
        recycler = findViewById(R.id.recyclerview);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        HashMap<Integer, UserMessages> map = SharedPrefs.getHomeMessages();
        if (map != null && map.size() > 0) {
            itemList = new ArrayList<>(map.values());
        }
        sortMessages();

        adapter = new ChatListAdapter(this
                , itemList);
        recycler.setAdapter(adapter);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isComplete()) {
                    String token = task.getResult().getToken();
                    updateFcmKey(token);

                }
            }
        });
        if (!Constants.IS_QR) {
            if (!SharedPrefs.getRoomId().equalsIgnoreCase("")) {
                Intent i = new Intent(MainActivity.this, AddUserToRoomWithRoomId.class);
                i.putExtra("roomId", SharedPrefs.getRoomId());
                startActivity(i);
                finish();
            }
        } else {
            if (!SharedPrefs.getQrId().equalsIgnoreCase("")) {
                Intent i = new Intent(MainActivity.this, AddUserToRoom.class);
                i.putExtra("qrId", SharedPrefs.getQrId());
                startActivity(i);
                finish();
            }
        }

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyProfile.class));
            }
        });
//        getDataFromServer();
    }

    private void updateFcmKey(String token) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        map.addProperty("fcmKey", token);

        Call<LoginResponse> call = getResponse.updateFcmKey(map);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.code() == 200) {
                    LoginResponse object = response.body();

                    if (object != null && object.getUser() != null) {
                        UserModel user = object.getUser();
                        SharedPrefs.setUserModel(user);
                    }

                } else {
//                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SharedPrefs.getUserModel().getEmail().startsWith("some")) {
            complete.setVisibility(View.VISIBLE);
        } else {
            complete.setVisibility(View.GONE);

        }
        getDataFromServer();
    }

    private void getDataFromServer() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        Call<UserMessagesResponse> call = getResponse.userMessages(map);
        call.enqueue(new Callback<UserMessagesResponse>() {
            @Override
            public void onResponse(Call<UserMessagesResponse> call, Response<UserMessagesResponse> response) {
                if (response.isSuccessful()) {

                    UserMessagesResponse object = response.body();
                    if (object != null) {
                        itemList.clear();
                        if (object.getMessages() != null && object.getMessages().size() > 0) {
                            itemList = object.getMessages();

                        }

                        HashMap<Integer, UserMessages> map = new HashMap<>();
                        for (UserMessages messages : itemList) {
                            map.put(messages.getRoomId(), messages);
                        }
                        SharedPrefs.setHomeMessages(map);
//                        for (UserMessages messages : itemList) {
//                            HashMap<Integer, Boolean> seenMap = SharedPrefs.getLastSeenMessage(messages.getRoomId());
//                            if (seenMap != null && seenMap.size() > 0) {
////                                seenMap.put(messages.getRoomId(), true);
////                                SharedPrefs.setLastSeenMessage(seenMap, messages.getRoomId());
//                            } else {
//
//                                seenMap = new HashMap<>();
//                                seenMap.put(messages.getRoomId(), false);
//                                SharedPrefs.setLastSeenMessage(seenMap, messages.getRoomId());
//                            }
////
//                        }
                        adapter.updateList(itemList);

                        sortMessages();
                    }

                }
            }

            @Override
            public void onFailure(Call<UserMessagesResponse> call, Throwable t) {
//                CommonUtils.showToast(t.getMessage());
            }
        });

    }

    private void sortMessages() {

        Collections.sort(itemList, new Comparator<UserMessages>() {
            @Override
            public int compare(UserMessages listData, UserMessages t1) {
                Long ob1 = listData.getTime();
                Long ob2 = t1.getTime();

                return ob2.compareTo(ob1);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, MyProfile.class));
            return true;
        }
        if (id == R.id.action_scan) {
            startActivity(new Intent(MainActivity.this, QrScanner.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            getDataFromServer();
        }
    };

}

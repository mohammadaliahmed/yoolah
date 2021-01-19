package com.appsinventiv.yoolah.Activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appsinventiv.yoolah.Activites.UserManagement.LoginActivity;
import com.appsinventiv.yoolah.Activites.UserManagement.LoginUser;
import com.appsinventiv.yoolah.Activites.UserManagement.MyProfile;
import com.appsinventiv.yoolah.Activites.UserManagement.NameSignupOnly;
import com.appsinventiv.yoolah.Adapters.ChatListAdapter;
import com.appsinventiv.yoolah.Adapters.WordChatListAdapter;
import com.appsinventiv.yoolah.Database.Word;
import com.appsinventiv.yoolah.Database.WordDao;
import com.appsinventiv.yoolah.Database.WordViewModel;
import com.appsinventiv.yoolah.Models.MessageModel;
import com.appsinventiv.yoolah.Models.RoomModel;
import com.appsinventiv.yoolah.Models.UserMessages;
import com.appsinventiv.yoolah.Models.UserModel;
import com.appsinventiv.yoolah.NetworkResponses.ApiResponse;
import com.appsinventiv.yoolah.NetworkResponses.LoginResponse;
import com.appsinventiv.yoolah.NetworkResponses.RoomDetailsResponse;
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
    WordChatListAdapter adapter;
    private List<Word> itemList = new ArrayList<>();

    TextView complete;
    private WordViewModel mWordViewModel;
    TextView servertype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        servertype=findViewById(R.id.servertype);
        if(AppConfig.BASE_URL.contains("com")){
            servertype.setVisibility(View.GONE);
        }else{
            servertype.setVisibility(View.VISIBLE);
        }
        mWordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);
        mWordViewModel.getUserWords().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(@Nullable List<Word> words) {
                adapter.setItemList(words);
                getRoomsFromServer();

            }

        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("newMsg"));


        if (getSupportActionBar() != null) {

            getSupportActionBar().setElevation(0);
        }
        complete = findViewById(R.id.complete);
        recycler = findViewById(R.id.recyclerview);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

//        HashMap<Integer, UserMessages> map = SharedPrefs.getHomeMessages();
//        if (map != null && map.size() > 0) {
//            itemList = new ArrayList<>(map.values());
//        }
        sortMessages();


        adapter = new WordChatListAdapter(this
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

    private void getRoomsFromServer() {

        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
//
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        Call<ApiResponse> call = getResponse.roomList(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getRooms() != null && response.body().getRooms().size() > 0) {
                        HashMap<Integer, RoomModel> map1 = new HashMap<>();
                        for (RoomModel roomModel : response.body().getRooms()) {
                            map1.put(roomModel.getId(), roomModel);
                        }
                        SharedPrefs.setRoomDetails(map1);
                        adapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
//                CommonUtils.showToast(t.getMessage());
            }
        });
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
        if (SharedPrefs.getUserModel() != null) {
            if (SharedPrefs.getUserModel().getEmail().startsWith("some")) {
                complete.setVisibility(View.VISIBLE);
            } else {
                complete.setVisibility(View.GONE);

            }
        }
        getDataFromServer();
    }

    private void getDataFromServer() {
//        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
//
//        JsonObject map = new JsonObject();
//        map.addProperty("api_username", AppConfig.API_USERNAME);
//        map.addProperty("api_password", AppConfig.API_PASSOWRD);
//        map.addProperty("id", SharedPrefs.getUserModel().getId());
//        Call<UserMessagesResponse> call = getResponse.userMessages(map);
//        call.enqueue(new Callback<UserMessagesResponse>() {
//            @Override
//            public void onResponse(Call<UserMessagesResponse> call, Response<UserMessagesResponse> response) {
//                if (response.isSuccessful()) {
//
//                    UserMessagesResponse object = response.body();
//                    if (object != null) {
//                        itemList.clear();
//                        if (object.getMessages() != null && object.getMessages().size() > 0) {
//                            itemList = object.getMessages();
//
//                        }
//
//                        HashMap<Integer, UserMessages> map = new HashMap<>();
//                        for (UserMessages messages : itemList) {
//                            map.put(messages.getRoomId(), messages);
//                        }
//                        SharedPrefs.setHomeMessages(map);
////                        for (UserMessages messages : itemList) {
////                            HashMap<Integer, Boolean> seenMap = SharedPrefs.getLastSeenMessage(messages.getRoomId());
////                            if (seenMap != null && seenMap.size() > 0) {
//////                                seenMap.put(messages.getRoomId(), true);
//////                                SharedPrefs.setLastSeenMessage(seenMap, messages.getRoomId());
////                            } else {
////
////                                seenMap = new HashMap<>();
////                                seenMap.put(messages.getRoomId(), false);
////                                SharedPrefs.setLastSeenMessage(seenMap, messages.getRoomId());
////                            }
//////
////                        }
//                        adapter.updateList(itemList);
//
//                        sortMessages();
//                    }
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UserMessagesResponse> call, Throwable t) {
////                CommonUtils.showToast(t.getMessage());
//            }
//        });

    }

    private void sortMessages() {

//        Collections.sort(itemList, new Comparator<UserMessages>() {
//            @Override
//            public int compare(UserMessages listData, UserMessages t1) {
//                Long ob1 = listData.getTime();
//                Long ob2 = t1.getTime();
//
//                return ob2.compareTo(ob1);
//
//            }
//        });
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
//            startActivity(new Intent(MainActivity.this, QrScanner.class));
            showOptionPopup();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showOptionPopup() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_dialog_options, null);

        dialog.setContentView(layout);

        ImageView scan = layout.findViewById(R.id.scan);
        EditText groupId = layout.findViewById(R.id.groupId);
        Button enter = layout.findViewById(R.id.enter);
        ProgressBar progress = layout.findViewById(R.id.progress);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, QrScanner.class));
            }
        });
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupId.getText().length() == 0) {
                    groupId.setError("Enter Group id");
                } else {
                    progress.setVisibility(View.VISIBLE);
                    callAddToGroupApi(groupId.getText().toString(), progress, dialog);
                }
            }
        });


        dialog.show();

    }

    private void callAddToGroupApi(String groupId, ProgressBar progress, Dialog dialog) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("code", groupId);
        Call<ApiResponse> call = getResponse.getRoomDetailsFromID(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progress.setVisibility(View.INVISIBLE);
                if (response.code() == 200) {
                    RoomModel room = response.body().getRoom();
                    UserModel user = response.body().getUser();
                    if (user != null) {
                        dialog.dismiss();
                        WordViewModel mWordViewModel;
                        mWordViewModel = ViewModelProviders.of(MainActivity.this).get(WordViewModel.class);
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
                                true, 0);
                        mWordViewModel.insert(myWordModel);
                    } else {
                        dialog.dismiss();
                        if (room != null) {
                            Intent i = new Intent(MainActivity.this, AddUserToRoom.class);
                            i.putExtra("qrId", "" + groupId);
                            startActivity(i);


                        }
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

            }
        });


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

package com.appsinventiv.yoolah.Activites;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.yoolah.Adapters.WordMessagesAdapter;
import com.appsinventiv.yoolah.Database.Word;
import com.appsinventiv.yoolah.Database.WordRepository;
import com.appsinventiv.yoolah.Database.WordViewModel;
import com.appsinventiv.yoolah.Models.UserModel;
import com.appsinventiv.yoolah.NetworkResponses.AllRoomMessagesResponse;
import com.appsinventiv.yoolah.NetworkResponses.RoomInfoResponse;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.ApplicationClass;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.CompressImage;
import com.appsinventiv.yoolah.Utils.Constants;
import com.appsinventiv.yoolah.Utils.KeyboardUtils;
import com.appsinventiv.yoolah.Utils.NotificationAsync;
import com.appsinventiv.yoolah.Utils.NotificationObserver;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.appsinventiv.yoolah.Utils.UserClient;
import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.droidninja.imageeditengine.ImageEditor;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shain.messenger.MessageSwipeController;
import com.shain.messenger.SwipeControllerActions;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChattingScreen extends AppCompatActivity implements NotificationObserver {

    private static final int REQUEST_CODE_CHOOSE_VIDEO = 24;
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    boolean isAttachAreaVisible = false;

    Integer roomId;
    String name;
    EditText message;
    ImageView send;
    RecyclerView recycler;
    WordMessagesAdapter adapter;
    private List<Word> itemList = new ArrayList<>();
    private static final int REQUEST_CODE_FILE = 25;

    private static final int REQUEST_CODE_CHOOSE = 23;
    private String compressedUrl;
    private String liveUrl;
    CircleImageView image;
    TextView groupName;
    ImageView back;

    RecordView recordView;
    RecordButton recordButton;
    private static String mFileName = null;

    private MediaRecorder mRecorder = null;

    private MediaPlayer mPlayer = null;
    RelativeLayout recordingArea;
    RelativeLayout messagingArea;
    String recordingLocalUrl;
    CardView attachArea;
    long recordingTime = 0L;
    ImageView attach, pickCamera;
    LinearLayout pickDocument, pickVideo, pickImg, pickLocation, pickContact;
    private String docfileName;
    List<UserModel> particiapantsList = new ArrayList<>();
    public static HashMap<Integer, UserModel> participantsMap = new HashMap<>();
    private String messageText;

    ImageView addParticipant, createPoll;
    private RoomInfoResponse object;
    RelativeLayout fillPoll;

    RelativeLayout bottomArea, cannotSend;
    String picture;
    private String liveUrlVideoPic;
    private String imageFilePath;
    private String videoPath;
    private File thumbFilename;
    private String documentType;
    Calendar calendar;
    private MessageSwipeController swipeController;
    private WordViewModel mWordViewModel;
    private Word myWordModel;
    private double lat, lon;
    private Integer oldMessageId = 0;
    private boolean replyShowing;
    CardView replyLayout;
    private ImageView replyImage;
    private TextView replyOldText;
    private ImageView close;
    private ArrayList<String> mSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_screen);
        calendar = Calendar.getInstance();
        getPermissions();
        roomId = getIntent().getIntExtra("roomId", 0);
        mWordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);

        close = findViewById(R.id.close);
        replyOldText = findViewById(R.id.replyOldText);
        pickLocation = findViewById(R.id.pickLocation);
        pickContact = findViewById(R.id.pickContact);
        bottomArea = findViewById(R.id.bottomArea);
        replyImage = findViewById(R.id.replyImage);
        cannotSend = findViewById(R.id.cannotSend);
        image = findViewById(R.id.image);
        fillPoll = findViewById(R.id.fillPoll);
        replyLayout = findViewById(R.id.replyLayout);
        groupName = findViewById(R.id.groupName);
        pickCamera = findViewById(R.id.pickCamera);
        attachArea = findViewById(R.id.attachArea);
        back = findViewById(R.id.back);
        pickDocument = findViewById(R.id.pickDocument);
        attach = findViewById(R.id.attach);
        recycler = findViewById(R.id.recycler);
        recordView = (RecordView) findViewById(R.id.record_view);
        recordButton = (RecordButton) findViewById(R.id.record_button);
        addParticipant = findViewById(R.id.addParticipant);
        createPoll = findViewById(R.id.createPoll);
        pickImg = findViewById(R.id.pickImg);
        pickVideo = findViewById(R.id.pickVideo);
        recordingArea = findViewById(R.id.recordingArea);
        messagingArea = findViewById(R.id.messageArea);
        initRecording();


        fillPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChattingScreen.this, PollsListToFill.class);
                i.putExtra("roomId", roomId);
                startActivity(i);
            }
        });

        name = getIntent().getStringExtra("name");
        picture = getIntent().getStringExtra("picture");


        try {
            Glide.with(ChattingScreen.this).load(AppConfig.BASE_URL_Image + picture).into(image);

        } catch (Exception e) {

        }
        groupName.setText(name);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replyShowing = false;
                replyLayout.setVisibility(View.GONE);
            }
        });


        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        swipeController = new MessageSwipeController(this, new SwipeControllerActions() {
            @Override
            public void showReplyUI(int position) {
                showReplyLayout(position);
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recycler);
//        HashMap<Integer, List<Word>> maap = SharedPrefs.getInsideMessages(roomId);

//        if (maap != null && maap.size() > 0) {
//            List<Word> list = maap.get(roomId);
//            if (list != null && list.size() > 0) {
//                addBubble(list);
////                itemList = list;
//                recycler.scrollToPosition(itemList.size() - 1);
//
//            }
//
//        }

        adapter = new WordMessagesAdapter(this, itemList, new WordMessagesAdapter.MessagesCallback() {
            @Override
            public void onDelete(Word messageModel, int position) {
                showDeleteAlert(messageModel, position);
            }

            @Override
            public void onUpdateMessage(Word messageModel) {
                mWordViewModel.updateWord(messageModel);
            }

            @Override
            public void onReplyMessageClick(Word messageModel) {
                for (int i = 0; i < itemList.size(); i++) {
                    if (itemList.get(i).getServerId().equals(messageModel.getOldId())) {
//                        int pos=itemList.indexOf(i);
                        recycler.scrollToPosition(i);
                    }

                }
            }
        });
        recycler.setAdapter(adapter);
        mWordViewModel.getAllWords(roomId).observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(@Nullable List<Word> words) {
                adapter.setItemList(words);
                itemList = words;
                recycler.scrollToPosition(itemList.size() - 1);
            }

        });
        getRoomDataFromDb();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        pickContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAttachAreaVisible) {
                    attachArea.setVisibility(View.GONE);
                    isAttachAreaVisible = false;
                } else {
                    attachArea.setVisibility(View.VISIBLE);
                    isAttachAreaVisible = true;
                }
                Intent i = new Intent(ChattingScreen.this, PhoneContacts.class);
                startActivityForResult(i, 50);
            }
        });
        pickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAttachAreaVisible) {
                    attachArea.setVisibility(View.GONE);
                    isAttachAreaVisible = false;
                } else {
                    attachArea.setVisibility(View.VISIBLE);
                    isAttachAreaVisible = true;
                }
//                Intent i = new Intent(ChattingScreen.this, GPSTrackerActivity.class);
//
//                startActivityForResult(i, 1);
                try {
                    Intent i = new Intent(ChattingScreen.this, ShareLocation.class);
                    i.putExtra("room", object.getRoom());
                    startActivityForResult(i, 1);
                } catch (Exception e) {
                    CommonUtils.showToast("Getting location..");
                }


            }
        });


        pickCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                openCameraAndTakePic();
                initMatisse();
            }
        });

        pickImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAttachAreaVisible) {
                    attachArea.setVisibility(View.GONE);
                    isAttachAreaVisible = false;
                } else {
                    attachArea.setVisibility(View.VISIBLE);
                    isAttachAreaVisible = true;
                }
                initMatisse();
            }
        });
        pickVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAttachAreaVisible) {
                    attachArea.setVisibility(View.GONE);
                    isAttachAreaVisible = false;
                } else {
                    attachArea.setVisibility(View.VISIBLE);
                    isAttachAreaVisible = true;
                }

            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAttachAreaVisible) {
                    attachArea.setVisibility(View.GONE);
                    isAttachAreaVisible = false;
                } else {
                    attachArea.setVisibility(View.VISIBLE);
                    isAttachAreaVisible = true;
                }
            }
        });


        pickDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachArea.setVisibility(View.GONE);
                isAttachAreaVisible = false;
                openFile(REQUEST_CODE_FILE);
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChattingScreen.this, EditGroupInfo.class);
                i.putExtra("roomId", roomId);
                startActivity(i);
            }
        });
        groupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChattingScreen.this, EditGroupInfo.class);
                i.putExtra("roomId", roomId);
                startActivity(i);
            }
        });

//        CommonUtils.showToast("" + roomId);

//        image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(ChattingScreen.this, ViewProfile.class);
//                i.putExtra("userId", hisUserId);
//                startActivity(i);
//            }
//        });
//        chatterName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(ChattingScreen.this, ViewProfile.class);
//                i.putExtra("userId", hisUserId);
//                startActivity(i);
//            }
//        });

        send = findViewById(R.id.send);
        message = findViewById(R.id.message);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getText().length() == 0) {
                    message.setError("Cant send empty message");
                } else {
                    messageText = message.getText().toString();
                    inserDateBubble();
                    myWordModel = new Word(
                            1,
                            messageText,
                            replyShowing ? Constants.MESSAGE_TYPE_REPLY : Constants.MESSAGE_TYPE_TEXT,
                            SharedPrefs.getUserModel().getName(),
                            "", SharedPrefs.getUserModel().getId(),
                            roomId,
                            System.currentTimeMillis(),
                            "", "", ""
                            , "", 0, "", object.getRoom().getCover_url()
                            , object.getRoom().getTitle(),
                            true, oldMessageId);
                    mWordViewModel.insert(myWordModel);

                    message.setText("");

                    sendMessage(replyShowing ? Constants.MESSAGE_TYPE_REPLY : Constants.MESSAGE_TYPE_TEXT);
                    replyLayout.setVisibility(View.GONE);
                    replyShowing = false;
                }
            }
        });

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    recordingArea.setVisibility(View.VISIBLE);
                    send.setVisibility(View.GONE);


                } else {
                    recordingArea.setVisibility(View.GONE);
                    send.setVisibility(View.VISIBLE);
                }
            }
        });

        addParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert();
            }
        });

        createPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChattingScreen.this, ListOfPolls.class);
                i.putExtra("roomId", roomId);
                startActivity(i);
            }
        });


    }

    private void showReplyLayout(int position) {
        KeyboardUtils.toggleKeyboardVisibility(this);
        message.requestFocus();
        oldMessageId = itemList.get(position).getServerId();
        replyShowing = true;
        replyLayout.setVisibility(View.VISIBLE);
        if (itemList.get(position).getMessageType().equalsIgnoreCase(Constants.MESSAGE_TYPE_IMAGE)) {
            replyImage.setVisibility(View.VISIBLE);
            replyOldText.setText("Photo");
            Glide.with(this).load(AppConfig.BASE_URL_Image + itemList.get(position).getImageUrl()).into(replyImage);
        } else {
            replyImage.setVisibility(View.GONE);
            replyOldText.setText(itemList.get(position).getMessageText());
        }
    }

    private void inserDateBubble() {

        if (!SharedPrefs.getLastDate(roomId).equals(CommonUtils.getDate(System.currentTimeMillis()))) {
            myWordModel = new Word(
                    1,
                    CommonUtils.getDate(System.currentTimeMillis()),
                    Constants.MESSAGE_TYPE_BUBBLE,
                    SharedPrefs.getUserModel().getName(),
                    "", SharedPrefs.getUserModel().getId(),
                    roomId,
                    System.currentTimeMillis(),
                    "", "", ""
                    , "", 0, "", object.getRoom().getCover_url()
                    , object.getRoom().getTitle(),
                    true, oldMessageId);
            mWordViewModel.insert(myWordModel);
            SharedPrefs.setLastDate(CommonUtils.getDate(System.currentTimeMillis()), roomId);
        }

    }

    private void openCameraAndTakePic() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.appsinventiv.yoolah.provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                startActivityForResult(pictureIntent,
                        REQUEST_CAPTURE_IMAGE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();

        return image;
    }


    private void markAsRead() {
        try {
            Word mod = itemList.get(itemList.size() - 1);
            mod.setMessageRead(true);
            mWordViewModel.updateWord(mod);

            if (mod.getMessageById() == object.getRoom().getUserid()) {
                markAsReadOnServer(mod);

            }

        } catch (
                Exception e) {

        }


    }

    private void markAsReadOnServer(Word mod) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("messageId", mod.getServerId());
        map.addProperty("userId", SharedPrefs.getUserModel().getId());
        map.addProperty("roomId", roomId);
        Call<ResponseBody> call = getResponse.markAsReadOnServer(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    private void showDeleteAlert(Word messageModel, int position) {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_dialog_delete, null);

        dialog.setContentView(layout);

        View deleteView = layout.findViewById(R.id.deleteView);
        TextView showInfo = layout.findViewById(R.id.showInfo);
        TextView copy = layout.findViewById(R.id.copy);
        TextView cancel = layout.findViewById(R.id.cancel);
        TextView delete = layout.findViewById(R.id.delete);
        TextView deleteForEveryOne = layout.findViewById(R.id.deleteForEveryOne);
        if (messageModel.getMessageType().equalsIgnoreCase(Constants.MESSAGE_TYPE_DELETED)) {
            deleteForEveryOne.setVisibility(View.GONE);
            deleteView.setVisibility(View.GONE);
        } else {
            if (messageModel.getMessageById().equals(SharedPrefs.getUserModel().getId())) {
                if (messageModel.getMessageById().equals(object.getRoom().getUserid())) {
                    showInfo.setVisibility(View.VISIBLE);
                }
                if (messageModel.getTime() > (System.currentTimeMillis() - 25200000l)) {
                    deleteForEveryOne.setVisibility(View.VISIBLE);
                    deleteView.setVisibility(View.VISIBLE);
                } else {
                    deleteForEveryOne.setVisibility(View.GONE);
                    deleteView.setVisibility(View.GONE);
                }
            } else {
                deleteForEveryOne.setVisibility(View.GONE);
                deleteView.setVisibility(View.GONE);
            }
        }

        showInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent i = new Intent(ChattingScreen.this, ViewMessageInfo.class);
                i.putExtra("messageId", messageModel.getServerId());
                i.putExtra("roomId", roomId);
                startActivity(i);
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                String text;
                ClipData myClip = ClipData.newPlainText("message", messageModel.getMessageText());
                myClipboard.setPrimaryClip(myClip);
                CommonUtils.showToast("Copied to clipboard");
            }
        });
        deleteForEveryOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                callDeleteApi(messageModel, position);

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWordViewModel.deleteWord(messageModel);
                dialog.dismiss();

            }
        });


        dialog.show();

    }

    private void callDeleteApi(Word messageModel, int position) {
//        itemList.remove(position);
        Word msg = itemList.get(position);
        msg.setMessageType(Constants.MESSAGE_TYPE_DELETED);
        adapter.notifyItemChanged(position);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", messageModel.getId());
        Call<AllRoomMessagesResponse> call = getResponse.deleteMessageFroAll(map);
        call.enqueue(new Callback<AllRoomMessagesResponse>() {
            @Override
            public void onResponse(Call<AllRoomMessagesResponse> call, Response<AllRoomMessagesResponse> response) {
                if (response.code() == 200) {

//                    handleMessagesList(response);
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<AllRoomMessagesResponse> call, Throwable t) {

            }
        });

    }

    private void showAlert() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_dialog_add_participant, null);

        dialog.setContentView(layout);

        Button send = layout.findViewById(R.id.send);
        EditText email = layout.findViewById(R.id.email);
        ProgressBar mailProgress = layout.findViewById(R.id.mailProgress);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().length() == 0) {
                    email.setError("Enter email");
                } else {
                    mailProgress.setVisibility(View.VISIBLE);
                    callEmailAPi(email.getText().toString(), dialog);
                }
//                String html = "Please click on the link below to view the qr code\n\n" + "http://yoolah.acnure.com/viewqr/" + roomId;
//
//
//                final Intent shareIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
//                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Invititation to join Yoolah group");
//                shareIntent.putExtra(
//                        Intent.EXTRA_TEXT,
//                        html
//                );
//
//                startActivity(shareIntent);
//                dialog.dismiss();
            }
        });
        dialog.show();


    }

    private void callEmailAPi(String email, Dialog dialog) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("roomId", roomId);
        map.addProperty("email", email);
        Call<ResponseBody> call = getResponse.inviteUserFromApp(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();
                if (response.code() == 200) {
                    CommonUtils.showToast("Email sent");
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }

    private void openFile(Integer CODE) {
//        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//        i.setType("*/*");
//        startActivityForResult(i, CODE);
        Intent intent4 = new Intent(this, NormalFilePickActivity.class);
        intent4.putExtra(Constant.MAX_NUMBER, 1);
        intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
        startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);

    }

    private void initRecording() {
        recordButton.setRecordView(recordView);
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                Log.d("RecordView", "onStart");
                mRecorder = null;
                startRecording();


            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                Log.d("RecordView", "onCancel");
                mRecorder.release();
                mRecorder = null;
//                setMargins(recycler, 0, 0, 0, 0);


            }

            @Override
            public void onFinish(long recordTime) {
                //Stop Recording..
                String time = CommonUtils.getFormattedDate(recordTime);
                Log.d("RecordView", "onFinish");

//                setMargins(recycler, 0, 0, 0, 0);

                Log.d("RecordTime", time);
                recordingTime = recordTime;
                messagingArea.setVisibility(View.VISIBLE);
                stopRecording();
                recycler.scrollToPosition(itemList.size() - 1);


            }

            @Override
            public void onLessThanSecond() {

                //When the record time is less than One Second
                Log.d("RecordView", "onLessThanSecond");
//                setMargins(recycler, 0, 0, 0, 0);

                messagingArea.setVisibility(View.VISIBLE);
                mRecorder = null;
                recycler.scrollToPosition(itemList.size() - 1);


            }
        });


        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
//                setMargins(recycler, 0, 0, 0, 170);

                Log.d("RecordView", "Basket Animation Finished");
                messagingArea.setVisibility(View.VISIBLE);
//                setMargins(recycler, 0, 0, 0, 0);


                recycler.scrollToPosition(itemList.size() - 1);


            }
        });
        recordView.setSoundEnabled(true);
    }

    private void startRecording() {
        messagingArea.setVisibility(View.GONE);

        recordingLocalUrl = Long.toHexString(Double.doubleToLongBits(Math.random()));
        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mRecorder.setOutputFile(mFileName + recordingLocalUrl + ".mp3");
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);


                try {
                    mRecorder.prepare();
                    mRecorder.start();
                } catch (IOException e) {
//                    Log.e(LOG_TAG, "prepare() failed");
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    messagingArea.setVisibility(View.VISIBLE);

                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    messagingArea.setVisibility(View.VISIBLE);

                }

            }
        }, 100);


    }

    private void stopRecording() {
        try {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            myWordModel = new Word(
                    1,
                    "",
                    Constants.MESSAGE_TYPE_AUDIO,
                    SharedPrefs.getUserModel().getName(),
                    "",
                    SharedPrefs.getUserModel().getId(),
                    roomId,
                    System.currentTimeMillis(),
                    mFileName + recordingLocalUrl + ".mp3", "", ""
                    , SharedPrefs.getUserModel().getPicUrl(), recordingTime
                    , "",
                    object.getRoom().getCover_url(), object.getRoom().getTitle(), true, oldMessageId);
            mWordViewModel.insert(myWordModel);
            uploadAudioToServer(mFileName + recordingLocalUrl + ".mp3");
        } catch (NullPointerException e) {

        } finally {
//            mRecorder.stop();
//            mRecorder.release();
//            mRecorder = null;
        }

    }


    private void initMatisse() {
        Options options = Options.init()
                .setRequestCode(23)                                           //Request code for activity results
                .setCount(1)                                                   //Number of images to restict selection count
                .setExcludeVideos(true)                                       //Option to exclude videos
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                ;                                       //Custom Path For media Storage

        Pix.start(ChattingScreen.this, options);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                lat = data.getDoubleExtra("Latitude", 0);
                lon = data.getDoubleExtra("Longitude", 0);
                String mapImagePath = data.getStringExtra("mapImagePath");
                CompressImage compressImage = new CompressImage(this);
                compressedUrl = compressImage.compressImage(mapImagePath);
                messageText = lat + "," + lon;
                myWordModel = new Word(
                        1, messageText,
                        Constants.MESSAGE_TYPE_LOCATION,
                        SharedPrefs.getUserModel().getName(), "",
                        SharedPrefs.getUserModel().getId(),
                        roomId,
                        System.currentTimeMillis(),
                        "", videoPath,
                        ""
                        , SharedPrefs.getUserModel().getPicUrl(), 0
                        , "",
                        object.getRoom().getCover_url(), object.getRoom().getTitle(), true, oldMessageId);
                mWordViewModel.insert(myWordModel);
                uploadImageToServer(Constants.MESSAGE_TYPE_LOCATION);
//                sendMessage(Constants.MESSAGE_TYPE_LOCATION);
//                showLocationShareAlert();

            }
        }
        if (requestCode == 50 && resultCode == RESULT_OK) {
            if (data != null) {
                String name = data.getStringExtra("name");
                String number = data.getStringExtra("number");
                messageText = name + "\n" + number;
                myWordModel = new Word(
                        1, messageText,
                        Constants.MESSAGE_TYPE_CONTACT,
                        SharedPrefs.getUserModel().getName(), "",
                        SharedPrefs.getUserModel().getId(),
                        roomId,
                        System.currentTimeMillis(),
                        "", videoPath,
                        ""
                        , SharedPrefs.getUserModel().getPicUrl(), 0
                        , "",
                        object.getRoom().getCover_url(), object.getRoom().getTitle(), true, oldMessageId);
                mWordViewModel.insert(myWordModel);
                sendMessage(Constants.MESSAGE_TYPE_CONTACT);

            }


        }

        if (requestCode == Constant.REQUEST_CODE_PICK_FILE && data != null) {
//            Uri Fpath = data.getData();
            ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
            if (list.size() > 0) {
                String path = list.get(0).getPath();
                docfileName = list.get(0).getName();
                uploadDocumentToServer(path);
            }


        }
        if (requestCode == 52) {
            String imagePath = data.getStringExtra(ImageEditor.EXTRA_EDITED_PATH);
            CompressImage compressImage = new CompressImage(this);
            compressedUrl = compressImage.compressImage(imagePath);
            myWordModel = new Word(
                    1, "",
                    Constants.MESSAGE_TYPE_IMAGE,
                    SharedPrefs.getUserModel().getName(), "",
                    SharedPrefs.getUserModel().getId(),
                    roomId,
                    System.currentTimeMillis(),
                    "", "",
                    compressedUrl
                    , SharedPrefs.getUserModel().getPicUrl(), 0
                    , "",
                    object.getRoom().getCover_url(), object.getRoom().getTitle(), true, oldMessageId);
            mWordViewModel.insert(myWordModel);


            uploadImageToServer(Constants.MESSAGE_TYPE_IMAGE);

        }
        if (resultCode == Activity.RESULT_OK && requestCode == 23) {

            try {

                mSelected = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
//                Intent i = new Intent(ChattingScreen.this, ViewAndSendImage.class);
//                i.putExtra("url", mSelected.get(0));
//                startActivity(i);

                try {
                    new ImageEditor.Builder(ChattingScreen.this, mSelected.get(0))
                            .setStickerAssets("stickers")
                            .open();
                } catch (Exception e) {
                    new ImageEditor.Builder(ChattingScreen.this, mSelected.get(0))
                            .setStickerAssets("stickers")
                            .open();
                }


//                CompressImage compressImage = new CompressImage(this);
//                compressedUrl = compressImage.compressImage(mSelected.get(0));
//                myWordModel = new Word(
//                        1, "",
//                        Constants.MESSAGE_TYPE_IMAGE,
//                        SharedPrefs.getUserModel().getName(), "",
//                        SharedPrefs.getUserModel().getId(),
//                        roomId,
//                        System.currentTimeMillis(),
//                        "", "",
//                        mSelected.get(0)
//                        , SharedPrefs.getUserModel().getPicUrl(), 0
//                        , "",
//                        object.getRoom().getCover_url(), object.getRoom().getTitle(), true, oldMessageId);
//                mWordViewModel.insert(myWordModel);
//
//
//                uploadImageToServer();
            } catch (Exception e) {
                CommonUtils.showToast(e.getMessage());
            }

        }
    }

    private void showLocationShareAlert() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Location Alert");
//        builder.setMessage("Share location with group members? ");
//
//        // add the buttons
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
        messageText = lat + "," + lon;
        myWordModel = new Word(
                1, messageText,
                Constants.MESSAGE_TYPE_LOCATION,
                SharedPrefs.getUserModel().getName(), "",
                SharedPrefs.getUserModel().getId(),
                roomId,
                System.currentTimeMillis(),
                "", videoPath,
                ""
                , SharedPrefs.getUserModel().getPicUrl(), 0
                , "",
                object.getRoom().getCover_url(), object.getRoom().getTitle(), true, oldMessageId);
        mWordViewModel.insert(myWordModel);
        sendMessage(Constants.MESSAGE_TYPE_LOCATION);

//            }
//        });
//        builder.setNegativeButton("Cancel", null);
//
//        // create and show the alert dialog
//        AlertDialog dialog = builder.create();
//        dialog.show();
    }


    public ChattingScreen() {

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/r";
    }

    private void uploadVideoToServer(String url) {

        // create upload service client
        File file = new File(url);

        UserClient service = AppConfig.getRetrofit().create(UserClient.class);

        RequestBody requestBody = RequestBody.create(MediaType.parse("video/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("video", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        // finally, execute the request
        Call<ResponseBody> call = service.uploadVideoFile(fileToUpload, filename);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {

                    try {
                        liveUrl = response.body().string();
//                        sendMessage(Constants.MESSAGE_TYPE_VIDEO);

//                        uploadVideoImageToServer();
                        sendMessage(Constants.MESSAGE_TYPE_VIDEO);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    CommonUtils.showToast(response.body().getUrl());
                } else {
//                    CommonUtils.showToast(response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                CommonUtils.showToast("video: " + t.getMessage());
            }
        });


    }

    private void uploadImageToServer(String type) {

        // create upload service client
        File file = new File(compressedUrl);

        UserClient service = AppConfig.getRetrofit().create(UserClient.class);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("photo", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        // finally, execute the request
        Call<ResponseBody> call = service.uploadFile(fileToUpload, filename);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {

                    try {
                        liveUrl = response.body().string();
                        sendMessage(type);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    CommonUtils.showToast(response.body().getUrl());
                } else {
//                    CommonUtils.showToast(response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

//                CommonUtils.showToast(t.getMessage());
            }
        });


    }

    private void uploadVideoImageToServer() {

        // create upload service client
        File file = new File("" + thumbFilename.getAbsolutePath());
        UserClient service = AppConfig.getRetrofit().create(UserClient.class);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("photo", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        // finally, execute the request
        Call<ResponseBody> call = service.uploadFile(fileToUpload, filename);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {

                    try {
                        liveUrlVideoPic = response.body().string();
                        sendMessage(Constants.MESSAGE_TYPE_VIDEO);
                        if (thumbFilename.exists()) {
                            thumbFilename.delete();
                            thumbFilename = null;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    CommonUtils.showToast(response.body().getUrl());
                } else {
//                    CommonUtils.showToast(response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                CommonUtils.showToast("pic: " + t.getMessage());

            }
        });


    }

    private void uploadAudioToServer(String audioFile) {
        // create upload service client
        File file = new File(audioFile);


        UserClient service = AppConfig.getRetrofit().create(UserClient.class);

        RequestBody requestBody = RequestBody.create(MediaType.parse("audio/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("audio", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        // finally, execute the request
        Call<ResponseBody> call = service.uploadAudioFile(fileToUpload, filename);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {

                    try {
                        liveUrl = response.body().string();
                        sendMessage(Constants.MESSAGE_TYPE_AUDIO);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    CommonUtils.showToast(response.body().getUrl());
                } else {
//                    CommonUtils.showToast(response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

//                CommonUtils.showToast(t.getMessage());
            }
        });


    }

    private void uploadDocumentToServer(String documetnFile) {
        File file = new File(documetnFile);
        documentType = CommonUtils.getMimeType(file, this);
        myWordModel = new Word(
                1, "",
                Constants.MESSAGE_TYPE_DOCUMENT,
                SharedPrefs.getUserModel().getName(),
                documetnFile,
                SharedPrefs.getUserModel().getId(),
                roomId,
                System.currentTimeMillis(),
                "", "",
                ""
                , SharedPrefs.getUserModel().getPicUrl(), 0
                , docfileName + documentType,
                object.getRoom().getCover_url(), object.getRoom().getTitle(), true, oldMessageId);
        mWordViewModel.insert(myWordModel);
        UserClient service = AppConfig.getRetrofit().create(UserClient.class);

        RequestBody requestBody = RequestBody.create(MediaType.parse("document/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("document", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        RequestBody extension = RequestBody.create(MediaType.parse("text/plain"), documentType);

        // finally, execute the request
        Call<ResponseBody> call = service.uploadDocumentFile(fileToUpload, filename, extension);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        liveUrl = response.body().string();
                        sendMessage(Constants.MESSAGE_TYPE_DOCUMENT);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    CommonUtils.showToast(response.body().getUrl());
                } else {
//                    CommonUtils.showToast(response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                CommonUtils.showToast(t.getMessage());
//                CommonUtils.showToast(t.getMessage());
            }
        });


    }
//


    private void getRoomDataFromDb() {
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

                    object = response.body();
                    markAsRead();

                    try {
                        Glide.with(ChattingScreen.this).load(AppConfig.BASE_URL_Image + object.getRoom().getCover_url()).placeholder(R.drawable.team).into(image);

                    } catch (Exception e) {

                    }
                    if (object.getRoom().getUserid() == SharedPrefs.getUserModel().getId()) {
                        addParticipant.setVisibility(View.VISIBLE);
                        createPoll.setVisibility(View.VISIBLE);
                    } else {
                        createPoll.setVisibility(View.GONE);
                        addParticipant.setVisibility(View.GONE);
                    }
                    groupName.setText(object.getRoom().getTitle());
                    if (object.getUsers() != null && object.getUsers().size() > 0) {
                        particiapantsList = object.getUsers();
                        for (UserModel user : particiapantsList) {
                            participantsMap.put(user.getId(), user);
                        }
                        adapter.setItemList(itemList);

                    }
                    if (object.getCanMessage() == 1) {
                        bottomArea.setVisibility(View.VISIBLE);
                        cannotSend.setVisibility(View.GONE);
                    } else {
                        cannotSend.setVisibility(View.VISIBLE);
                        bottomArea.setVisibility(View.GONE);
                    }

                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<RoomInfoResponse> call, Throwable t) {
//                CommonUtils.showToast(t.getMessage());
            }
        });
    }


    private void sendMessage(String messageType) {

        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("messageText", messageText);
        map.addProperty("messageType", messageType);
        if (messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_IMAGE)) {
            map.addProperty("imageUrl", liveUrl);

        } else if (messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_IMAGE)) {
            map.addProperty("imageUrl", liveUrl);

        } else if (messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_AUDIO)) {
            map.addProperty("audioUrl", liveUrl);
            map.addProperty("mediaTime", recordingTime);

        } else if (messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_VIDEO)) {
            map.addProperty("videoUrl", liveUrl);
            map.addProperty("imageUrl", liveUrlVideoPic);

        } else if (messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_DOCUMENT)) {
            map.addProperty("documentUrl", liveUrl);
            map.addProperty("filename", docfileName + documentType);


        }

        map.addProperty("messageByName", SharedPrefs.getUserModel().getName());
        map.addProperty("messageById", SharedPrefs.getUserModel().getId());
        map.addProperty("roomId", roomId);
        map.addProperty("oldId", oldMessageId);
        map.addProperty("messageByPicUrl", SharedPrefs.getUserModel().getThumbnailUrl());
        map.addProperty("time", "" + System.currentTimeMillis());
        Call<AllRoomMessagesResponse> call = getResponse.createMessage(map);
        call.enqueue(new Callback<AllRoomMessagesResponse>() {
            @Override
            public void onResponse(Call<AllRoomMessagesResponse> call, Response<AllRoomMessagesResponse> response) {
                if (response.code() == 200) {
                    message.setText("");
//                    handleMessagesList(response);
//                    myWordModel = response.body().getMessageModel();
                    myWordModel.setId(itemList.get(itemList.size() - 1).getId());
                    myWordModel.setServerId(response.body().getMessageModel().getId());
                    myWordModel.setGroupPicUrl(object.getRoom().getCover_url());
                    myWordModel.setRoomName(object.getRoom().getTitle());
                    myWordModel.setAudioUrl(liveUrl);
                    myWordModel.setImageUrl(liveUrl);
                    myWordModel.setDocumentUrl(liveUrl);
                    myWordModel.setVideoUrl(liveUrl);
                    myWordModel.setFilename(docfileName + documentType);
                    WordRepository mRepository = new WordRepository(ApplicationClass.getInstance());
                    mRepository.updateWord(myWordModel);
//                    mWordViewModel.updateWord(myWordModel);
                    sendNotification(messageType, myWordModel);
                }
            }

            @Override
            public void onFailure(Call<AllRoomMessagesResponse> call, Throwable t) {


            }
        });

    }

    private void sendNotification(String type, Word msgModel) {
        for (UserModel user : particiapantsList) {
            if (!user.getId().equals(SharedPrefs.getUserModel().getId())) {

                NotificationAsync notificationAsync = new NotificationAsync(ChattingScreen.this);
                String NotificationTitle = "New message in " + object.getRoom().getTitle();
                String NotificationMessage = "";
                if (type.equals(Constants.MESSAGE_TYPE_TEXT)) {
                    NotificationMessage = SharedPrefs.getUserModel().getName() + ": " + messageText;
                } else if (type.equals(Constants.MESSAGE_TYPE_REPLY)) {
                    NotificationMessage = SharedPrefs.getUserModel().getName() + ": " + messageText;
                } else if (type.equals(Constants.MESSAGE_TYPE_IMAGE)) {
                    NotificationMessage = SharedPrefs.getUserModel().getName() + ": \uD83D\uDCF7 Image";
                } else if (type.equals(Constants.MESSAGE_TYPE_AUDIO)) {
                    NotificationMessage = SharedPrefs.getUserModel().getName() + ": \uD83C\uDFB5 Audio";
                } else if (type.equals(Constants.MESSAGE_TYPE_DOCUMENT)) {
                    NotificationMessage = SharedPrefs.getUserModel().getName() + ": \uD83D\uDCC4 Document";
                } else if (type.equals(Constants.MESSAGE_TYPE_STICKER)) {
                    NotificationMessage = SharedPrefs.getUserModel().getName() + ": \uD83D\uDD37 Sticker";
                } else if (type.equals(Constants.MESSAGE_TYPE_VIDEO)) {
                    NotificationMessage = SharedPrefs.getUserModel().getName() + ": \uD83D\uDCFD Video";
                } else if (type.equals(Constants.MESSAGE_TYPE_TRANSLATED)) {
                    NotificationMessage = SharedPrefs.getUserModel().getName() + ": \uD83C\uDE02 Translation";
                } else if (type.equals(Constants.MESSAGE_TYPE_LOCATION)) {
                    NotificationMessage = SharedPrefs.getUserModel().getName() + ": \ud83d\udccd Location";
                } else if (type.equals(Constants.MESSAGE_TYPE_CONTACT)) {
                    NotificationMessage = SharedPrefs.getUserModel().getName() + ": ☎ Contact";
                } else if (type.equals(Constants.MESSAGE_TYPE_POST)) {
                    NotificationMessage = SharedPrefs.getUserModel().getName() + ":  \uD83D\uDCF7 Post";
                }
                Gson gson = new Gson();

                notificationAsync.execute(
                        "ali",
                        user.getFcmKey(),
                        NotificationTitle,
                        NotificationMessage,
                        "chat", "" + roomId, gson.toJson(msgModel)
                );
            }
        }

    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,


        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
        }
    }


    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                } else {

                }
            }
        }
        return true;
    }

    @Override
    public void onSuccess(String chatId) {
//        CommonUtils.showToast("Sent ");
    }

    @Override
    public void onFailure() {

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ChattingScreen.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}

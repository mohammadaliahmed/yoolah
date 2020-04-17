package com.appsinventiv.yoolah.Activites;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.yoolah.Adapters.MessagesAdapter;
import com.appsinventiv.yoolah.Models.MessageModel;
import com.appsinventiv.yoolah.Models.UserMessages;
import com.appsinventiv.yoolah.Models.UserModel;
import com.appsinventiv.yoolah.NetworkResponses.AllRoomMessagesResponse;
import com.appsinventiv.yoolah.NetworkResponses.NewMessageResponse;
import com.appsinventiv.yoolah.NetworkResponses.RoomDetailsResponse;
import com.appsinventiv.yoolah.NetworkResponses.RoomInfoResponse;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.CompressImage;
import com.appsinventiv.yoolah.Utils.Constants;
import com.appsinventiv.yoolah.Utils.FileUtils;
import com.appsinventiv.yoolah.Utils.GifSizeFilter;
import com.appsinventiv.yoolah.Utils.Glide4Engine;
import com.appsinventiv.yoolah.Utils.NotificationAsync;
import com.appsinventiv.yoolah.Utils.NotificationObserver;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.appsinventiv.yoolah.Utils.UserClient;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.gson.JsonObject;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
    MessagesAdapter adapter;
    private List<MessageModel> itemList = new ArrayList<>();
    private static final int REQUEST_CODE_FILE = 25;

    private static final int REQUEST_CODE_CHOOSE = 23;
    private List<Uri> mSelected = new ArrayList<>();
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
    ImageView attach, pickCamera, pickDocument, pickVideo, pickImg;
    private String docfileName;
    private List<UserModel> particiapantsList = new ArrayList<>();
    private String messageText;

    ImageView addParticipant, createPoll;
    private RoomInfoResponse object;
    RelativeLayout fillPoll;

    RelativeLayout bottomArea, cannotSend;
    String picture;
    private String liveUrlVideoPic;
    private String imageFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_screen);
        LocalBroadcastManager.getInstance(ChattingScreen.this).registerReceiver(mMessageReceiver,
                new IntentFilter("newMsg"));

        getPermissions();

        bottomArea = findViewById(R.id.bottomArea);
        cannotSend = findViewById(R.id.cannotSend);
        image = findViewById(R.id.image);
        fillPoll = findViewById(R.id.fillPoll);
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

        roomId = getIntent().getIntExtra("roomId", 0);
        name = getIntent().getStringExtra("name");
        picture = getIntent().getStringExtra("picture");

        markAsRead();


        try {
            Glide.with(ChattingScreen.this).load(AppConfig.BASE_URL_Image + picture).into(image);

        } catch (Exception e) {

        }
        groupName.setText(name);


        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        HashMap<Integer, List<MessageModel>> maap = SharedPrefs.getInsideMessages(roomId);

        if (maap != null && maap.size() > 0) {
            List<MessageModel> list = maap.get(roomId);
            if (list != null && list.size() > 0) {
                itemList = list;
                recycler.scrollToPosition(itemList.size() - 1);

            }

        }
        adapter = new MessagesAdapter(this, itemList, new MessagesAdapter.MessagesCallback() {
            @Override
            public void onDelete(MessageModel messageModel, int position) {
                showDeleteAlert(messageModel, position);
            }
        });
        recycler.setAdapter(adapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pickCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openCameraAndTakePic();
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
                initVideoMatisse();
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

        getRoomDataFromDb();

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

                    MessageModel messageModel = new MessageModel(itemList.size() + 1, messageText,
                            Constants.MESSAGE_TYPE_TEXT,
                            SharedPrefs.getUserModel().getId(),
                            roomId, System.currentTimeMillis(), false, ""
                    );
                    itemList.add(messageModel);
                    adapter.setItemList(itemList);
                    recycler.scrollToPosition(itemList.size() - 1);

                    message.setText("");
                    sendMessage(Constants.MESSAGE_TYPE_TEXT);
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

        getRoomMessagesFromDB();


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

    private void initVideoMatisse() {
        Matisse.from(this)
                .choose(MimeType.ofVideo())
                .countable(true)
                .maxSelectable(1)
                .showSingleMediaType(true)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new Glide4Engine())
                .forResult(REQUEST_CODE_CHOOSE_VIDEO);

    }

    private void markAsRead() {


    }

    private void showDeleteAlert(MessageModel messageModel, int position) {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_dialog_delete, null);

        dialog.setContentView(layout);

        View deleteView = layout.findViewById(R.id.deleteView);
        TextView copy = layout.findViewById(R.id.copy);
        TextView cancel = layout.findViewById(R.id.cancel);
        TextView delete = layout.findViewById(R.id.delete);
        TextView deleteForEveryOne = layout.findViewById(R.id.deleteForEveryOne);
        if (messageModel.getMessageType().equalsIgnoreCase(Constants.MESSAGE_TYPE_DELETED)) {
            deleteForEveryOne.setVisibility(View.GONE);
            deleteView.setVisibility(View.GONE);
        } else {
            if (messageModel.getMessageById().equals(SharedPrefs.getUserModel().getId())) {
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

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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

                HashMap<Integer, Integer> map = SharedPrefs.getDeletedMessagesId();
                if (map != null) {
                    map.put(messageModel.getId(), messageModel.getId());
                    SharedPrefs.setDeletedMessagesId(map);
                } else {
                    map = new HashMap<>();
                    map.put(messageModel.getId(), messageModel.getId());
                    SharedPrefs.setDeletedMessagesId(map);
                }
                itemList.remove(position);
                adapter.setItemList(itemList);
                dialog.dismiss();

            }
        });


        dialog.show();

    }

    private void callDeleteApi(MessageModel messageModel, int position) {
//        itemList.remove(position);
        MessageModel msg = itemList.get(position);
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

                    handleMessagesList(response);
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
        ImageView image = layout.findViewById(R.id.image);
        TextView url = layout.findViewById(R.id.url);


        url.setText("http://yoolah.acnure.com/r" + roomId);
        String imgUrl = AppConfig.BASE_URL_QR + roomId + "qrcode.png";
        Glide.with(this).load(AppConfig.BASE_URL_QR + roomId + "qrcode.png").into(image);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String html = "Please click on the link below to view the qr code\n\n" + "http://yoolah.acnure.com/viewqr/" + roomId;


                final Intent shareIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Invititation to join Yoolah group");
                shareIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        html
                );

                startActivity(shareIntent);
                dialog.dismiss();
            }
        });
        dialog.show();


    }

    private void openFile(Integer CODE) {
//        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        Intent i = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        i.setType("*/*");
        startActivityForResult(i, CODE);

    }

    private void initRecording() {
        recordButton.setRecordView(recordView);
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                Log.d("RecordView", "onStart");
                mRecorder = null;
                setMargins(recycler, 0, 0, 0, 170);
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
            MessageModel messageModel = new MessageModel(itemList.size() + 1, messageText,
                    Constants.MESSAGE_TYPE_AUDIO,
                    SharedPrefs.getUserModel().getId(),
                    roomId, System.currentTimeMillis(), false, mFileName + recordingLocalUrl + ".mp3"
            );
            itemList.add(messageModel);
            adapter.setItemList(itemList);
            recycler.scrollToPosition(itemList.size() - 1);

            uploadAudioToServer(mFileName + recordingLocalUrl + ".mp3");


        } catch (NullPointerException e) {

        } finally {
//            mRecorder.stop();
//            mRecorder.release();
//            mRecorder = null;
        }

    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
//        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
//            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
//            p.setMargins(left, top, right, bottom);
//            view.requestLayout();
//        }
    }


    private void initMatisse() {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(1)
                .showSingleMediaType(true)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new Glide4Engine())
                .forResult(REQUEST_CODE_CHOOSE);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 23) {
            if (data != null) {
                mSelected = Matisse.obtainResult(data);
                CompressImage compressImage = new CompressImage(this);
                compressedUrl = compressImage.compressImage("" + mSelected.get(0));
                MessageModel messageModel = new MessageModel(itemList.size() + 1, messageText,
                        Constants.MESSAGE_TYPE_IMAGE,
                        SharedPrefs.getUserModel().getId(),
                        roomId, System.currentTimeMillis(), false, "" + compressedUrl
                );
                itemList.add(messageModel);
                adapter.setItemList(itemList);
                recycler.scrollToPosition(itemList.size() - 1);

                uploadImageToServer();

            }

        }
        if (requestCode == 24) {
            if (data != null) {
                mSelected = Matisse.obtainResult(data);
                MessageModel messageModel = new MessageModel(itemList.size() + 1, messageText,
                        Constants.MESSAGE_TYPE_VIDEO,
                        SharedPrefs.getUserModel().getId(),
                        roomId, System.currentTimeMillis(), false, "" + mSelected.get(0)
                );
                itemList.add(messageModel);
                adapter.setItemList(itemList);
                recycler.scrollToPosition(itemList.size() - 1);

                uploadVideoToServer("" + CommonUtils.getRealPathFromURI(mSelected.get(0)));

            }

        }
        if (requestCode == REQUEST_CODE_FILE && data != null) {
//            Uri Fpath = data.getData();
            Uri uri = data.getData();

            File file = new File(uri.getPath());//create path from uri
            final String[] split = file.getPath().split(":");//split the path.
            try {
                String filePath = split[1];

                uploadDocumentToServer(filePath);

            } catch (Exception e) {
//                CommonUtils.showToast("invalid file");
            }

        }
        if (requestCode == REQUEST_CAPTURE_IMAGE) {
            CompressImage compressImage = new CompressImage(ChattingScreen.this);
            Uri abc = Uri.fromFile(new File(imageFilePath));
            compressedUrl = compressImage.compressImage("" + abc);
            MessageModel messageModel = new MessageModel(itemList.size() + 1, messageText,
                    Constants.MESSAGE_TYPE_IMAGE,
                    SharedPrefs.getUserModel().getId(),
                    roomId, System.currentTimeMillis(), false, "" + compressedUrl
            );
            itemList.add(messageModel);
            adapter.setItemList(itemList);
            recycler.scrollToPosition(itemList.size() - 1);
            uploadImageToServer();
            //don't compare the data to null, it will always come as  null because we are providing a file URI, so load with the imageFilePath we obtained before opening the cameraIntent
//            Glide.with(this).load(imageFilePath).into(mImageView);
            // If you are using Glide.
        }
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
                        uploadVideoImageToServer(CommonUtils.getVideoPic(AppConfig.BASE_URL_Videos + liveUrl));

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

    private void uploadImageToServer() {

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
                        sendMessage(Constants.MESSAGE_TYPE_IMAGE);

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

    private void uploadVideoImageToServer(Uri videoPic) {

        // create upload service client
        File file = new File(CommonUtils.getRealPathFromURI(videoPic));

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
        // create upload service client
        Uri uri = Uri.fromFile(new File("" + documetnFile));

        docfileName = CommonUtils.uri2filename(uri);
//        File file = new File(documetnFile);
        File file = new File(documetnFile);


        MessageModel messageModel = new MessageModel(itemList.size() + 1, messageText,
                Constants.MESSAGE_TYPE_DOCUMENT,
                SharedPrefs.getUserModel().getId(),
                roomId, System.currentTimeMillis(), false, "", docfileName
        );
        itemList.add(messageModel);
        adapter.setItemList(itemList);
        recycler.scrollToPosition(itemList.size() - 1);


        UserClient service = AppConfig.getRetrofit().create(UserClient.class);

        RequestBody requestBody = RequestBody.create(MediaType.parse("document/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("document", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        RequestBody extension = RequestBody.create(MediaType.parse("text/plain"), ".pdf");

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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            getRoomMessagesFromDB();
        }
    };

    private void getRoomMessagesFromDB() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("roomId", roomId);
        Call<AllRoomMessagesResponse> call = getResponse.allRoomMessages(map);
        call.enqueue(new Callback<AllRoomMessagesResponse>() {
            @Override
            public void onResponse(Call<AllRoomMessagesResponse> call, Response<AllRoomMessagesResponse> response) {
                if (response.code() == 200) {

                    handleMessagesList(response);
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<AllRoomMessagesResponse> call, Throwable t) {

            }
        });
    }

    private void handleMessagesList(Response<AllRoomMessagesResponse> response) {
        AllRoomMessagesResponse object = response.body();
        if (object.getMessages() != null && object.getMessages().size() > 0) {
            itemList.clear();
            List<MessageModel> messagesList = object.getMessages();
            HashMap<Integer, Integer> deletedMap = SharedPrefs.getDeletedMessagesId();
            if (deletedMap != null) {
                for (MessageModel model : messagesList) {
                    if (!deletedMap.containsKey(model.getId())) {


                        itemList.add(model);
                    }

                }
            } else {
                itemList = messagesList;
            }


            Collections.sort(itemList, new Comparator<MessageModel>() {
                @Override
                public int compare(MessageModel listData, MessageModel t1) {
                    Long ob1 = listData.getTime();
                    Long ob2 = t1.getTime();

                    return ob1.compareTo(ob2);

                }
            });


            adapter.setItemList(itemList);
            recycler.scrollToPosition(itemList.size() - 1);
            HashMap<Integer, List<MessageModel>> map = new HashMap<>();
            map.put(roomId, itemList);
            SharedPrefs.setInsideMessages(map, roomId);

            HashMap<Integer, Boolean> seenMap = SharedPrefs.getLastSeenMessage(roomId);

            seenMap = new HashMap<>();
            seenMap.put(itemList.get(itemList.size() - 1).getId(), true);
            SharedPrefs.setLastSeenMessage(seenMap, itemList.get(itemList.size() - 1).getId());


        }
    }

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
                    try {
                        Glide.with(ChattingScreen.this).load(AppConfig.BASE_URL_Image + object.getRoom().getCover_url()).into(image);

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

        } else if (messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_AUDIO)) {
            map.addProperty("audioUrl", liveUrl);
            map.addProperty("mediaTime", recordingTime);

        } else if (messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_VIDEO)) {
            map.addProperty("videoUrl", liveUrl);
            map.addProperty("imageUrl", liveUrlVideoPic);

        } else if (messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_DOCUMENT)) {
            map.addProperty("documentUrl", liveUrl);
            map.addProperty("filename", docfileName);


        }

        map.addProperty("messageByName", SharedPrefs.getUserModel().getName());
        map.addProperty("messageById", SharedPrefs.getUserModel().getId());
        map.addProperty("roomId", roomId);
        map.addProperty("messageByPicUrl", SharedPrefs.getUserModel().getThumbnailUrl());
        map.addProperty("time", "" + System.currentTimeMillis());
        Call<AllRoomMessagesResponse> call = getResponse.createMessage(map);
        call.enqueue(new Callback<AllRoomMessagesResponse>() {
            @Override
            public void onResponse(Call<AllRoomMessagesResponse> call, Response<AllRoomMessagesResponse> response) {
                if (response.code() == 200) {
                    message.setText("");
                    handleMessagesList(response);
//                    NewMessageResponse object = response.body();
//                    if (object.getMessages() != null && object.getMessages().size() > 0) {
//                        itemList = object.getMessages();
//                        Collections.sort(itemList, new Comparator<MessageModel>() {
//                            @Override
//                            public int compare(MessageModel listData, MessageModel t1) {
//                                Long ob1 = listData.getTime();
//                                Long ob2 = t1.getTime();
//
//                                return ob1.compareTo(ob2);
//
//                            }
//                        });
//                        adapter.setItemList(itemList);
//                        recycler.scrollToPosition(itemList.size() - 1);
//
//
//                    }
                    sendNotification(messageType);
                }
            }

            @Override
            public void onFailure(Call<AllRoomMessagesResponse> call, Throwable t) {

//                CommonUtils.showToast(t.getMessage());

            }
        });

    }

    private void sendNotification(String type) {
        for (UserModel user : particiapantsList) {
            if (!user.getId().equals(SharedPrefs.getUserModel().getId())) {

                NotificationAsync notificationAsync = new NotificationAsync(ChattingScreen.this);
                String NotificationTitle = "New message ";
                String NotificationMessage = "";
                if (type.equals(Constants.MESSAGE_TYPE_TEXT)) {
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
                    NotificationMessage = SharedPrefs.getUserModel().getName() + ": \uD83D\uDCCD Location";
                } else if (type.equals(Constants.MESSAGE_TYPE_CONTACT)) {
                    NotificationMessage = SharedPrefs.getUserModel().getName() + ": ☎ Contact";
                } else if (type.equals(Constants.MESSAGE_TYPE_POST)) {
                    NotificationMessage = SharedPrefs.getUserModel().getName() + ":  \uD83D\uDCF7 Post";
                }
                notificationAsync.execute(
                        "ali",
                        user.getFcmKey(),
                        NotificationTitle,
                        NotificationMessage,
                        "chat", "" + roomId
                );
            }
        }

    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        LocalBroadcastManager.getInstance(ChattingScreen.this).unregisterReceiver(mMessageReceiver);

    }

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,


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
}

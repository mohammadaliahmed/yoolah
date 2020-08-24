package com.appsinventiv.yoolah.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.SoundPool;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.appsinventiv.yoolah.Activites.ChattingScreen;
import com.appsinventiv.yoolah.Activites.FillPoll;
import com.appsinventiv.yoolah.Activites.MainActivity;
import com.appsinventiv.yoolah.Database.Word;
import com.appsinventiv.yoolah.Database.WordDao;
import com.appsinventiv.yoolah.Database.WordRepository;
import com.appsinventiv.yoolah.Database.WordViewModel;
import com.appsinventiv.yoolah.Models.MessageModel;
import com.appsinventiv.yoolah.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * Created by AliAh on 01/03/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String msg;
    String title, message, type;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private String username;
    private String Id, PictureUrl;
    private int ChannelId;
    SoundPool sp;
    WordRepository mRepository;
    String messageJson;
    private Word msgModel;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0 && SharedPrefs.getUserModel() != null) {
            Log.d("message payload", "Message data payload: " + remoteMessage.getData());
            msg = "" + remoteMessage.getData();

            Map<String, String> map = remoteMessage.getData();

            message = map.get("Message");
            title = map.get("Title");
            type = map.get("Type");
            Id = map.get("Id");
            messageJson = map.get("messageJson");
            handleNow(title, message, type);
            if (type.equalsIgnoreCase("chat")) {
                msgModel = new Gson().fromJson(
                        messageJson, new TypeToken<Word>() {
                        }.getType()
                );
                mRepository = new WordRepository(ApplicationClass.getInstance());
                inserDateBubble();
                if (!msgModel.getMessageById().equals(SharedPrefs.getUserModel().getId())) {
                    msgModel.setMessageRead(false);

                    mRepository.insert(msgModel);
                }
//                sendMessage();
            }
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("body", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void  inserDateBubble() {

        if (!SharedPrefs.getLastDate(msgModel.getRoomId()).equals(CommonUtils.getDate(System.currentTimeMillis()))) {
            Word amsgModel = new Word(
                    1,
                    CommonUtils.getDate(System.currentTimeMillis()),
                    Constants.MESSAGE_TYPE_BUBBLE,
                    SharedPrefs.getUserModel().getName(),
                    "", SharedPrefs.getUserModel().getId(),
                    msgModel.getRoomId(),
                    System.currentTimeMillis()-2000,
                    "", "", ""
                    , "", 0, "", msgModel.getGroupPicUrl()
                    , msgModel.getRoomName(),
                    true);
            mRepository.insert(amsgModel);
            SharedPrefs.setLastDate(CommonUtils.getDate(System.currentTimeMillis()), msgModel.getRoomId());
        }

    }

    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("newMsg");
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleNow(String notificationTitle, String messageBody, String type) {


        int num = (int) System.currentTimeMillis();
        /**Creates an explicit intent for an Activity in your app**/
        Intent resultIntent = null;
        resultIntent = new Intent(this, MainActivity.class);

        if (type.equalsIgnoreCase("chat")) {

            resultIntent = new Intent(this, ChattingScreen.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            resultIntent.putExtra("roomId", Integer.parseInt(Id));

        }
        if (type.equalsIgnoreCase("poll")) {
            resultIntent = new Intent(this, FillPoll.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            resultIntent.putExtra("pollId", Integer.parseInt(Id));

        }
//        else if (type.equalsIgnoreCase(Constants.NOTIFICATION_CHAT)) {
//
//            resultIntent = new Intent(this, ChattingScreen.class);
//            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            resultIntent.putExtra("roomId", Integer.parseInt(Id));
//            sendMessage();
//
//        }
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(notificationTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.WHITE);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(Integer.parseInt(Id) /* Request Code */, mBuilder.build());
    }
}

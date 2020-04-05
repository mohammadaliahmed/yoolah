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
import com.appsinventiv.yoolah.Activites.MainActivity;
import com.appsinventiv.yoolah.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import androidx.core.app.NotificationCompat;
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


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("message payload", "Message data payload: " + remoteMessage.getData());
            msg = "" + remoteMessage.getData();

            Map<String, String> map = remoteMessage.getData();

            message = map.get("Message");
            title = map.get("Title");
            type = map.get("Type");
            Id = map.get("Id");
            handleNow(title, message, type);
            if (type.equalsIgnoreCase("chat")) {
                sendMessage();
            }


        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("body", "Message Notification Body: " + remoteMessage.getNotification().getBody());
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
        mNotificationManager.notify(num /* Request Code */, mBuilder.build());
    }
}

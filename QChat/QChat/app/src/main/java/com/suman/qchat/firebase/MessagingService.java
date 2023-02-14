package com.suman.qchat.firebase;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.suman.qchat.R;
import com.suman.qchat.activities.ChatActivity;
import com.suman.qchat.models.User;
import com.suman.qchat.utilities.Constants;

import java.util.Random;

public class MessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "qchat_message";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        User user = new User();
        user.id = remoteMessage.getData().get(Constants.KEY_USER_ID);
        user.name = remoteMessage.getData().get(Constants.KEY_NAME);
        user.token = remoteMessage.getData().get(Constants.KEY_FCM_TOKEN);

        int notificationId = new Random().nextInt();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification;

        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.KEY_USER, user);

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setContentTitle(user.name)
                    .setContentText(remoteMessage.getData().get(Constants.KEY_MESSAGE))
                    .setContentIntent(pendingIntent)
                    .setOngoing(false)
                    .setChannelId(CHANNEL_ID)
                    .build();
            notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "Message", NotificationManager.IMPORTANCE_HIGH));
        } else {
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setContentTitle(user.name)
                    .setContentText(remoteMessage.getData().get(Constants.KEY_MESSAGE))
                    .setContentIntent(pendingIntent)
                    .setOngoing(false)
                    .build();
        }
        notificationManager.notify(notificationId, notification);
    }
}

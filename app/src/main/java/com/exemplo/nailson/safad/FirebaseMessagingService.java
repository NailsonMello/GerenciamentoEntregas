package com.exemplo.nailson.safad;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.exemplo.nailson.safad.FloatingWidget.FloatingViewService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title_notification = remoteMessage.getData().get("title");
        String body_notification = remoteMessage.getData().get("body");
        String from_sender_id_entrega = remoteMessage.getData().get("from_sender_id_entrega").toString();
       // String from_sender_id_msg = remoteMessage.getData().get("from_sender_id_msg").toString();
        String click_action = remoteMessage.getData().get("click_action");


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.iconenotification)
                .setContentTitle(title_notification)
                .setContentText(body_notification)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);


        Intent resultIntent = new Intent(click_action);
        resultIntent.putExtra("entrega_id", from_sender_id_entrega);
        resultIntent.putExtra("user_id", from_sender_id_entrega);
                PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        int mNotification = (int)System.currentTimeMillis();
        NotificationManager mNotify=
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotify.notify(mNotification, mBuilder.build());

    }
}

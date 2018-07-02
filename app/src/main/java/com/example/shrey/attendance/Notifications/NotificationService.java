package com.example.shrey.attendance.Notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.shrey.attendance.Activity.HomePageActivity;
import com.example.shrey.attendance.R;
import com.google.android.gms.common.api.Response;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Shrey on 6/28/2018.
 */

public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
             Intent intent=new Intent(this, HomePageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingintent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
            Uri sounduri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            final NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
            builder.setContentTitle("Alert");
            builder.setContentIntent(pendingintent);
            builder.setSound(sounduri);
            builder.setSmallIcon(R.drawable.sticky_logo);
            super.onMessageReceived(remoteMessage);
    }
}


package com.example.usahatanipadi.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.usahatanipadi.DatabaseHelper;
import com.example.usahatanipadi.MenuUtama;
import com.example.usahatanipadi.R;
import com.example.usahatanipadi.UserSessionManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

public class FirebaseMessageReceiver extends FirebaseMessagingService {
    UserSessionManager session;
    public DatabaseHelper db;
    String id_kelompok_tani;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        db = new DatabaseHelper(getApplicationContext());
        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        final String nama = user.get(UserSessionManager.KEY_NAMA);

        // get data pengguna info
        Cursor res = db.getData(nama);
        if (res.getCount() == 0) {
            Toast.makeText(this.getApplicationContext(), "Error tidak diketahui", Toast.LENGTH_SHORT).show();
        }
        while (res.moveToNext()) {
            this.id_kelompok_tani = res.getString(2);
        }

        if(remoteMessage.getData().get("id").equals(this.id_kelompok_tani)){
            if(remoteMessage.getData().size()>0){
                showNotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("message"));
            }

            if(remoteMessage.getNotification()!=null){
                showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
            }
        }
    }

    private RemoteViews getCustomDesgin(String title, String message){
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.title,title);
        remoteViews.setTextViewText(R.id.message,message);
        remoteViews.setImageViewResource(R.id.icon,R.drawable.ic_catat_keuangan);

        return remoteViews;
    }

    public void showNotification(String title, String message){
        Intent intent = new Intent(this, MenuUtama.class);
        intent.putExtra("menuFragment", "surveyFragment");
        String channel_id = "web_app_channel";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),channel_id)
                .setSmallIcon(R.drawable.ic_catat_keuangan)
                .setSound(uri)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000,1000,1000,1000,1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN){
            builder = builder.setContent(getCustomDesgin(title,message));
        }
        else{
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_catat_keuangan);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(channel_id,"web_app",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri,null);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0,builder.build());
    }
}

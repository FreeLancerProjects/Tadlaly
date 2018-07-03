package com.semicolon.tadlaly.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.semicolon.tadlaly.Activities.SendMsgActivity;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    private Target target;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String,String> map = remoteMessage.getData();
        for (String key:map.keySet())
        {
            Log.e("Key",key);
            Log.e("Values",map.get(key));
        }
        UpdateUi(map,remoteMessage.getSentTime());
    }

    private void UpdateUi(Map<String, String> map, long sentTime) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
                String time = dateFormat.format(new Date(sentTime));
                Preferences preferences = new Preferences(getApplicationContext());
                UserModel userModel = preferences.getUserData();
                String session = preferences.getSession();
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                builder.setAutoCancel(true);
                builder.setVibrate(new long[]{1000,1000,1000});
                builder.setLights(Color.WHITE,3000,3000);
                Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+ R.raw.not);
                builder.setSound(uri);
                builder.setSmallIcon(R.mipmap.launcher_icon);
                if (map.get("notification_type").equals(Tags.msg_not_type))
                    if (!TextUtils.isEmpty(session)||session!=null)
                    {
                        if (session.equals(Tags.login_session))
                        {
                            if (!map.get("from_id").equals(userModel.getUser_id()))
                            {

                                new Handler(Looper.getMainLooper())
                                        .postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notification_layout);
                                                remoteViews.setTextViewText(R.id.title,map.get("title"));
                                                remoteViews.setTextViewText(R.id.msg,map.get("message_content"));
                                                remoteViews.setTextViewText(R.id.time,time);
                                                builder.setContent(remoteViews);

                                                Intent intent = new Intent(getApplicationContext(), SendMsgActivity.class);
                                                intent.putExtra("chat_user_id",map.get("from_id"));
                                                intent.putExtra("chat_user_name",map.get("from_name"));
                                                intent.putExtra("chat_user_image",map.get("from_image"));
                                                intent.putExtra("chat_user_phone",map.get("from_phone"));
                                                intent.putExtra("curr_user_name",userModel.getUser_full_name());
                                                intent.putExtra("curr_user_image",userModel.getUser_photo());
                                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                                                builder.setContentIntent(pendingIntent);

                                                target = new Target() {
                                                    @Override
                                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                        ShortcutBadger.applyCount(getApplicationContext(),1);
                                                        remoteViews.setImageViewBitmap(R.id.image,bitmap);
                                                        manager.notify(0,builder.build());

                                                    }

                                                    @Override
                                                    public void onBitmapFailed(Drawable errorDrawable) {

                                                    }

                                                    @Override
                                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                    }
                                                };

                                                Picasso.with(getApplicationContext()).load(Uri.parse(Tags.Image_Url+map.get("from_image"))).into(target);

                                            }
                                        },1);
                            }

                        }
                    }

            }
        },500);









    }
}

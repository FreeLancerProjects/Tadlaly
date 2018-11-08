package com.semicolon.tadlaly.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.semicolon.tadlaly.Activities.SendMsgActivity;
import com.semicolon.tadlaly.Models.Cong_Model;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    private Target target;
    private Preferences preferences = new Preferences(this);
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String,String> map = remoteMessage.getData();
        for (String key:map.keySet())
        {
            Log.e("Key",key);
            Log.e("Values",map.get(key));
        }

        if (getSession().equals(Tags.login_session))
        {
            String to_id = map.get("to_id");
            String curr_id = getUserData().getUser_id();
            String not_type = map.get("notification_type");
            if (to_id.equals(curr_id))
            {


                final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

                new Handler(Looper.getMainLooper())
                        .postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (not_type.equals(Tags.not_messaging))
                                {
                                    createMessage_Notification(getUserData(),map,manager,builder);

                                }else if (not_type.equals(Tags.not_lottery))
                                {
                                    createCongratulation_notification(map);
                                }

                            }
                        },100);

            }
        }
    }

    private void createCongratulation_notification(Map<String, String> map) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            String sound_path = "android.resource://"+getPackageName()+"/"+R.raw.not;
            final int NOTIFICATION_ID =1;
            final String CHANNEL_ID = "my_channel_01";
            final CharSequence CHANNEL_NAME="my_channel_name";
            final int IMPORTANCE=NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,IMPORTANCE);
            channel.setSound(Uri.parse(sound_path),new AudioAttributes.Builder()
            .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .build()
            );
            channel.setShowBadge(true);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setChannelId(CHANNEL_ID);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle(map.get("title_notification"));
            builder.setContentText(map.get("message_content"));

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);

            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    builder.setLargeIcon(bitmap);
                    manager.notify(NOTIFICATION_ID,builder.build());
                    EventBus.getDefault().post(new Cong_Model());
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.with(this).load(R.drawable.logo).into(target);


        }else
            {
                String sound_path = "android.resource://"+getPackageName()+"/"+R.raw.not;
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);


                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        builder.setLargeIcon(bitmap);
                        builder.setSmallIcon(R.mipmap.ic_launcher);
                        builder.setContentTitle(map.get("title_notification"));
                        builder.setSound(Uri.parse(sound_path));
                        builder.setContentText(map.get("message_content"));
                        manager.notify(1,builder.build());
                        EventBus.getDefault().post(new Cong_Model());


                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };

                Picasso.with(this).load(R.drawable.logo).into(target);

            }
    }

    private void createMessage_Notification(UserModel userData, Map<String, String> map, NotificationManager manager, NotificationCompat.Builder builder) {


        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            Log.e("nnnnnnnn","nnnnnnnnnnn");

            final int notifyID = 1;
            String CHANNEL_ID = "my_channel_01";
            CharSequence name ="my_channel_name";
            String notPath = "android.resource://"+getPackageName()+"/"+ R.raw.not;
            int importance = NotificationManager.IMPORTANCE_HIGH;

            Intent intent = new Intent(MyFirebaseMessaging.this, SendMsgActivity.class);
            intent.putExtra("chat_user_id",map.get("from_id"));
            intent.putExtra("chat_user_name",map.get("from_name"));
            intent.putExtra("chat_user_image",map.get("from_image"));
            intent.putExtra("chat_user_phone",map.get("from_phone"));
            intent.putExtra("curr_user_name",userData.getUser_full_name());
            intent.putExtra("curr_user_image",userData.getUser_photo());

            android.app.TaskStackBuilder stackBuilder = android.app.TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            mChannel.setShowBadge(true);
            mChannel.setSound(Uri.parse(notPath),new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .build());

            final Notification.Builder notification = new Notification.Builder(this)
                    .setContentTitle(map.get("title"))
                    .setContentText(map.get("message_content"))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setChannelId(CHANNEL_ID)
                    .setContentIntent(pendingIntent);
            final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(mChannel);


            Target target = new Target() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    notification.setLargeIcon(bitmap);
                    mNotificationManager.notify(notifyID , notification.build());
                    Log.e("rr","rr");
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            Picasso.with(this).load(Tags.Image_Url+map.get("from_image")).into(target);

        }else
        {
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Log.e("sdsdsa","sdasdas");
                    builder.setLargeIcon(bitmap);
                    builder.setSmallIcon(R.drawable.logo);
                    Intent intent = new Intent(MyFirebaseMessaging.this, SendMsgActivity.class);
                    intent.putExtra("chat_user_id",map.get("from_id"));
                    intent.putExtra("chat_user_name",map.get("from_name"));
                    intent.putExtra("chat_user_image",map.get("from_image"));
                    intent.putExtra("chat_user_phone",map.get("from_phone"));
                    intent.putExtra("curr_user_name",userData.getUser_full_name());
                    intent.putExtra("curr_user_image",userData.getUser_photo());
                    PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessaging.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                    builder.setContentTitle(map.get("title"));
                    String notPath = "android.resource://"+getPackageName()+"/"+ R.raw.not;
                    builder.setSound(Uri.parse(notPath));
                    builder.setContentIntent(pendingIntent);
                    builder.setContentText(map.get("message_content"));

                    manager.notify(0,builder.build());

                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.with(this).load(Tags.Image_Url+map.get("from_image")).into(target);

        }

              /*  SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
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
                builder.setSmallIcon(R.mipmap.ic_launcher);
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
                    }*/











    }


    private String getSession()
    {

        String session = preferences.getSession();
        return session;
    }

    private UserModel getUserData(){
        UserModel userModel = preferences.getUserData();
        return userModel;
    }
}

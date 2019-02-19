package com.valuebiz.servicemarket.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.valuebiz.servicemarket.R;
import com.valuebiz.servicemarket.activities.WebViewActivity;

/**
 * 프로젝트명    : toitdoit
 * 패키지명      : com.hucloud.toitdoit.fcm
 * 작성 및 소유자 : hucloud(huttchang@gmail.com)
 * 최초 생성일   : 2018. 9. 10.
 */
public class FCMMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }


    private void sendNotification(RemoteMessage remoteMessage) {

        if (remoteMessage == null) return;

        Intent intent = new Intent(this, WebViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        int icon = R.mipmap.icon;
        NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();
        inboxStyle
                .setBigContentTitle(remoteMessage.getData().get("title"))
                .bigText(remoteMessage.getData().get("content"));

        String channelId = "service_market_channel_id";
        String channelName = "ServiceMarketPushChannel";
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(icon)
                        .setBadgeIconType(icon)
                        .setContentTitle(remoteMessage.getData().get("title"))
                        .setTicker(remoteMessage.getData().get("title"))
                        .setContentText(remoteMessage.getData().get("content"))
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), icon))
                        .setDefaults(Notification.DEFAULT_ALL) // 알림, 사운드 진동 설정
                        .setStyle(inboxStyle)
                        .setColorized(true)
                        .setColor(getColor(R.color.statusOrange))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)   // heads-up
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription("channel description");
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(1, notificationBuilder.build());

    }

}

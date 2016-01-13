package app.com.example.android.mutemehere;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class LocationUnMuteManager extends BroadcastReceiver {
    public LocationUnMuteManager() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        unMuteDevice(context);
    }

    static public void unMuteDevice(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder not = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Mute Me Here")
                .setContentText("GeoFence exited and phone un muted");

        notificationManager.notify(0, not.build());
        Log.i("Location Un mute Manger", "Un Muted");

        boolean notifications = HelperFunctions.getNotificationLight(context);
        Log.i("Notification Value", notifications + "");
        if (notifications) {
            not.setPriority(Notification.PRIORITY_HIGH);
            not.setLights(0x00ff0000, 500, 50000);
        }

        boolean noti_vibrate = HelperFunctions.getNotificationVibration(context);
        Log.i("Notification Value", noti_vibrate + "");
        if (noti_vibrate) {
            not.setDefaults(Notification.DEFAULT_VIBRATE);
        }
        notificationManager.notify(0, not.build());
        //setting ringer volume back
        HelperFunctions.setRingerModeBack(context);
    }
}

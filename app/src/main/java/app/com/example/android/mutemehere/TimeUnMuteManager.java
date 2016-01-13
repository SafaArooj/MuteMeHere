package app.com.example.android.mutemehere;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Safa Arooj on 7/11/2015.
 */
public class TimeUnMuteManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("UnMuteManager", "Started");

        //setting ringer volume back
        HelperFunctions.setRingerModeBack(context);

        Calendar finishTime = Calendar.getInstance();
        finishTime.set(Calendar.HOUR_OF_DAY,intent.getExtras().getInt("finishHour"));
        finishTime.set(Calendar.MINUTE, intent.getExtras().getInt("finishMinute"));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        //Reading the Settings
        NotificationManager notificationManager = (NotificationManager)
                                                    context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder not = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("Mute Me Here")
                        .setContentText("Phone UnMuted on " + sdf.format(finishTime.getTime()));

        boolean noti_pulse = HelperFunctions.getNotificationLight(context);
        Log.i("Notification Value", noti_pulse +"");
        if(noti_pulse){
            not.setPriority(Notification.PRIORITY_HIGH);
            not.setLights(0x00ff0000, 500, 50000);
        }

        boolean noti_vibrate = HelperFunctions.getNotificationVibration(context);
        Log.i("Notification Value", noti_vibrate + "");
        if(noti_vibrate){
            not.setDefaults(Notification.DEFAULT_VIBRATE);
        }

        notificationManager.notify(0, not.build());

        //call to set the next scheduled alarms
        TimeMuteManager.setTimeBasedMute(context);

    }

}

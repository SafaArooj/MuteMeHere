package app.com.example.android.mutemehere;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeMuteService extends IntentService {

    public TimeMuteService(){
        super("TimeMuteService");
        Log.i("Time Mute Service ", "Started");
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i("Time Mute Service", "Created");
    }

    @Override
    public void onHandleIntent(Intent intent)
    {
        //storing current Ringer Volume Settings
        Log.i("Time Mute Serivce","Set current ringer mode called");
        HelperFunctions.setCurrentRingerMode(getApplicationContext());

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY,intent.getExtras().getInt("startHour"));
        startTime.set(Calendar.MINUTE,intent.getExtras().getInt("startMinute"));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        NotificationCompat.Builder not = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("Mute Me Here:")
                        .setContentText("Phone Muted on " + sdf.format(startTime.getTime()));

        //Reading the Settings
        boolean noti_pulse = HelperFunctions.getNotificationLight(getApplicationContext());
        Log.i("Notification Value", noti_pulse +"");
        if(noti_pulse){
            Log.i("Inside Noti_pulse","");
            not.setPriority(Notification.PRIORITY_HIGH);
            not.setLights(0xffff0000, 500, 50000);
        }

        boolean noti_vibrate = HelperFunctions.getNotificationVibration(getApplicationContext());
        Log.i("Notification Value", noti_vibrate + "");
        if(noti_vibrate){
            not.setDefaults(Notification.DEFAULT_VIBRATE);
        }

        notificationManager.notify(0,  not.build());

        boolean vibrate = HelperFunctions.getVibrateMode(getApplicationContext());
        Log.i("Vibrate = ",vibrate + "");
        if(vibrate){
            HelperFunctions.setRinger2Vibrate(getApplicationContext());
        }

        boolean silent = HelperFunctions.getSilentMode(getApplicationContext());
        Log.i("Silent", silent + "");
        if(silent){
            HelperFunctions.setRinger2Silent(getApplicationContext());
        }

        cancelAlarm(intent);
    }

    @Override
    public boolean stopService(Intent intent){
        Log.i("Time Mute Service ", "Stopped");
        return super.stopService(intent);
    }

    @Override
    public void onDestroy(){
        Log.i("Time Mute Service ", "Destroyed");

    }

    private void cancelAlarm(Intent intent) {

        Intent intent1 = new Intent(getApplicationContext(),TimeUnMuteManager.class);

        int finishHour = intent.getExtras().getInt("finishHour");
        int finishMinute = intent.getExtras().getInt("finishMinute");
        long timeSpanModelId =  intent.getExtras().getLong("ID");
        int id = (int) timeSpanModelId;

        intent1.putExtra("finishHour", finishHour);
        intent1.putExtra("finishMinute", finishMinute);

        Calendar finishTime = Calendar.getInstance();
        finishTime.set(Calendar.HOUR_OF_DAY,finishHour);
        finishTime.set(Calendar.MINUTE,finishMinute);
        finishTime.set(Calendar.SECOND,0);

        PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(), id, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Log.i("In cancelAlarm ", "called on " + finishTime.get(Calendar.HOUR_OF_DAY));
        Log.i("In cancelAlarm ", "called on " + finishTime.get(Calendar.MINUTE));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, finishTime.getTimeInMillis(), pIntent);
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, finishTime.getTimeInMillis(), pIntent);
        }

    }
}

package app.com.example.android.mutemehere;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Safa Arooj on 5/27/2015.
 */
public class TimeMuteManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("TimeMuteManager", "Started");
        setTimeBasedMute(context);
    }

    public static void cancelTimeBasedMute(Context context) {
        AppDataSource appDataSource = new AppDataSource(context);
        appDataSource.open();
        //reading list form timespan
        List<TimeSpanModel> timeSpanModelList = appDataSource.getTimeSpanList();

        if (timeSpanModelList != null) {
            for (TimeSpanModel timeSpan : timeSpanModelList) {
                PendingIntent pendingIntent = createPendingIntent(context, timeSpan);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);

            }
        }
        appDataSource.close();
    }

    public static void setTimeBasedMute(Context context) {
        //cancelling the previous alarms
        cancelTimeBasedMute(context);
        //getting most recent list from database
        AppDataSource appDataSource = new AppDataSource(context);
        appDataSource.open();
        List<TimeSpanModel> timeSpanModelList = appDataSource.getTimeSpanList();
        if (timeSpanModelList != null) {
            for (TimeSpanModel timeSpanModel : timeSpanModelList) {

                PendingIntent pendingIntent = createPendingIntent(context, timeSpanModel);

                Log.i("Alarm Name ", timeSpanModel.name);

                Calendar startCalendar = Calendar.getInstance();
                startCalendar.setTimeInMillis(System.currentTimeMillis());
                //setting startCalendar equal to the startTime of TimeSpanModel
                startCalendar.set(Calendar.HOUR_OF_DAY, timeSpanModel.start_hour);
                startCalendar.set(Calendar.MINUTE, timeSpanModel.start_minute);
                startCalendar.set(Calendar.SECOND, 0);
                //getting current day, hour and minute
                final int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                final int nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                final int nowMinute = Calendar.getInstance().get(Calendar.MINUTE);

                boolean alarmSet = false;
                //This loop checks if it's later in the week
                //as Calendar.SUNDAY = 1 and Calendar.SATURDAY = 7
                for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; ++dayOfWeek) {

                    if ((timeSpanModel.getRepeatingDay(dayOfWeek - 1) && dayOfWeek >= nowDay) && //checking if the startTime day is now or in future
                            !(dayOfWeek == nowDay && timeSpanModel.start_hour < nowHour) && //making sure the startHour has not passed yet
                            !(dayOfWeek == nowDay && timeSpanModel.start_hour == nowHour && timeSpanModel.start_minute <= nowMinute)) //making sure startMinute has not passed yet
                    {
                        startCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek); //setting the startCalendar day to dayOfWeek

                        Log.i(startCalendar.get(Calendar.DAY_OF_WEEK) + " ", "setAlarm called");
                        //calling Alarm
                        setAlarm(context, startCalendar, pendingIntent);
                        alarmSet = true;
                        break;
                    }

                    Log.i("iteration ", dayOfWeek + "");
                }
                //This checks if the it's earlier in the week
                if (!alarmSet) {
                    for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; ++dayOfWeek) {
                        if (timeSpanModel.getRepeatingDay(dayOfWeek - 1) && dayOfWeek <= nowDay) {
                            startCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                            startCalendar.add(Calendar.WEEK_OF_YEAR, 1);//the alarm will go off next week(in future)

                            Log.i(startCalendar.get(Calendar.DAY_OF_WEEK) + " ", "setAlarm called in if clause");
                            setAlarm(context, startCalendar, pendingIntent);
                            alarmSet = true;
                            break;
                        }
                    }
                }
            }
        }
    }


    public static void setAlarm(Context context, Calendar calendar, PendingIntent pIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
            Log.i("In setAlarm ", "called on " + calendar.get(Calendar.HOUR_OF_DAY));
            Log.i("In setAlarm ", "called on " + calendar.get(Calendar.MINUTE));
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        }
    }


    public static PendingIntent createPendingIntent(Context context, TimeSpanModel timeSpanModel) {

        Intent intent = new Intent(context, TimeMuteService.class);
        intent.putExtra("ID", timeSpanModel.id);
        intent.putExtra("Name", timeSpanModel.name);
        intent.putExtra("startHour", timeSpanModel.start_hour);
        intent.putExtra("startMinute", timeSpanModel.start_minute);
        intent.putExtra("finishHour", timeSpanModel.finish_hour);
        intent.putExtra("finishMinute", timeSpanModel.finish_minute);

        Log.i("pending intent", "created");
        return PendingIntent.getService(context, (int) timeSpanModel.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}

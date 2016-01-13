package app.com.example.android.mutemehere;

import android.app.Activity;

/**
 * Created by Safa Arooj on 5/10/2015.
 */
public class TimeSpanModel  {

    public static final int SUNDAY = 0;
    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;

    public boolean repeatingDays[];
    public int start_hour;
    public int start_minute;
    public int finish_hour;
    public int finish_minute;
    public long id;
    public String name;

    public TimeSpanModel(){
        repeatingDays = new boolean[7];
    }

    public void setRepeatingDay(int dayOfWeek, boolean value) {
        repeatingDays[dayOfWeek] = value;
    }
    public boolean getRepeatingDay(int dayOfWeek) {
        return repeatingDays[dayOfWeek];
    }

}

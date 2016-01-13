package app.com.example.android.mutemehere;

import android.provider.BaseColumns;

/**
 * Created by Safa Arooj on 5/15/2015.
 */
public class TableDef {

    public TableDef() {}

    public static class TimeSpan implements BaseColumns {
        //table name
        public static final String TABLE_NAME = "timeSpanTable";
        //columns of the table timeSpan
        public static final String COLUMN_NAME_TIMESPAN_EVENT_NAME = "name";
        public static final String COLUMN_NAME_TIMESPAN_START_HOUR = "startHour";
        public static final String COLUMN_NAME_TIMESPAN_START_MINUTE = "startMinute";
        public static final String COLUMN_NAME_TIMESPAN_FINISH_HOUR = "finishHour";
        public static final String COLUMN_NAME_TIMESPAN_FINISH_MINUTE = "finishMinute";
        public static final String COLUMN_NAME_TIMESPAN_REPEAT_DAYS = "days";


    }

    public static class Location implements BaseColumns {
        //table name
        public static final String TABLE_NAME = "locationTable";
        //columns of the table location
        public static final String COLUMN_NAME_LOCATION_NAME = "name";
        public static final String COLUMN_NAME_LOCATION_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_LOCATION_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LOCATION_RADIUS = "radius";

    }
}

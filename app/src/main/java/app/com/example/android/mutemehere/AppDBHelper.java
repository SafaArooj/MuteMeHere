package app.com.example.android.mutemehere;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import app.com.example.android.mutemehere.TableDef.TimeSpan;
import app.com.example.android.mutemehere.TableDef.Location;

/**
 * Created by Safa Arooj on 5/15/2015.
 */
public class AppDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "muteMeHere.db";

    public AppDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String SQL_CREATE_TIMESPAN =
            "CREATE TABLE " + TimeSpan.TABLE_NAME + " (" +
                    TimeSpan._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TimeSpan.COLUMN_NAME_TIMESPAN_EVENT_NAME + " TEXT, " +
                    TimeSpan.COLUMN_NAME_TIMESPAN_START_HOUR + " INTEGER, " +
                    TimeSpan.COLUMN_NAME_TIMESPAN_START_MINUTE + " INTEGER, " +
                    TimeSpan.COLUMN_NAME_TIMESPAN_FINISH_HOUR + " INTEGER, " +
                    TimeSpan.COLUMN_NAME_TIMESPAN_FINISH_MINUTE + " INTEGER, " +
                    TimeSpan.COLUMN_NAME_TIMESPAN_REPEAT_DAYS + " TEXT" + " )";

    public static final String SQL_CREATE_LOCATION =
            "CREATE TABLE " + Location.TABLE_NAME + " (" +
                    Location._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Location.COLUMN_NAME_LOCATION_NAME + " TEXT, " +
                    Location.COLUMN_NAME_LOCATION_LATITUDE + " DOUBLE, " +
                    Location.COLUMN_NAME_LOCATION_LONGITUDE + " DOUBLE, " +
                    Location.COLUMN_NAME_LOCATION_RADIUS + " FLOAT" + " )";

    public static final String SQL_DELETE_TIMESPAN =
            "DROP TABLE IF EXISTS " + TimeSpan.TABLE_NAME;

    public static final String SQL_DELETE_LOCATION =
            "DROP TABLE IF EXISTS " + Location.TABLE_NAME;


    @Override
    public void onCreate(SQLiteDatabase db){
         db.execSQL(SQL_CREATE_TIMESPAN);
        Log.i("TimeSpan Table ", "created");
         db.execSQL(SQL_CREATE_LOCATION);
        Log.i("Location Table ", "created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion){
        db.execSQL(SQL_DELETE_TIMESPAN);
        db.execSQL(SQL_DELETE_LOCATION);
        onCreate(db);
    }

}

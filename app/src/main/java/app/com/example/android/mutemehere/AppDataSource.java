package app.com.example.android.mutemehere;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.com.example.android.mutemehere.TableDef.TimeSpan;
import app.com.example.android.mutemehere.TableDef.Location;

/**
 * Created by Safa Arooj on 5/16/2015.
 */
public class AppDataSource {

    public SQLiteOpenHelper dbhelper;
    public SQLiteDatabase database;

    public AppDataSource(Context context) {
        dbhelper = new AppDBHelper(context);
    }

    public void open(){
        database = dbhelper.getWritableDatabase();
    }

    public void close(){
        dbhelper.close();
    }

    public long createTimeSpan(TimeSpanModel timeSpanModel){
        ContentValues values = new ContentValues();
        values.put(TimeSpan.COLUMN_NAME_TIMESPAN_EVENT_NAME, timeSpanModel.name);
        values.put(TimeSpan.COLUMN_NAME_TIMESPAN_START_HOUR, timeSpanModel.start_hour);
        values.put(TimeSpan.COLUMN_NAME_TIMESPAN_START_MINUTE, timeSpanModel.start_minute);
        values.put(TimeSpan.COLUMN_NAME_TIMESPAN_FINISH_HOUR, timeSpanModel.finish_hour);
        values.put(TimeSpan.COLUMN_NAME_TIMESPAN_FINISH_MINUTE, timeSpanModel.finish_minute);

        String repeatingDays = "";
        for (int i = 0; i < 7; ++i) {
            repeatingDays = repeatingDays + timeSpanModel.getRepeatingDay(i) + ",";
        }
        values.put(TimeSpan.COLUMN_NAME_TIMESPAN_REPEAT_DAYS, repeatingDays);

        long insertID = database.insert(TimeSpan.TABLE_NAME, null, values);
        timeSpanModel.id = insertID;

        Log.i("AppDataSource","TimeSpan inserted with id " + timeSpanModel.id);
        Log.i("AppDataSource","Repeat on days created" + repeatingDays);

        return insertID;
    }

    public long createLocation(LocationModel locationModel){
        ContentValues values = new ContentValues();
        values.put(Location.COLUMN_NAME_LOCATION_NAME, locationModel.name);
        values.put(Location.COLUMN_NAME_LOCATION_LATITUDE, locationModel.latitude);
        values.put(Location.COLUMN_NAME_LOCATION_LONGITUDE, locationModel.longitude);
        values.put(Location.COLUMN_NAME_LOCATION_RADIUS, locationModel.radius);

        long insertID = database.insert(Location.TABLE_NAME, null, values);
        locationModel.id = insertID;

        Log.i("AppDataSource","Location inserted with id " + locationModel.id);

        return  insertID;

    }


    public LocationModel getSingleLocation(long id){
        String select = "SELECT * FROM " + Location.TABLE_NAME + " WHERE " + Location._ID + " = " + id;
        Cursor cursor = database.rawQuery(select, null);

        if(cursor.moveToNext()){
            LocationModel locationModel = new LocationModel();
            locationModel.id = cursor.getLong(cursor.getColumnIndex(Location._ID));
            locationModel.name = cursor.getString(cursor.getColumnIndex(Location.COLUMN_NAME_LOCATION_NAME));
            locationModel.latitude = cursor.getDouble(cursor.getColumnIndex(Location.COLUMN_NAME_LOCATION_LATITUDE));
            locationModel.longitude = cursor.getDouble(cursor.getColumnIndex(Location.COLUMN_NAME_LOCATION_LONGITUDE));
            locationModel.radius = cursor.getFloat(cursor.getColumnIndex(Location.COLUMN_NAME_LOCATION_RADIUS));

            return locationModel;
        }

        return null;
    }

    public void deleteTimeSpan(long id){
        String deleteQuery = "DELETE FROM " + TimeSpan.TABLE_NAME + " WHERE " + TimeSpan._ID + " = " + id;
        database.execSQL(deleteQuery);
    }

    public void deleteLocation(long id){
        String deleteQuery = "DELETE FROM " + Location.TABLE_NAME + " WHERE " + Location._ID + " = " + id;
        database.execSQL(deleteQuery);
    }

    public List<TimeSpanModel> getTimeSpanList(){
        String select = "SELECT * FROM " + TimeSpan.TABLE_NAME;

        Cursor cursor = database.rawQuery(select,null);

        List<TimeSpanModel> timeSpansList = new ArrayList<TimeSpanModel>() ;

        while(cursor.moveToNext()){

            TimeSpanModel timeSpanModel = new TimeSpanModel();
            timeSpanModel.id = cursor.getLong(cursor.getColumnIndex(TimeSpan._ID));
            timeSpanModel.name = cursor.getString(cursor.getColumnIndex(TimeSpan.COLUMN_NAME_TIMESPAN_EVENT_NAME));
            timeSpanModel.start_hour = cursor.getInt(cursor.getColumnIndex(TimeSpan.COLUMN_NAME_TIMESPAN_START_HOUR));
            timeSpanModel.start_minute = cursor.getInt(cursor.getColumnIndex(TimeSpan.COLUMN_NAME_TIMESPAN_START_MINUTE));
            timeSpanModel.finish_hour = cursor.getInt(cursor.getColumnIndex(TimeSpan.COLUMN_NAME_TIMESPAN_FINISH_HOUR));
            timeSpanModel.finish_minute = cursor.getInt(cursor.getColumnIndex(TimeSpan.COLUMN_NAME_TIMESPAN_FINISH_MINUTE));

            String[] repeatingDays = cursor.getString(cursor.getColumnIndex(TimeSpan.COLUMN_NAME_TIMESPAN_REPEAT_DAYS)).split(",");
            String logCatDays = "";
            for (int i = 0; i < repeatingDays.length; ++i) {
                timeSpanModel.setRepeatingDay(i, Boolean.parseBoolean(repeatingDays[i]));
                logCatDays = logCatDays + timeSpanModel.getRepeatingDay(i) + ",";
            }
            timeSpansList.add(timeSpanModel);

            Log.i("Reading week values", logCatDays);
        }
        if(!timeSpansList.isEmpty()){
            return timeSpansList;
        }

        Log.i("TimeSpan Model ", "No value added yet");
        return null;
    }

    public List<LocationModel> getLocationModelList(){
        String select = "SELECT * FROM " + Location.TABLE_NAME;

        Cursor cursor = database.rawQuery(select, null);

        List<LocationModel> locationModelList = new ArrayList<LocationModel>();

        while(cursor.moveToNext()){

            LocationModel locationModel = new LocationModel();
            locationModel.id = cursor.getLong(cursor.getColumnIndex(Location._ID));
            locationModel.name = cursor.getString(cursor.getColumnIndex(Location.COLUMN_NAME_LOCATION_NAME));
            locationModel.latitude = cursor.getDouble(cursor.getColumnIndex(Location.COLUMN_NAME_LOCATION_LATITUDE));
            locationModel.longitude = cursor.getDouble(cursor.getColumnIndex(Location.COLUMN_NAME_LOCATION_LONGITUDE));
            locationModel.radius = cursor.getFloat(cursor.getColumnIndex(Location.COLUMN_NAME_LOCATION_RADIUS));

            locationModelList.add(locationModel);
        }

        if(!locationModelList.isEmpty()){
            return locationModelList;
        }

        Log.i("Location Model ", "No value added yet");
        return null;
    }
}

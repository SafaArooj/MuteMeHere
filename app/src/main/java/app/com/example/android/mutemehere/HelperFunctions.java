package app.com.example.android.mutemehere;

import android.app.Notification;
import android.app.SharedElementCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Safa Arooj on 8/1/2015.
 */
public class HelperFunctions {

    static int currentVolume;
    static int currentMode;

    public static void setRinger2Silent(Context context)
    {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        Log.i("HelperFunctions ", "Silent method called");
    }

    public static void setRinger2Normal(Context context)
    {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        //setting the volume
        audioManager.setStreamVolume(AudioManager.STREAM_RING, currentVolume, 0);
        Log.i("HelperFunctions ", "Normal method called");
    }

    public static void setRinger2Vibrate(Context context)
    {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        Log.i("HelperFunctions ", "Vibrate method called");
    }

    public static boolean getNotificationLight(Context context){
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        return SP.getBoolean("LED_pref", true);
    }

    public static boolean getNotificationVibration(Context context){
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        return SP.getBoolean("noti_vibrate_pref", true);
    }

    public static boolean getVibrateMode(Context context){
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        return SP.getBoolean("vibrate_pref",true);
    }

    public static boolean getSilentMode(Context context){
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        return SP.getBoolean("silent_pref",false);
    }

    public static void setCurrentRingerMode(Context context){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            currentMode = AudioManager.RINGER_MODE_NORMAL;
            currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
            Log.i("Current Mode Normal", currentVolume +"");
            Log.i("Vlaue of",currentMode+"");
        }
        else if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){
            currentMode = AudioManager.RINGER_MODE_VIBRATE;
            Log.i("Current Mode Vibrate", currentVolume +"");
            Log.i("Vlaue of",currentMode+"");
        }
        else if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT){
            currentMode = AudioManager.RINGER_MODE_SILENT;
            Log.i("Current Mode Silent", currentVolume +"");
            Log.i("Vlaue of",currentMode+"");
        }else{
            Log.i("Nothing ","Matched above");
        }

    }

    public static void setRingerModeBack(Context context){
        if(currentMode == AudioManager.RINGER_MODE_NORMAL){
            setRinger2Normal(context);
            Log.i("Restored Mode normal", currentVolume + "");
        }
        else if (currentMode == AudioManager.RINGER_MODE_VIBRATE )
        {
            setRinger2Vibrate(context);
            Log.i("Restored Mode vibrate", currentVolume + "");
        }
        else if (currentMode == AudioManager.RINGER_MODE_SILENT ){
            setRinger2Silent(context);
            Log.i("Restored Mode Silent", currentVolume + "");
        }
        else{
            Log.i("Nothing ","Matched above");
        }
    }
}

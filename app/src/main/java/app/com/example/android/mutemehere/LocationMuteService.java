package app.com.example.android.mutemehere;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;


public class LocationMuteService extends IntentService {

    public LocationMuteService() {
        super("LocationMuteService");
    }

    public void onCreate() {
        super.onCreate();
        Log.i("location Mute Service", "created");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.i("location Mute service", "destroyed");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (!geofencingEvent.hasError()) {
            //get the transition type
            int transition = geofencingEvent.getGeofenceTransition();
            List triggeringGeoFences = geofencingEvent.getTriggeringGeofences();

            String geoFenceTransitionDetails = triggeringGeoFences.toString();
            Log.i("LocationMuteService", geoFenceTransitionDetails);

            switch (transition) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    Log.i(" ", "GeoFence Entered");
                    NotificationCompat.Builder not = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.notification_icon)
                            .setContentTitle("Mute Me Here")
                            .setContentText("GeoFence entered and phone muted");
                    notificationManager.notify(0, not.build());
                    Log.i("Location Mute Serivce", "Set current ringer mode called");
                    //storing current Ringer Mode
                    HelperFunctions.setCurrentRingerMode(getApplicationContext());

                    //Reading the Settings
                    boolean noti_pulse = HelperFunctions.getNotificationLight(getApplicationContext());
                    Log.i("Notification Value", noti_pulse + "");
                    if (noti_pulse) {
                        not.setPriority(Notification.PRIORITY_HIGH);
                        not.setLights(0x00ff0000, 500, 50000);
                    }

                    boolean noti_vibrate = HelperFunctions.getNotificationVibration(getApplicationContext());
                    Log.i("Notification Value", noti_vibrate + "");
                    if (noti_vibrate) {
                        not.setDefaults(Notification.DEFAULT_VIBRATE);
                    }

                    notificationManager.notify(0, not.build());

                    boolean vibrate = HelperFunctions.getVibrateMode(getApplicationContext());
                    Log.i("Vibrate = ", vibrate + "");
                    if (vibrate) {
                        HelperFunctions.setRinger2Vibrate(getApplicationContext());
                    }

                    boolean silent = HelperFunctions.getSilentMode(getApplicationContext());
                    Log.i("Silent", silent + "");
                    if (silent) {
                        HelperFunctions.setRinger2Silent(getApplicationContext());
                    }
                    break;


                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    //starting UnMuteReciver inCase the user exited the GeoFence
                    LocationUnMuteManager.unMuteDevice(getApplicationContext());
                    break;
                default:
                    break;
            }

        }
    }

}
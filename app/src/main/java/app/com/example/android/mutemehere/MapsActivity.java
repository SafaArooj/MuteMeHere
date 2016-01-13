package app.com.example.android.mutemehere;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment that launches other parts of the demo application.
 */
public class MapsActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    LocationModel locationModel;
    //location object for updating current Location
    Location currentLocation;
    //list for geofences
    private List<Geofence> geofenceList;

    private GoogleMap mMap;
    Marker marker;
    private static final float DEFAULT_ZOOM = 12;
    //location layout variables
    private EditText locationName;
    private Button btn_search;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    float radius;
    Circle circle;

    Dialog dialog; //dialog and its layout views
    private TextView tv_locationName, tv_latitude, tv_longitude;
    private EditText et_radius; //textBox for getting Radius Value
    private Button btn_submit, btn_viewGeofence;

    private AppDataSource appDataSource;

    //flag to help remove the deleted geofences
    public static boolean flagButton = false;

    @Override
    public void onCreate(Bundle savedInstance) {

        super.onCreate(savedInstance);
        setContentView(R.layout.activity_maps);

        locationModel = new LocationModel();
        appDataSource = new AppDataSource(this);
        appDataSource.open();
        geofenceList = new ArrayList<Geofence>();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API) //adding locationServicesAPI
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //initaillizing layout variables
        locationName = (EditText) findViewById(R.id.et_location);
        btn_search = (Button) findViewById(R.id.btn_find);

        currentLocation = new Location("dummyprovider");//initializing location with namedProvider, lat=lng=0

        if (initMap()) {
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10 * 1000); // Update location every second

            searchButtonOnClickListener();
            mapClickedLongListener();
            markerClickListener();

        }

    }

    public void displayAlertBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Turn on the Internet and try Again")
                .setTitle("Connectivity Issues")
                .setCancelable(true)
                .setPositiveButton("OK", null);
        builder.show();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    public void searchButtonOnClickListener() {
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                if (isNetworkAvailable()) {
                    String location = locationName.getText().toString();
                    GeoLocate task = new GeoLocate();
                    task.execute(location);
                } else {
                    displayAlertBox();
                }
            }
        });
    }

    private void gotoLocation(double lat, double lng, float zoom) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.animateCamera(update);
    }

    private boolean initMap() {
        //checking if map is null
        if (mMap == null) {
            //try to obtain the map from supportFragment
            SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mMap = mapFrag.getMap();
        }
        return (mMap != null);
    }


    @Override
    public void onConnected(Bundle arg0) {
        Location location;
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient); //getting last location from google play services locationAPI
        if (location == null) {
            //requesting locationUpdate if last location is null
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            double currentLatitude = latLng.latitude;
            double currentLongitude = latLng.longitude;
            //equating currentLocation to LastLocation lat and long
            currentLocation.setLatitude(currentLatitude);
            currentLocation.setLongitude(currentLongitude);

            gotoLocation(currentLatitude, currentLongitude, DEFAULT_ZOOM);
            String locationName = getLocationName(currentLatitude, currentLongitude);
            //setting marker
            if (marker != null) {
                marker.remove();
            }
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(locationName);
            marker = mMap.addMarker(markerOptions);
        }

        if (flagButton == true) {
            //passing the deleted locationModels that were saved in a list in LocationListActivity
            removeGeofence(LocationListActivity.locationModelList);
            Log.i("Remove Geofence", LocationListActivity.locationModelList.toString());
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO Auto-generated method stub
        Log.i("Map activity", "GoogleApiClient connection has failed");

    }


    @Override
    public void onConnectionSuspended(int i) {
        // TODO Auto-generated method stub
        Log.i("Map Activity", "GoogleApiClient connection has been suspended");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);
        switch (menuItem.getItemId()) {
            case R.id.action_settings:
                //start the settings activity
                Intent intent = new Intent(MapsActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_saved_locations:
                //checking if the locationList in not null
                LocationListAdapter locationListAdapter = new LocationListAdapter(this, appDataSource.getLocationModelList());
                if (locationListAdapter.getCount() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "No Location Added Yet", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Intent intent1 = new Intent(MapsActivity.this, LocationListActivity.class);
                    startActivity(intent1);
                }
                break;
            case R.id.gotoCurrentLocation:
                if (isNetworkAvailable()) {
                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    Log.i("CurrentLocation", "Button Clicked");
                    ToCurrentLocation toCurrentLocation = new ToCurrentLocation();
                    toCurrentLocation.execute(latLng);
                } else {
                    displayAlertBox();
                }
                break;
            default:
                break;
        }

        return true;
    }

    public void hideSoftKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    @Override
    public void onLocationChanged(Location location) {
        //updating current Location coOrdinates
        currentLocation.setLatitude(location.getLatitude());
        currentLocation.setLongitude(location.getLongitude());
    }

    private String getLocationName(double latitude, double longitude) {
        String currentLocationName = "";
        if (!isNetworkAvailable()) {
            displayAlertBox();
        } else {
            //creating a geocoder instance
            Geocoder geocoder = new Geocoder(getBaseContext());
            try {
                //getting a maximum of one address
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    StringBuilder currentAddress = new StringBuilder();
                    //from zero to largest index currently used to specify an address line
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        currentAddress.append(address.getAddressLine(i)).append(" ");
                    }
                    currentLocationName = currentAddress.toString();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "No address found", Toast.LENGTH_LONG);
                    toast.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return currentLocationName;
    }

    //GeoLocate SubClass for Geocoding AsyncTask
    private class GeoLocate extends AsyncTask<String, Void, LatLng> {
        String locationName = " ";
        List<Address> addresses = new ArrayList<Address>();

        @Override
        protected LatLng doInBackground(String... params) {
            String locationName = params[0]; //getting the paramter at position 0
            double latitude = 0;
            double longitude = 0;
            //creating a geocoder instance
            Geocoder geocoder = new Geocoder(getBaseContext());
            try {
                addresses = geocoder.getFromLocationName(locationName, 1); //getting maximum 1 result
                if (addresses != null) {
                    Address address = addresses.get(0);
                    StringBuilder currentAddress = new StringBuilder();
                    //from zero to largest index currently used to specify an address line
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        currentAddress.append(address.getAddressLine(i)).append(" ");
                    }
                    this.locationName = currentAddress.toString();
                    latitude = address.getLatitude();
                    longitude = address.getLongitude();
                    Log.i("Geocoder", this.locationName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new LatLng(latitude, longitude);
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            if (addresses.size() == 0) {
                Toast toast = Toast.makeText(getApplicationContext(), "No address found", Toast.LENGTH_LONG);
                toast.show();
            } else {
                gotoLocation(latLng.latitude, latLng.longitude, DEFAULT_ZOOM);
                if (marker != null) {
                    marker.remove();
                }
                MarkerOptions markerOptions = new MarkerOptions()
                        .title(locationName)
                        .position(latLng);
                marker = mMap.addMarker(markerOptions);
            }
        }
    }

    //ToCurrentLocation SubClass for Reverse Geocoding AsyncTask
    private class ToCurrentLocation extends AsyncTask<LatLng, Void, LatLng> {
        String locationName = "";
        List<Address> addresses = new ArrayList<Address>();

        @Override
        public LatLng doInBackground(LatLng... params) {
            Log.i("Current Location", "Async Task");
            LatLng latLng = params[0]; //getting the parameter at position 0
            Geocoder geocoder = new Geocoder(getBaseContext());
            try {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addresses.size() > 0 && addresses != null) {
                    Address address = addresses.get(0);
                    StringBuilder currentAddress = new StringBuilder();
                    //from zero to largest index currently used to specify an address line
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        currentAddress.append(address.getAddressLine(i)).append(" ");
                    }
                    locationName = currentAddress.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new LatLng(latLng.latitude, latLng.longitude);
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            if (addresses.size() == 0) {
                Toast toast = Toast.makeText(getApplicationContext(), "No address found", Toast.LENGTH_LONG);
                toast.show();
            } else {
                gotoLocation(latLng.latitude, latLng.longitude, DEFAULT_ZOOM);
                if (marker != null) {
                    marker.remove();
                }
                MarkerOptions markerOptions = new MarkerOptions()
                        .title(locationName)
                        .position(latLng);
                marker = mMap.addMarker(markerOptions);
            }
        }
    }

    public void mapClickedLongListener() {

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                String address = getLocationName(latLng.latitude, latLng.longitude);
                if (marker != null) {
                    marker.remove();
                }
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(address);
                marker = mMap.addMarker(markerOptions);
            }
        });
    }

    public void markerClickListener() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
               // try {
                    dialog = new Dialog(MapsActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //flag for the "no title" at the top of screen
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
                    dialog.setContentView(R.layout.dialouge_box);
                    dialog.show();

                    tv_locationName = (TextView) dialog.findViewById(R.id.tv_location_value);
                    tv_latitude = (TextView) dialog.findViewById(R.id.tv_latitude_value);
                    tv_longitude = (TextView) dialog.findViewById(R.id.tv_longitude_value);

                    et_radius = (EditText) dialog.findViewById(R.id.et_radius_value);

                    btn_submit = (Button) dialog.findViewById(R.id.btn_Submit);
                    btn_viewGeofence = (Button) dialog.findViewById(R.id.btn_ViewGeofence);

                    //getting latLng of the marker clicked
                    LatLng latLng = marker.getPosition();
                    //displaying values
                    tv_locationName.setText(marker.getTitle());
                    tv_longitude.setText(Double.toString(latLng.longitude));
                    tv_latitude.setText(Double.toString(latLng.latitude));

                    locationModel.latitude = latLng.latitude;
                    locationModel.longitude = latLng.longitude;
                    locationModel.name = getLocationName(latLng.latitude, latLng.longitude);

                    viewGeofenceOnClickListener();
                    submitOnClickListener();

                /*} catch (Exception e) {
                    e.printStackTrace();
                }*/
                return false;
            }
        });

    }


    public void drawGeofence(LatLng point, double radius) {
        if (circle != null) {
            circle.remove();
        }

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(point);
        circleOptions.radius(radius);

        circleOptions.fillColor(0x26ff0000);
        circleOptions.strokeColor(0x26ff0000);
        circleOptions.strokeWidth(5);

        circle = mMap.addCircle(circleOptions);
    }

    public void viewGeofenceOnClickListener() {
        btn_viewGeofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                dialog.dismiss();
                Log.i("GEOfence button: ", "clicked");
                if (et_radius.getText().length() > 0) {
                    radius = Float.parseFloat(et_radius.getText().toString());
                    locationModel.radius = radius;
                    LatLng latLng = new LatLng(locationModel.latitude, locationModel.longitude);
                    drawGeofence(latLng, radius);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Enter a radius value", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    public void submitOnClickListener() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                dialog.dismiss();
                if (et_radius.getText().length() > 0) {
                    locationModel.radius = Float.parseFloat(et_radius.getText().toString());
                    //inserting locationModel in database
                    long id = appDataSource.createLocation(locationModel);
                    Toast toast;
                    if (id > 0) {
                        toast = Toast.makeText(getApplicationContext(), "Value Submitted Successfully", Toast.LENGTH_LONG);
                        toast.show();
                        //calling to update geoFences accordingly
                        Log.i("Map Activity", "setGeofence() called");
                        setGeofences();
                    } else {
                        toast = Toast.makeText(getApplicationContext(), "Value could not be submitted", Toast.LENGTH_LONG);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Enter a radius value", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });
    }

    public void setGeofences() {
        List<LocationModel> locationModelList = new ArrayList<>();
        //getting the locationModelList form DataBase
        locationModelList = appDataSource.getLocationModelList();

        if (locationModelList != null) {
            for (LocationModel locationModel : locationModelList) {
                //removing all the geoFences
                LocationServices.GeofencingApi.removeGeofences(googleApiClient,
                        createPendingIntent(locationModel));
                //Creating a new Geofence instance
                Geofence geofence = new Geofence.Builder()
                        //set request ID, the string to identify this geofence
                        .setRequestId(String.valueOf(locationModel.id))
                        //set circular geofence
                        .setCircularRegion(locationModel.latitude,
                                locationModel.longitude,
                                locationModel.radius)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE) //setting geofence never to expire
                        //setting tranistion types
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT).build();
                //adding the created geofence to geofenceList
                geofenceList.add(geofence);
                Log.i(locationModel.name, " added to the geofenceList");
                //submitting the request to monitor geofences
                LocationServices.GeofencingApi.addGeofences(googleApiClient,
                        getGeofencingRequest(geofenceList),
                        createPendingIntent(locationModel));
            }
        }
    }

    private GeofencingRequest getGeofencingRequest(List<Geofence> geofenceList) {
        //creating a geoFence request with the geofences we have stored in geofenceList
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent createPendingIntent(LocationModel locationModel) {
        Intent intent = new Intent(MapsActivity.this, LocationMuteService.class);
        intent.putExtra("ID", locationModel.id);
        intent.putExtra("name", locationModel.name);
        intent.putExtra("latitude", locationModel.latitude);
        intent.putExtra("longitude", locationModel.longitude);
        intent.putExtra("radius", locationModel.radius);

        return PendingIntent.getService(MapsActivity.this, (int) locationModel.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void removeGeofence(LocationModel[] locationModelList) {
        if (locationModelList != null && locationModelList.length > 0) {
            for (int i = 0; i < locationModelList.length; i++) {
                LocationModel locationModel = locationModelList[i];
                if (locationModel != null) {
                    Log.i("Removed Geofence", locationModel.name);
                    //removing geofences
                    LocationServices.GeofencingApi.removeGeofences(googleApiClient,
                            createPendingIntent(locationModel));
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        appDataSource.close();
        if (googleApiClient.isConnected()) {
            //removing location updates
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        appDataSource.open();
        googleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


}


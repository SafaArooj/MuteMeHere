package app.com.example.android.mutemehere;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import app.com.example.android.mutemehere.MapsActivity;


/**
 * Created by Safa Arooj on 7/29/2015.
 */
public class LocationListActivity extends ListActivity {

    AppDataSource appDataSource = new AppDataSource(this);

    private LocationListAdapter adapter;
    private Context context;

    static int i = 0;
    //list to keep deleted geofences
    static public LocationModel locationModelList[] = new LocationModel[10];



    @Override
    public void onCreate(Bundle savedInstance) {

        super.onCreate(savedInstance);

        appDataSource.open();

        setContentView(R.layout.saved_location_list);

        context = this;
        //initalizing the customized adapter
        adapter= new LocationListAdapter(this, appDataSource.getLocationModelList());
        //assign the adapter to list
        setListAdapter(adapter);
    }


    public void deleteLocation(long id) {
        final long locationId = id;
        if(i>9){
            Toast toast = Toast.makeText(context, "Can't delete more than ten values at one time", Toast.LENGTH_LONG);
            toast.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please confirm")
                    .setTitle("Delete?")
                    .setCancelable(true)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //getting the locationModel to be deleted
                            LocationModel deletedLocationModel = appDataSource.getSingleLocation(locationId);
                            Log.i("Location  Model", deletedLocationModel.name);
                            //storing the deleted model in locationModelList
                            locationModelList[i] = deletedLocationModel;
                            //deleting location from DB
                            appDataSource.deleteLocation(locationId);
                            i++; //incrementing i
                            Log.i("I value", i + "");
                            MapsActivity.flagButton = true; //setting button true, indicating deletion of atleast one item
                            //refresh the list of locationModel in the adaptor
                            adapter.setList(appDataSource.getLocationModelList());
                            //Notify the adapter the data has changed
                            adapter.notifyDataSetChanged();
                            Log.i("Loc Values", locationModelList.toString());

                        }
                    }).show();
        }
    }
}




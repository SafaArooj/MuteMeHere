package app.com.example.android.mutemehere;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Safa Arooj on 7/29/2015.
 */
public class LocationListAdapter extends BaseAdapter {

    private Context context;
    private List<LocationModel> locationModels;
    LayoutInflater inflater;

    public LocationListAdapter(Context context, List<LocationModel> locationModels){

        this.context = context;
        this.locationModels = locationModels;
    }

    public void setList(List<LocationModel> locationModels){
        this.locationModels = locationModels;
    }

    @Override
    public int getCount() {
        if (locationModels != null) {
            return locationModels.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (locationModels != null) {
            return locationModels.get(position).id;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (locationModels != null) {
            return locationModels.get(position);
        }
        return null;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        //check if existing view is being used, otherwise inflate the view
        if (view == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.saved_location, parent, false);// initializing saved_timespan.xml file into its corresponding View Objects
        }

        LocationModel locationModel = (LocationModel) getItem(position);

        TextView locationName = (TextView) view.findViewById(R.id.location_name);
        locationName.setText(locationModel.name);

        TextView textName = (TextView) view.findViewById(R.id.radius);
        textName.setText( Float.toString(locationModel.radius));

        ImageButton delete = (ImageButton) view.findViewById(R.id.imageButton);
        //saving id of the of timeSpanModel associated with the button clicked
        delete.setTag(locationModel.id);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LocationListActivity) context).deleteLocation((Long) v.getTag());
            }
        });
        //returning completed view to render on screen
        return view;

    }
}

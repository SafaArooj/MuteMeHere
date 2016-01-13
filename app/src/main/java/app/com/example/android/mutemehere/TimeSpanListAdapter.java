package app.com.example.android.mutemehere;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Safa Arooj on 5/18/2015.
 */

//custom adapter class
public class TimeSpanListAdapter extends BaseAdapter{

    private Context context;
    private List<TimeSpanModel> timeSpanModels;
    LayoutInflater inflater;

    public TimeSpanListAdapter(Context context, List<TimeSpanModel> timeSpanModels){

        this.context = context;
        this.timeSpanModels = timeSpanModels;
    }

    public void setList(List<TimeSpanModel> timeSpanModels){
        this.timeSpanModels = timeSpanModels;
    }

    @Override
    public int getCount() {
        if (timeSpanModels != null) {
            return timeSpanModels.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (timeSpanModels != null) {
            return timeSpanModels.get(position).id;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (timeSpanModels != null) {
            return timeSpanModels.get(position);
        }
        return null;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        //check if existing view is being used, otherwise inflate the view
        if (view == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.saved_timespan, parent, false); // initializing saved_timespan.xml file into its corresponding View Objects
        }
        //get the item at this position
        TimeSpanModel timeSpanModel = (TimeSpanModel) getItem(position);

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY,timeSpanModel.start_hour);
        startTime.set(Calendar.MINUTE, timeSpanModel.start_minute);

        Calendar finishTime = Calendar.getInstance();
        finishTime.set(Calendar.HOUR_OF_DAY,timeSpanModel.finish_hour);
        finishTime.set(Calendar.MINUTE, timeSpanModel.finish_minute);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        TextView textTime = (TextView) view.findViewById(R.id.timeSpan_value);
        textTime.setText(sdf.format(startTime.getTime()) + " - " + sdf.format(finishTime.getTime()));

        TextView textName = (TextView) view.findViewById(R.id.timeSpan_name);
        textName.setText(timeSpanModel.name);

        ImageButton delete = (ImageButton) view.findViewById(R.id.imageButton);

        changeTextColor((TextView) view.findViewById(R.id.sunday), timeSpanModel.getRepeatingDay(TimeSpanModel.SUNDAY));
        changeTextColor((TextView) view.findViewById(R.id.monday), timeSpanModel.getRepeatingDay(TimeSpanModel.MONDAY));
        changeTextColor((TextView) view.findViewById(R.id.tuesday), timeSpanModel.getRepeatingDay(TimeSpanModel.TUESDAY));
        changeTextColor((TextView) view.findViewById(R.id.wednesday), timeSpanModel.getRepeatingDay(TimeSpanModel.WEDNESDAY));
        changeTextColor((TextView) view.findViewById(R.id.thursday), timeSpanModel.getRepeatingDay(TimeSpanModel.THURSDAY));
        changeTextColor((TextView) view.findViewById(R.id.friday), timeSpanModel.getRepeatingDay(TimeSpanModel.FRIDAY));
        changeTextColor((TextView) view.findViewById(R.id.saturday), timeSpanModel.getRepeatingDay(TimeSpanModel.SATURDAY));
        //saving id of the of timeSpanModel associated with the button clicked
        delete.setTag(timeSpanModel.id);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TimeSpanListActivity) context).deleteTimeSpan((Long) v.getTag());
            }
        });
        //returning completed view to render on screen
        return view;

    }

    private void changeTextColor(TextView textView, boolean isOn){
        if(isOn){

            textView.setTextColor(Color.GREEN);
        }
        else{
            textView.setTextColor(Color.RED);
        }

    }
}



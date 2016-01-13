package app.com.example.android.mutemehere;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;


/**
 * Created by Safa Arooj on 5/21/2015.
 */
public class TimeSpanListActivity extends ListActivity {

    AppDataSource appDataSource = new AppDataSource(this);

    private TimeSpanListAdapter adapter;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstance) {

        super.onCreate(savedInstance);

        appDataSource.open();

        setContentView(R.layout.saved_timespan_list);

        context = this;
        //initializing the customized adapter
        adapter= new TimeSpanListAdapter(this, appDataSource.getTimeSpanList());
        //assign the list adapter
        setListAdapter(adapter);
    }

    public void deleteTimeSpan(long id) {
        final long timeSpanId = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please confirm")
                .setTitle("Delete?")
                .setCancelable(true)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Cancel timeSpans
                        TimeMuteManager.cancelTimeBasedMute(context);
                        //Delete timeSpan from DB by id
                        appDataSource.deleteTimeSpan(timeSpanId);
                        //Refresh the list of the timeSpan in the adaptor
                        adapter.setList(appDataSource.getTimeSpanList());
                        //Notify the adapter the data has changed
                        adapter.notifyDataSetChanged();
                        //Set the modified timeSpan
                        TimeMuteManager.setTimeBasedMute(context);
                    }
                }).show();
    }
}




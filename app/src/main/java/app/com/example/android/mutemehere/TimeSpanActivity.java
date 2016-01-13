package app.com.example.android.mutemehere;

import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Safa Arooj on 4/4/2015.
 */
public class TimeSpanActivity extends ActionBarActivity
        implements TimePickerDialog.OnTimeSetListener {

    private static int start = 0;
    private static int finish = 1;
    private int flag; //to indicate which textView was clicked

    private AppDataSource appDataSource;

    private int startingHour, startingMinute, finishingHour, finishingMinute;
    private TimeSpanModel timeSpanModel;

    private TextView displayStartTime, displayFinishTime;
    private Button submitTime, viewDetails;
    private EditText timeName;

    private CheckBox checkSunday, checkMonday, checkTuesday, checkWednesday,
            checkThursday, checkFriday, checkSaturday;

    private Calendar startTime, finishTime;

    protected void onCreate(Bundle savedInstance) {

        super.onCreate(savedInstance);

        setContentView(R.layout.time);

        displayStartTime = (TextView) findViewById(R.id.value_startTime);
        displayFinishTime = (TextView) findViewById(R.id.value_finishTime);

        submitTime = (Button) findViewById(R.id.bSubmit);
        viewDetails = (Button) findViewById(R.id.bView_savedAlarms);

        timeName = (EditText) findViewById(R.id.et_EventName);

        checkSunday = (CheckBox) findViewById(R.id.checkbox_sunday);
        checkMonday = (CheckBox) findViewById(R.id.checkbox_monday);
        checkTuesday = (CheckBox) findViewById(R.id.checkbox_tuesday);
        checkWednesday = (CheckBox) findViewById(R.id.checkbox_wednesday);
        checkThursday = (CheckBox) findViewById(R.id.checkbox_thursday);
        checkFriday = (CheckBox) findViewById(R.id.checkbox_friday);
        checkSaturday = (CheckBox) findViewById(R.id.checkbox_saturday);


        timeSpanModel = new TimeSpanModel();
        appDataSource = new AppDataSource(this);
        appDataSource.open();


        startTime = Calendar.getInstance();
        finishTime = Calendar.getInstance();

        addTvOnClickListener();
        submitButtonOnClickListener();
        viewButtonOnClickListener();

    }


    public void addTvOnClickListener() {
        displayStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = start;
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

        displayFinishTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = finish;
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
            }

        });

    }

    public void submitButtonOnClickListener() {
        submitTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timeSpanModel.name = timeName.getText().toString();
                timeSpanModel.start_hour = startingHour;
                timeSpanModel.start_minute = startingMinute;
                timeSpanModel.finish_hour = finishingHour;
                timeSpanModel.finish_minute = finishingMinute;

                timeSpanModel.setRepeatingDay(TimeSpanModel.SUNDAY, checkSunday.isChecked());
                timeSpanModel.setRepeatingDay(TimeSpanModel.MONDAY, checkMonday.isChecked());
                timeSpanModel.setRepeatingDay(TimeSpanModel.TUESDAY, checkTuesday.isChecked());
                timeSpanModel.setRepeatingDay(TimeSpanModel.WEDNESDAY, checkWednesday.isChecked());
                timeSpanModel.setRepeatingDay(TimeSpanModel.THURSDAY, checkThursday.isChecked());
                timeSpanModel.setRepeatingDay(TimeSpanModel.FRIDAY, checkFriday.isChecked());
                timeSpanModel.setRepeatingDay(TimeSpanModel.SATURDAY, checkSaturday.isChecked());


                if (finishTime.get(Calendar.HOUR_OF_DAY) == 0) {
                    finishTime.add(Calendar.DAY_OF_MONTH, 1);//as the day count increases by 1 after 12 PM
                }

                int a = finishTime.compareTo(startTime);

                if (a <= 0) {
                    Toast toast = Toast.makeText(TimeSpanActivity.this, "Finish Time cannot be less than Start Time", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    long id = appDataSource.createTimeSpan(timeSpanModel);
                    Toast toast;
                    if (id > 0) {
                        toast = Toast.makeText(getApplicationContext(), "Value Submitted Successfully", Toast.LENGTH_LONG);
                        toast.show();
                        Log.i("In TimeSpanActivity ", "setTimeBasedMute called");
                        //call to set the reschedule TimeSpan ALarms accordingly
                        TimeMuteManager.setTimeBasedMute(TimeSpanActivity.this);
                    } else {
                        toast = Toast.makeText(getApplicationContext(), "Value could not be submitted", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            }
        });
    }

    public void viewButtonOnClickListener() {
        viewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeSpanListAdapter adapter = new TimeSpanListAdapter(TimeSpanActivity.this, appDataSource.getTimeSpanList());
                if(adapter.getCount() == 0){

                    Toast toast = Toast.makeText(getApplicationContext(), "First Submit a Value" , Toast.LENGTH_LONG);
                    toast.show();
                }
                else {

                    Intent timeList = new Intent(TimeSpanActivity.this, TimeSpanListActivity.class);
                    startActivity(timeList);
                }


            }
        });
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        // set selected time into textview
        if (flag == start) {
            startingHour = hourOfDay;
            startingMinute = minute;

            startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            startTime.set(Calendar.MINUTE, minute);

            displayStartTime.setText(sdf.format(startTime.getTime()));
        } else if (flag == finish) {
            finishingHour = hourOfDay;
            finishingMinute = minute;

            finishTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            finishTime.set(Calendar.MINUTE, minute);

            displayFinishTime.setText(sdf.format(finishTime.getTime()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_span,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);
        switch (menuItem.getItemId()) {
            case R.id.action_settings:
                //start the settings activity
                Intent intent = new Intent(TimeSpanActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        appDataSource.open();
    }

    @Override
    public void onPause(){
        super.onPause();
        appDataSource.close();
    }




}

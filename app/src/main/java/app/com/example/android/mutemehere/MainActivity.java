package app.com.example.android.mutemehere;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    //setting up button variables
    private Button time, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        time = (Button) findViewById(R.id.b_time);
        location = (Button) findViewById(R.id.b_location);

        //setting onClickListener for both buttons

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent timeActivity = new Intent(MainActivity.this,TimeSpanActivity.class);
                startActivity(timeActivity);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapsActivity = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(mapsActivity);
            }
        });

    }

}

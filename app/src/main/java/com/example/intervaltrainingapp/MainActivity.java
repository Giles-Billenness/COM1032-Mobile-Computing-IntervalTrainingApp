package com.example.intervaltrainingapp;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * This class is mainly used for user data collection. The infomation this activity gathers is sent to the timer activity for further processing.
 * @author Giles Billenness
 */
public class MainActivity extends AppCompatActivity {

    //set initial states of each variable sent to the timer activity
    //These are the boolean values used to identify what features are used.
    private boolean timeractive = false;
    private boolean beeperactive = false;
    private boolean pauseactive = false;

    //These values are used for the timer
    private int timerhourvalue = 0;
    private int timerminvalue = 0;
    private int timersecvalue = 0;

    //These values are used for the beeper
    private int beephourvalue = 0;
    private int beepminvalue = 0;
    private int beepsecvalue = 0;

    //these values are used for the timer pause, specifically the interval in which the timer should be paused periodically
    private int pausetimehourvalue = 0;
    private int pausetimeminvalue = 0;
    private int pausetimesecvalue = 0;

    //These values are used for the timer pause for the length of time the timer should be paused for.
    private int pauselengthhourvalue = 0;
    private int pauselengthminvalue = 0;
    private int pauselengthsecvalue = 0;

    public MainActivity(){
        //zero arg constructor required for this implementation
    }


    //sets up the data structure with the textviews of the main activity

    /**
     * This constructor is used to set the active feature booleans and provide a simple way to reference values for intent data transmission
     * @param timerhour
     * @param timermin
     * @param timersec
     * @param beephour
     * @param beepmin
     * @param beepsec
     * @param pausetimehour
     * @param pausetimemin
     * @param pausetimesec
     * @param pauselengthhour
     * @param pauselengthmin
     * @param pauselengthsec
     */
    public MainActivity(TextView timerhour, TextView timermin, TextView timersec, TextView beephour, TextView beepmin, TextView beepsec, TextView pausetimehour, TextView pausetimemin, TextView pausetimesec, TextView pauselengthhour, TextView pauselengthmin, TextView pauselengthsec) {//create object to store the data
        super();//set which features are active if any of the data values are >0
        if (Integer.parseInt(timerhour.getText().toString()) > 0 | Integer.parseInt(timermin.getText().toString()) > 0 | Integer.parseInt(timersec.getText().toString()) > 0) {
            this.timeractive = true;
            timeractive = true;
        }
        if (Integer.parseInt(beephour.getText().toString()) > 0 | Integer.parseInt(beepmin.getText().toString()) > 0 | Integer.parseInt(beepsec.getText().toString()) > 0) {
            this.beeperactive = true;
            beeperactive = true;
        }
        if (Integer.parseInt(pausetimehour.getText().toString()) > 0 | Integer.parseInt(pausetimemin.getText().toString()) > 0 | Integer.parseInt(pausetimesec.getText().toString()) > 0) {
            this.pauseactive = true;
            pauseactive = true;
        }
        //set all the values needed by getting the text and parsing to int
        this.timerhourvalue = Integer.parseInt(timerhour.getText().toString());
        this.timerminvalue = Integer.parseInt(timermin.getText().toString());
        this.timersecvalue = Integer.parseInt(timersec.getText().toString());

        this.beephourvalue = Integer.parseInt(beephour.getText().toString());
        this.beepminvalue = Integer.parseInt(beepmin.getText().toString());
        this.beepsecvalue = Integer.parseInt(beepsec.getText().toString());

        this.pausetimehourvalue = Integer.parseInt(pausetimehour.getText().toString());
        this.pausetimeminvalue = Integer.parseInt(pausetimemin.getText().toString());
        this.pausetimesecvalue = Integer.parseInt(pausetimesec.getText().toString());

        this.pauselengthhourvalue = Integer.parseInt(pauselengthhour.getText().toString());
        this.pauselengthminvalue = Integer.parseInt(pauselengthmin.getText().toString());
        this.pauselengthsecvalue = Integer.parseInt(pauselengthsec.getText().toString());
    }

    @Override//the method executed on activity creation
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //sets up the textveiws on the ui for data collection
        final TextView timerhour = findViewById(R.id.timerHour);
        final TextView timermin = findViewById(R.id.timermin);
        final TextView timersec = findViewById(R.id.timersec);

        final TextView beephour = findViewById(R.id.beephour);
        final TextView beepmin = findViewById(R.id.beepmin);
        final TextView beepsec = findViewById(R.id.beepsec);

        final TextView pausetimehour = findViewById(R.id.pausetimehour);
        final TextView pausetimemin = findViewById(R.id.pausetimemin);
        final TextView pausetimesec = findViewById(R.id.pausetimesec);

        final TextView pauselengthhour = findViewById(R.id.pauselengthhour);
        final TextView pauselengthmin = findViewById(R.id.pauselengthmin);
        final TextView pauselengthsec = findViewById(R.id.pauselengthsec);


        //the start button
        //Button sButton = findViewById(R.id.Startbutton);
        //sButton.setOnClickListener(this);
        final Intent intent = new Intent(this, Timer.class);//creates the intent
        Button mButton = findViewById(R.id.Startbutton);//getting the button
        mButton.setOnClickListener(new View.OnClickListener() {//creates an on click listener
            @Override
            public void onClick(View view) {//on start button click
                //create MainActivity object with attributes once the button is clicked to start the timer.
                MainActivity mActivity = new MainActivity(timerhour, timermin, timersec, beephour, beepmin, beepsec, pausetimehour, pausetimemin, pausetimesec, pauselengthhour, pauselengthmin, pauselengthsec);//creates the data object
                if (mActivity.getTimerActive()) {

                    //Bundle extras = new Bundle();
                    //Puts all the data into the intents extra each with a reference and a value
                    //the first variable is the tag or name of the value that's being sent to the other activity and the second is the value.
                    intent.putExtra("timerhourvalue", mActivity.getTimerhourvalue());
                    intent.putExtra("timerminvalue", mActivity.getTimerminvalue());
                    intent.putExtra("timersecvalue", mActivity.getTimersecvalue());


                    intent.putExtra("beephourvalue", mActivity.getBeephourvalue());
                    intent.putExtra("beepminvalue", mActivity.getBeepminvalue());
                    intent.putExtra("beepsecvalue", mActivity.getBeepsecvalue());

                    intent.putExtra("pausetimehourvalue", mActivity.getPausetimehourvalue());
                    intent.putExtra("pausetimeminvalue", mActivity.getPausetimeminvalue());
                    intent.putExtra("pausetimesecvalue", mActivity.getPausetimesecvalue());

                    intent.putExtra("pauselengthhourvalue", mActivity.getPauselengthhourvalue());
                    intent.putExtra("pauselengthminvalue", mActivity.getPauselengthminvalue());
                    intent.putExtra("pauselengthsecvalue", mActivity.getPauselengthsecvalue());

                    intent.putExtra("timeractive", mActivity.getTimerActive());
                    intent.putExtra("beeperactive", mActivity.getBeeperActive());
                    intent.putExtra("pauseactive", mActivity.getPauseActive());

                    //extras.putInt("timerhourvalue", mActivity.getTimerhourvalue());
                    //extras.putBoolean("timeractive", mActivity.getTimerActive());
                    //intent.putExtra(data);

                    ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();//creates a mem info object with the available mem
                    if(!memoryInfo.lowMemory) {// Checks if memory is low before starting the next activity
                        startActivity(intent);
                    }
                }

            }
        });
    }

    private ActivityManager.MemoryInfo getAvailableMemory() {//used to get the memory of the device
        ActivityManager activityManager = (ActivityManager)
                this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    //all the getters for easy data transfer
    public boolean getTimerActive() {
        return this.timeractive;
    }

    public boolean getBeeperActive() {
        return this.beeperactive;
    }

    public boolean getPauseActive() {
        return this.pauseactive;
    }

    public int getTimerhourvalue() {
        return timerhourvalue;
    }

    public int getTimerminvalue() {
        return timerminvalue;
    }

    public int getTimersecvalue() {
        return timersecvalue;
    }

    public int getBeephourvalue() {
        return beephourvalue;
    }

    public int getBeepminvalue() {
        return beepminvalue;
    }

    public int getBeepsecvalue() {
        return beepsecvalue;
    }

    public int getPausetimehourvalue() {
        return pausetimehourvalue;
    }

    public int getPausetimeminvalue() {
        return pausetimeminvalue;
    }

    public int getPausetimesecvalue() {
        return pausetimesecvalue;
    }

    public int getPauselengthhourvalue() {
        return pauselengthhourvalue;
    }

    public int getPauselengthminvalue() {
        return pauselengthminvalue;
    }

    public int getPauselengthsecvalue() {
        return pauselengthsecvalue;
    }
}

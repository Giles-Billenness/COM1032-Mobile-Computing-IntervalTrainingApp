package com.example.intervaltrainingapp;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * This class is used for the timer activity that displays the timer to the user and deals with the behavior of the different features of the app.
 * This class implements sensor event listener to easily monitor the accelerometer, to check if the device is held upright.
 *
 * @author Giles Billenness
 */
public class Timer extends AppCompatActivity implements SensorEventListener {
    private static final String timerTag = "Timer.java";//creates a tag for logging
    /**
     * The handler is used to change values on the ui thread, specifically the values of the text views, so the user can see the time on the timer
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {//updates timer ui
                    //initalise textviews
                    sec = findViewById(R.id.Timerseconds);
                    min = findViewById(R.id.Timermin);
                    hour = findViewById(R.id.Timerhour);

                    //math for getting the values for h,m,s from s(i from the timer loop)
                    int totalsec = Integer.parseInt(msg.obj.toString());//total sec
                    int remainsec = (totalsec % 60);
                    int totalmin = ((totalsec - remainsec) / 60);
                    int remainmin = (totalmin % 60);
                    int totalhour = ((totalmin - remainmin) / 60);

                    try {//try to set the text appropriately
                        //had to convert to string after the math (int)
                        sec.setText(/*remainsec.toString()*/ Integer.toString(remainsec));
                        min.setText(/*remainmin*/ Integer.toString(remainmin));
                        hour.setText(/*totalhour*/ Integer.toString(totalhour));
                    } catch (Exception e) {//if an exception happens
                        e.printStackTrace();//print the stack trace
                    }
                    break;
                }

            }
        }
    };

    //creates the text views
    private TextView sec = null;
    private TextView min = null;
    private TextView hour = null;

    private RelativeLayout layout;//can be used for simple screen touch stop threads

    //sets inital states
    private boolean timeractive = false;
    private boolean beeperactive = false;
    private boolean pauseactive = false;

    private int timerhourvalue = 0;
    private int timerminvalue = 0;
    private int timersecvalue = 0;

    private int beephourvalue = 0;
    private int beepminvalue = 0;
    private int beepsecvalue = 0;

    private int pausetimehourvalue = 0;
    private int pausetimeminvalue = 0;
    private int pausetimesecvalue = 0;

    private int pauselengthhourvalue = 0;
    private int pauselengthminvalue = 0;
    private int pauselengthsecvalue = 0;

    //creates the threads
    private Thread timerthread = null;
    private Thread beepthread = null;
    private Thread pausethread = null;

    //for accelerometer
    private SensorManager sensorMan = null;
    private Sensor accelerometer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//on activity creation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        //gets the intent info
        Intent intent = getIntent();
        //Bundle extras= intent.getExtras();
        //set values
        timerhourvalue = intent.getIntExtra("timerhourvalue", 0);

        timerminvalue = intent.getIntExtra("timerminvalue", 0);
        timersecvalue = intent.getIntExtra("timersecvalue", 0);

        beephourvalue = intent.getIntExtra("beephourvalue", 0);
        beepminvalue = intent.getIntExtra("beepminvalue", 0);
        beepsecvalue = intent.getIntExtra("beepsecvalue", 0);

        pausetimeminvalue = intent.getIntExtra("pausetimeminvalue", 0);
        pausetimesecvalue = intent.getIntExtra("pausetimesecvalue", 0);

        pauselengthhourvalue = intent.getIntExtra("pauselengthhourvalue", 0);
        pauselengthminvalue = intent.getIntExtra("pauselengthminvalue", 0);
        pauselengthsecvalue = intent.getIntExtra("pauselengthsecvalue", 0);

        timeractive = intent.getBooleanExtra("timeractive", false);
        beeperactive = intent.getBooleanExtra("beeperactive", false);
        pauseactive = intent.getBooleanExtra("pauseactive", false);
        //timerhourvalue=extras.getInt("timerhourvalue");
        //timerminvalue=extras.getInt("timerminvalue");
        //timersecvalue=extras.getInt("timersecvalue");

        /*layout = findViewById(R.id.layoutid);//can use this so on touch it should pause the threads for 5s
        //but it doesnt work, just pauses ui
        //layout.setOnTouchListener();
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    synchronized (timerthread) {
                        timerthread.wait(1000*5);
                    }
                    synchronized (beepthread) {
                        beepthread.wait(1000*5);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });*/

        //getting the accelerometer ready and sensor listener
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        //give the runnables to the threads
        timerthread = new Thread(timerrunnable);
        beepthread = new Thread(beeprunnable);
        pausethread = new Thread(pauserunnable);

        //start threads
        timerthread.start();
        beepthread.start();
        //pausethread.start();//commented out as it isn't needed as the desired functionality wasn't possible to implement in time
        Toast.makeText(this, "Starting Timer", Toast.LENGTH_SHORT).show();//show message to user
    }

    @Override
    public void onSensorChanged(SensorEvent event) {//when the sensor changes then
        if (event.values[1] > 9) {//if gravity is pointing down therefore phone is upright then
            timerthread.interrupt();//cancel the threads
            beepthread.interrupt();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //not used
    }

    @Override
    public void onDestroy() {//when the activity is destroyed
        super.onDestroy();
        timerthread.interrupt();
        beepthread.interrupt();
    }

    @Override
    public void onStop() {//when the activity is stopped
        super.onStop();
        timerthread.interrupt();
        beepthread.interrupt();
        Toast.makeText(this, "Thanks for using my app", Toast.LENGTH_SHORT).show();
    }

    @Override//can detect and do something if screen is swiped up or down during counter
    public boolean onTouchEvent(MotionEvent event) {//if the user swipes up then the threads processing is ended
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case (MotionEvent.ACTION_UP):
                Log.d(timerTag, "Swipe Action was Up");
                timerthread.interrupt();//skips the waits on the timer thread to completion
                beepthread.interrupt();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }


    //Below are the runnables used with the threads for the timers features.
    Runnable timerrunnable = new Runnable() {//This runnable is used to have a timer that updates the ui.
        public void run() {//run by the thread
            int totalseconds = (timerhourvalue * 60 * 60) + (timerminvalue * 60) + timersecvalue;
            for (int i = 0; i <= totalseconds; i++) {//goes up to user timer value
                try {
                    Thread.sleep(1000);//1000 milliseconds is one second. Waits 1 second
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                //update ui with current second value
                Log.d(timerTag, "Updating ui with seconds = " + i);
                handler.sendMessage(handler.obtainMessage(1, i));

                //pause if the i seconds is in the interval to pause set by the user and i!=0 and the pause feature is active
                if (pauseactive && i % ((pausetimehourvalue * 60 * 60) + (pausetimeminvalue * 60) + pausetimesecvalue) == 0 && i != 0) {//if pause condition is met
                    //pause timer and beep thread for this long (pauselengthhourvalue*60*60) + (pauselengthminvalue*60) +pauselengthsecvalue
                    try {
                        Log.d(timerTag, "Attempting to pause the other threads");
                        Log.d(timerTag, "and after every: " + ((pausetimehourvalue * 60 * 60) + (pausetimeminvalue * 60) + pausetimesecvalue));
                        Log.d(timerTag, "For this amount of seconds: " + ((pauselengthhourvalue * 60 * 60) + (pauselengthminvalue * 60) + pauselengthsecvalue));
                        //pause timer
                        synchronized (timerthread) {
                            try {
                                Log.d(timerTag, "timer thread wait");
                                Thread.sleep(1000 * ((pauselengthhourvalue * 60 * 60) + (pauselengthminvalue * 60) + pauselengthsecvalue));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    //This runnable is used for the beep feature of the app, implementing the pause method if the conditions are met
    Runnable beeprunnable = new Runnable() {
        public void run() {
            if (beeperactive) {//if beep feature is active
                Log.d(timerTag, "Running beep thread");
                //int totalseconds = (beephourvalue * 60 * 60) + (beepminvalue * 60) + beepsecvalue;

                for (int i = 0; i <= (timerhourvalue * 60 * 60) + (timerminvalue * 60) + timersecvalue; i++) {//use the timer time limit
                    try {
                        Thread.sleep(1000);//1000 milliseconds is one second.
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    if (i % ((beephourvalue * 60 * 60) + (beepminvalue * 60) + beepsecvalue) == 0 && i != 0) {//if beep condition is met
                        //make beep sound
                        Log.d(timerTag, "Playing beep now");
                        ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);//creates tone generator object
                        toneGen.startTone(ToneGenerator.TONE_CDMA_PIP, 150);//uses the tone generator to make a tone for 150ms (basically a beep)
                    }
                    //pause
                    if (pauseactive && i % ((pausetimehourvalue * 60 * 60) + (pausetimeminvalue * 60) + pausetimesecvalue) == 0 && i != 0) {//if pause condition is met
                        //pause timer and beep thread for this long (pauselengthhourvalue*60*60) + (pauselengthminvalue*60) +pauselengthsecvalue
                        try {
                            Log.d(timerTag, "Attempting to pause the other threads");
                            Log.d(timerTag, "and after every: " + ((pausetimehourvalue * 60 * 60) + (pausetimeminvalue * 60) + pausetimesecvalue));
                            Log.d(timerTag, "For this amount of seconds: " + ((pauselengthhourvalue * 60 * 60) + (pauselengthminvalue * 60) + pauselengthsecvalue));
                            //pause timer
                            synchronized (timerthread) {
                                try {
                                    Log.d(timerTag, "timer thread wait");
                                    Thread.sleep(1000 * ((pauselengthhourvalue * 60 * 60) + (pauselengthminvalue * 60) + pauselengthsecvalue));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
    };


    //cant get this to work, this runnable was ment to pause the other threads but wait and sleep just pause this thread and not the others.
    //ive implemented the pause feature in the other runnables to make up for this.
    Runnable pauserunnable = new Runnable() {
        public void run() {
            if (pauseactive) {//if pause feature active
                Log.d(timerTag, "Running pause thread");//log
                //int totalseconds = (beephourvalue * 60 * 60) + (beepminvalue * 60) + beepsecvalue;
                for (int i = 0; i <= (timerhourvalue * 60 * 60) + (timerminvalue * 60) + timersecvalue; i++) {//use the timer time limit
                    try {
                        Thread.sleep(1000);//1000 milliseconds is one second.
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    if (i % ((pausetimehourvalue * 60 * 60) + (pausetimeminvalue * 60) + pausetimesecvalue) == 0 && i != 0) {//if pause condition is met
                        //pause timer and beep thread for this long (pauselengthhourvalue*60*60) + (pauselengthminvalue*60) +pauselengthsecvalue
                        try {
                            Log.d(timerTag, "Attempting to pause the other threads");
                            Log.d(timerTag, "and after every: " + ((pausetimehourvalue * 60 * 60) + (pausetimeminvalue * 60) + pausetimesecvalue));
                            Log.d(timerTag, "For this amount of seconds: " + ((pauselengthhourvalue * 60 * 60) + (pauselengthminvalue * 60) + pauselengthsecvalue));
                            //pause timer
                            synchronized (timerthread) {//thread synchronisation
                                try {
                                    Log.d(timerTag, "timer thread wait");
                                    //was ment to pause the thread specified but actually pauses the thread running this code
                                    timerthread.wait(1000 * ((pauselengthhourvalue * 60 * 60) + (pauselengthminvalue * 60) + pauselengthsecvalue));
                                    //timerthread.interrupt();
                                    //timerthread.sleep(1000*((pauselengthhourvalue*60*60) + (pauselengthminvalue*60) +pauselengthsecvalue));
                                    //timerthread.suspend();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            //pause beeper
                            synchronized (beepthread) {
                                try {
                                    Log.d(timerTag, "beep thread wait");
                                    //beepthread.wait();
                                    //beepthread.interrupt();
                                    //beepthread.sleep(1000*((pauselengthhourvalue*60*60) + (pauselengthminvalue*60) +pauselengthsecvalue));
                                    beepthread.wait(1000 * ((pauselengthhourvalue * 60 * 60) + (pauselengthminvalue * 60) + pauselengthsecvalue));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            //Thread.sleep(1000*((pauselengthhourvalue*60*60) + (pauselengthminvalue*60) +pauselengthsecvalue));
                            //resume timer
                            synchronized (timerthread) {
                                try {
                                    Log.d(timerTag, "timer thread notify");
                                    //timerthread.notify();//methods to resume the thread
                                    //timerthread.resume();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            //resume beeper
                            synchronized (beepthread) {
                                try {
                                    Log.d(timerTag, "beep thread notify");
                                    //beepthread.notify();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                ;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };
}

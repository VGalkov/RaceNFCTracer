package ru.galkov.racenfctracer.adminLib;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.yandex.mapkit.MapKitFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.FaceControllers.ActivityFaceController;
import ru.galkov.racenfctracer.FaceControllers.HelpFaceController;
import ru.galkov.racenfctracer.FaceControllers.MapViewController;
import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.R;
import ru.galkov.racenfctracer.common.AskCurrentRaceStart;
import ru.galkov.racenfctracer.common.AskMapPoints;
import ru.galkov.racenfctracer.common.AskRaceStructure;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.AskStartSructure;
import ru.galkov.racenfctracer.common.SendActiveRaceStart;

import static ru.galkov.racenfctracer.MainActivity.MV;
import static ru.galkov.racenfctracer.MainActivity.TimerDelay;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;
import static ru.galkov.racenfctracer.MainActivity.getTimerTimeout;
import static ru.galkov.racenfctracer.MainActivity.mapview;
import static ru.galkov.racenfctracer.MainActivity.setStartDate;
import static ru.galkov.racenfctracer.MainActivity.setStopDate;
import static ru.galkov.racenfctracer.common.Utilites.messager;

public class ActivityRaceSetup  extends AppCompatActivity {

    public ActivityRaceSetupController ARSController;
    public Context activity;
    private SimpleDateFormat formatForDate = MainActivity.formatForDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_setup);
        setActivity(this);
        if  (ARSController ==null) {
            ARSController = new ActivityRaceSetupController();
            ARSController.start();
        }
        else { ARSController.restart(); }
    }
    @Override
    protected void onStop() {
        super.onStop();
        try {
            mapview.onStop();
            MapKitFactory.getInstance().onStop();
        }
        catch (NullPointerException e) { e.printStackTrace();}
        ARSController.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mapview.onStart();
            MapKitFactory.getInstance().onStart();
        }
        catch (NullPointerException e) { e.printStackTrace();}
    }
    @Override
    protected void onResume() {
        super.onResume();
        try {
            mapview.onStart();
            MapKitFactory.getInstance().onStart();
        }
        catch (NullPointerException e) { e.printStackTrace();}
        ARSController.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mapview.onStop();
            MapKitFactory.getInstance().onStop();
        }
        catch (NullPointerException e) { e.printStackTrace();}
        ARSController.stop();
    }
    public void setActivity(Context activity1) {
        activity = activity1;
    }

    public Context  getActivity() {
        return activity;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.race_setup_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){

            case R.id.help:
                setContentView(R.layout.activity_help_system);
                HelpFaceController HFC = new HelpFaceController();
                HFC.setEkran((TextView) findViewById(R.id.ekran));
                HFC.setHelpTopic(getString(R.string.raceSetupHelp));
                HFC.start();
                return true;

            case R.id.map:
                setContentView(R.layout.activity_map);
                mapview = findViewById(R.id.mapview);
                mapview.onStart();
                MapKitFactory.getInstance().onStart();
                // активные элементы view надо ли?
                MV = new MapViewController(mapview);
                MV.start();
                // управляет размещением объектов на карте
                // асинхронно запросить все поинты и разместить их на карте.
                AskMapPoints AMP = new AskMapPoints();
                AMP.setMapView(mapview);
                AMP.execute();
                return true;
            case R.id.exit:
                setResult(RESULT_OK, new Intent());
                finish();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }


    public class ActivityRaceSetupController extends ActivityFaceController {

        private TextView ServerTime, loginInfo, gpsPosition, showStart, showStop, raceConfig;
        private ImageButton back_button;
        private Timer ServerTimer;
        private Button setRaceConfig_button;
        public Spinner spinnerRace, spinnerStart;
        private boolean isStarted = false;
        private Calendar dateAndTime=Calendar.getInstance();
        String dt = "";

        ActivityRaceSetupController() {
            super();
        }

        private void startTimeSync() {
            ServerTimer = new Timer();
            ServerTimer.schedule(new TimerTask() { // Определяем задачу
                @Override
                public void run() {new AskServerTime(ServerTime).execute();}
            },
                    TimerDelay, getTimerTimeout());
        }


        @Override
        protected void initViewObjects() {

            back_button = findViewById(R.id.back_button);
            ServerTime = findViewById(R.id.ServerTime);
            setRaceConfig_button = findViewById(R.id.setRaceConfig_button);
            setRaceConfig_button.setEnabled(false);
            spinnerStart = findViewById(R.id.spinnerStart);
            spinnerRace = findViewById(R.id.spinnerRace);
            raceConfig = findViewById(R.id.raceConfig);
            loginInfo =             findViewById(R.id.loginInfo);
            ServerTime =             findViewById(R.id.ServerTime);
            gpsPosition=             findViewById(R.id.gpsPosition);
            showStart =             findViewById(R.id.showStart);
            showStop =             findViewById(R.id.showStop);
        }


        private void setInitialDateTime(TextView ekran1) {

            ekran1.setText(dt);
            try {
                    if (ekran1.equals(showStart)) {
                        setStartDate(formatForDate.parse(ekran1.getText().toString())); }
                    else {
                        setStopDate(formatForDate.parse(ekran1.getText().toString())); }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

// https://metanit.com/java/android/18.1.php
        void setUpDateTime(final TextView ekran1) {

            DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    dt = year + "-" + monthOfYear + "-" + dayOfMonth;

            TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            dt = dt + " " + hourOfDay + ":" + minute + ":00";
                            setInitialDateTime(ekran1);
                    }
            };

                    new TimePickerDialog(ActivityRaceSetup.this, t,
                            dateAndTime.get(Calendar.HOUR_OF_DAY),
                            dateAndTime.get(Calendar.MINUTE), true).show();
                }
            };

            new DatePickerDialog(ActivityRaceSetup.this, d,
                    dateAndTime.get(Calendar.YEAR),
                    dateAndTime.get(Calendar.MONTH),
                    dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
        }


        @Override
        protected void addListeners() {
//  https://metanit.com/java/android/18.1.php

            showStart.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    setUpDateTime(showStart);
                }
            });

            showStop.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    setUpDateTime(showStop);
                }
            });

            back_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
            });

            setRaceConfig_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    SendActiveRaceStart SARS = new SendActiveRaceStart(raceConfig);
                    SARS.setRace_id(Long.parseLong(spinnerRace.getSelectedItem().toString()));
                    SARS.setStart_id(Long.parseLong(spinnerStart.getSelectedItem().toString()));
                    SARS.execute();
                }
            });

          spinnerStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    setRaceConfig_button.setEnabled(true);
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    messager(getActivity(), "Нужно что-то выбрать!");
                }
            });

          spinnerRace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
              @Override
              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  AskStartSructure AStartStr = new AskStartSructure();
                  AStartStr.setRaceID(Long.parseLong(spinnerRace.getSelectedItem().toString())); // значение из выбранного спинером.
                  AStartStr.setActivityContext(getActivity());
                  AStartStr.setStartSpiner(spinnerStart);
                  AStartStr.execute();
              }
              @Override
              public void onNothingSelected(AdapterView<?> arg0) {
                  messager(getActivity(), "Нужно что-то выбрать!");
              }
          });
        }

        @Override
        public boolean isStarted() {
            return isStarted;
        }


        @Override
        protected void setDefaultFace() {
            constructStatusString();
            AskCurrentRaceStart ACRS = new AskCurrentRaceStart(raceConfig, showStart, showStop);
            ACRS.execute();

            // спинеры собираются тут ...
            //AskRaceStructure ARaceS = new AskRaceStructure(ARSController);
            AskRaceStructure ARaceS = new AskRaceStructure();
            ARaceS.setActivityContext(getActivity());
            ARaceS.setRaceSpiner(spinnerRace);
            ARaceS.execute();
        }

        @Override
        public void start() {
            startTimeSync();
            MainActivity.setGPSMonitor(gpsPosition);
            isStarted = true;
        }

        @Override
        public void stop() {
            if (ServerTimer!=null) { ServerTimer.cancel(); }
            isStarted = false;
        }


        private void constructStatusString() {
            String str = getLogin() + ":" + getLevel();
            loginInfo.setText(str);
        }
    }

    }


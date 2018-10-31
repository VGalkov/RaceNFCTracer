package ru.galkov.racenfctracer.adminLib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.FaceControllers.ActivityFaceController;
import ru.galkov.racenfctracer.FaceControllers.HelpFaceController;
import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.R;
import ru.galkov.racenfctracer.common.AskCurrentRaceStart;
import ru.galkov.racenfctracer.common.AskRaceStructure;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.AskStartSructure;
import ru.galkov.racenfctracer.common.GPS;
import ru.galkov.racenfctracer.common.SendActiveRaceStart;
import ru.galkov.racenfctracer.common.Utilites;

import static ru.galkov.racenfctracer.MainActivity.TimerDelay;

public class ActivityRaceSetup  extends AppCompatActivity {

    public ActivityRaceSetupController ARSController;
    public Context activity;
    private HelpFaceController HFC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_setup);
        setActivity(this);
        ARSController = new ActivityRaceSetupController();
        ARSController.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ARSController.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        // getWindow().getDecorView().findViewById(android.R.id.content)
        int id = item.getItemId();
        switch(id){

            case R.id.help:
                setContentView(R.layout.activity_help_system);
                HFC = new HelpFaceController();
                HFC.setEkran((TextView) findViewById(R.id.ekran));
                HFC.setHelpTopic(getString(R.string.raceSetupHelp));
                HFC.start();
                return true;


            case R.id.exit:
                setResult(RESULT_OK, new Intent());
                finish();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }


    public class ActivityRaceSetupController extends ActivityFaceController {
        private TextView ServerTime;
        private Button back_button;
        private Timer ServerTimer;
        private TextView raceConfig;
        private Button setRaceConfig_button;
        public Spinner spinnerRace;
        public Spinner spinnerStart;
        private long race_id = 0L;
        private long start_id = 0L;
        private TextView loginInfo;
        private TextView gpsPosition;
        private boolean isStarted = false;
        private DatePicker datePicker;
        private Calendar today = Calendar.getInstance();
        private final Calendar calendar = Calendar.getInstance();
        private int year = calendar.get(Calendar.YEAR);
        private int month = calendar.get(Calendar.MONTH);
        private int day = calendar.get(Calendar.DAY_OF_MONTH);
        private String changeType = MainActivity.changeType.start.toString();
        private Switch changeTypeSw;

        ActivityRaceSetupController() {
            super();
        }


        private void startTimeSync() {
            ServerTimer = new Timer();
            ServerTimer.schedule(new TimerTask() { // Определяем задачу
                @Override
                public void run() {new AskServerTime(ServerTime).execute();}
            },
                    TimerDelay, MainActivity.getTimerTimeout());
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
            datePicker =            findViewById(R.id.datePicker);
            changeTypeSw =            findViewById(R.id.changeTypeSw);
        }


        @Override
        protected void addListeners() {

            changeTypeSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        changeTypeSw.setText(R.string.startTime);
                        changeType = MainActivity.changeType.start.toString();
                    }
                    else  {
                        changeTypeSw.setText(R.string.stopTime);
                        changeType = MainActivity.changeType.stop.toString();
                    }
                }
            });

// http://developer.alexanderklimov.ru/android/views/datepicker.php
            datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                        @Override
                        public void onDateChanged(DatePicker view, int year1,
                                                  int monthOfYear2, int dayOfMonth3) {
//                            Toast.makeText(getApplicationContext(),  "onDateChanged", Toast.LENGTH_SHORT).show();
                            //TODO global vars with date like login/pass AND ADD TIME!!!

                            try {
                                Date dt = MainActivity.formatForDate.parse(year1+"-"+monthOfYear2+"-"+dayOfMonth3 + "00:00:00");
                                if (changeType.equals( MainActivity.changeType.start.toString())) MainActivity.setStartDate(dt);
                                else if (changeType.equals( MainActivity.changeType.start.toString()))   MainActivity.setStopDate(dt);
                            } catch (ParseException e) { e.printStackTrace();   }

                        }
                    });

            back_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    setResult(RESULT_OK, new Intent());
                    // Устанавливаем текущую дату для DatePicker
                    datePicker.init(year, month, day, null);
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
                    Utilites.messager(getActivity(), "Нужно что-то выбрать!");
                }
            });

          spinnerRace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
              @Override
              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  AskStartSructure AStartStr = new AskStartSructure(ARSController);
                  AStartStr.setRaceID(Long.parseLong(spinnerRace.getSelectedItem().toString())); // значение из выбранного спинером.
                  AStartStr.setActivityContext(getActivity());
                  AStartStr.setStartSpiner(spinnerStart);
                  AStartStr.execute();
              }
              @Override
              public void onNothingSelected(AdapterView<?> arg0) {
                  Utilites.messager(getActivity(), "Нужно что-то выбрать!");
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
            new GPS(getActivity(),gpsPosition);
            AskCurrentRaceStart ACRS = new AskCurrentRaceStart(raceConfig);
            ACRS.execute();

            // спинеры собираются тут ...
            AskRaceStructure ARaceS = new AskRaceStructure(ARSController);
            ARaceS.setActivityContext(getActivity());
            ARaceS.setRaceSpiner(spinnerRace);
            ARaceS.execute();
        }

        @Override
        public void start() {
            startTimeSync();
            isStarted = true;
        }

        @Override
        public void stop() {
            ServerTimer.cancel();
            isStarted = false;
        }

        private void constructStatusString() {
            loginInfo.setText(MainActivity.getLogin()+"/" + MainActivity.getLevel() + "/") ;
        }
    }

    }


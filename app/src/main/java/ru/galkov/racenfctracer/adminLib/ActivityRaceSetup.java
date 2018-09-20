package ru.galkov.racenfctracer.adminLib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

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
import static ru.galkov.racenfctracer.MainActivity.TimerTimeout;

public class ActivityRaceSetup  extends Activity {

    private Timer ServerTimer;
    public ActivityRaceSetupController ARSController;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_setup);

        context = this;

        new GPS(this,(TextView) findViewById(R.id.gpsPosition) );
        ARSController = new ActivityRaceSetupController();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimeSync();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ServerTimer.cancel();
    }

  private void startTimeSync() {
        ServerTimer = new Timer();
        ServerTimer.schedule(new TimerTask() { // Определяем задачу
          @Override
          public void run() {
              new AskServerTime((TextView) findViewById(R.id.ServerTime)).execute();
          }
      }, TimerDelay, TimerTimeout);
    }


    public class ActivityRaceSetupController {
        private TextView ServerTime;
        private Button back_button;

        private TextView raceConfig;
        private Button setRaceConfig_button;
        public Spinner spinnerRace;
        public Spinner spinnerStart;
        private long race_id = 0L;
        private long start_id = 0L;
        private TextView loginInfo;

        ActivityRaceSetupController() {
            initViewObjects();
            addListeners();
        }

        private void constructStatusString() {
            loginInfo.setText(MainActivity.getLogin()+"/" + MainActivity.getLevel() + "/") ;
        }

        private void initViewObjects() {
            back_button = findViewById(R.id.back_button);
            ServerTime = findViewById(R.id.ServerTime);
            setRaceConfig_button = findViewById(R.id.setRaceConfig_button);
            setRaceConfig_button.setEnabled(false);

            spinnerStart = findViewById(R.id.spinnerStart);
            spinnerRace = findViewById(R.id.spinnerRace);
            raceConfig = findViewById(R.id.raceConfig);
            loginInfo =             findViewById(R.id.loginInfo);
            constructStatusString();

            AskCurrentRaceStart ACRS = new AskCurrentRaceStart(raceConfig);
            ACRS.execute();

            // спинеры собираются тут ...
            AskRaceStructure ARaceS = new AskRaceStructure(ARSController);
            ARaceS.setActivityContext(context);
            ARaceS.setRaceSpiner(spinnerRace);
            ARaceS.execute();

        }


        private void addListeners() {

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
                    Utilites.messager(context, "Нужно что-то выбрать!");
                }
            });

          spinnerRace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
              @Override
              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  AskStartSructure AStartStr = new AskStartSructure(ARSController);
                  AStartStr.setRaceID(Long.parseLong(spinnerRace.getSelectedItem().toString())); // значение из выбранного спинером.
                  AStartStr.setActivityContext(context);
                  AStartStr.setStartSpiner(spinnerStart);
                  AStartStr.execute();
              }
              @Override
              public void onNothingSelected(AdapterView<?> arg0) {
                  Utilites.messager(context, "Нужно что-то выбрать!");
              }
          });
        }
    }

    }


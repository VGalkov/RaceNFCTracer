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

import ru.galkov.racenfctracer.R;
import ru.galkov.racenfctracer.common.AskCurrentRaceStart;
import ru.galkov.racenfctracer.common.AskRaceStructure;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.GPS;
import ru.galkov.racenfctracer.common.SendActiveRaceStart;

import static ru.galkov.racenfctracer.MainActivity.TimerDelay;
import static ru.galkov.racenfctracer.MainActivity.TimerTimeout;

public class ActivityRaceSetup  extends Activity {

    private Timer ServerTimer;
    private ActivityRaceSetupController ARSController;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_setup);

        context = this;

        new GPS(this,(TextView) findViewById(R.id.gpsPosition) );
        ARSController = new ActivityRaceSetupController();

        startTimeSync();

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
        private TextView activityLogger;
        private TextView raceConfig;
        private Button setRaceConfig_button;
        public Spinner spinnerRace;
        public Spinner spinnerStart;
        private long race_id = 0L;
        private long start_id = 0L;


        ActivityRaceSetupController() {
            initViewObjects();
            addListeners();
        }

        public void setRace_id(long race_id) {
            this.race_id = race_id;
        }

        public void setStart_id(long start_id) {
            this.start_id = start_id;
        }

        private void initViewObjects() {
            back_button = findViewById(R.id.back_button);
            ServerTime = findViewById(R.id.ServerTime);
            setRaceConfig_button = findViewById(R.id.setRaceConfig_button);
            activityLogger = findViewById(R.id.activityLogger);
            spinnerStart = findViewById(R.id.spinnerStart);
            spinnerRace = findViewById(R.id.spinnerRace);
            raceConfig = findViewById(R.id.raceConfig);

            AskCurrentRaceStart ACRS = new AskCurrentRaceStart(raceConfig);
            ACRS.execute();

            // спинеры собираются тут ...
            AskRaceStructure ARaceS = new AskRaceStructure(ARSController);
            ARaceS.setActivityContext(context);
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
                    SendActiveRaceStart SARS = new SendActiveRaceStart(ARSController.activityLogger);
                    SARS.setRace_id(race_id);
                    SARS.setStart_id(start_id);
                    SARS.execute();
                }
            });

          spinnerStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });

          spinnerRace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
              @Override
              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

              }
              @Override
              public void onNothingSelected(AdapterView<?> arg0) {

              }
          });
        }
    }

    }


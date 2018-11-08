package ru.galkov.racenfctracer;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.FaceControllers.ActivityFaceController;
import ru.galkov.racenfctracer.FaceControllers.HelpFaceController;
import ru.galkov.racenfctracer.FaceControllers.MainLogController;
import ru.galkov.racenfctracer.common.AskCurrentRaceStart;
import ru.galkov.racenfctracer.common.AskServerTime;

import static ru.galkov.racenfctracer.MainActivity.TimerDelay;


public class ActivityGuestManager  extends AppCompatActivity {

    private ActivityGuestManagereController AGMC;
    private HelpFaceController HFC;
    private MainLogController MLC;
    private static Context activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_manager);
        setActivity(this);
        AGMC = new ActivityGuestManagereController();
        AGMC.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.guest_activity_menu, menu);
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
                HFC.setHelpTopic(getString(R.string.GuestAccessHelp));
                HFC.start();
                return true;

            case  R.id.EventLog:
                setContentView(R.layout.activity_race_events);
                MLC = new MainLogController();
                MLC.setEkran((TextView) findViewById(R.id.User_Monitor));
                MLC.setCaller(this.toString());
                MLC.start();
                return true;

            case R.id.exit:
                setContentView(R.layout.activity_guest_manager);
                setActivity(this);
                AGMC = new ActivityGuestManagereController();
                AGMC.start();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    public static void setActivity(Context activity1) {
        activity = activity1;
    }

    public static Context  getActivity() {
        return activity;
    }

    @Override
    protected void onResume() {
        super.onResume();
        AGMC.start();

    }

        @Override
        protected void onPause () {
            super.onPause();
            AGMC.stop();
        }



        public class ActivityGuestManagereController extends ActivityFaceController {
            //private ImageButton back_button;
            private ImageButton exitButton;
            public TextView UserLogger;
            public TextView ServerTime;
            private TextView loginInfo;
            private TextView gpsPosition;
            private TextView showStart;
            private TextView showStop;
            private TextView raceStart;
            private Timer ServerTimer;
            private boolean isStarted = false;
            ActivityGuestManagereController() {
                super();
            }

            @Override
            protected void initViewObjects() {
                exitButton =  findViewById(R.id.back_button);
                UserLogger =   findViewById(R.id.UserLogger);
                ServerTime =   findViewById(R.id.ServerTime);
                loginInfo =    findViewById(R.id.loginInfo);
                showStart =  findViewById(R.id.showStart);
                showStop =  findViewById(R.id.showStop);
                raceStart =  findViewById(R.id.raceStart);
                gpsPosition =  findViewById(R.id.gpsPosition);
            }
            @Override
            public boolean isStarted() {
                return isStarted;
            }


            @Override
            protected void addListeners() {
                exitButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        setResult(RESULT_OK, new Intent());
                        finish();
                    }
                });
            }

            @Override
            protected void setDefaultFace() {
                constructStatusString();
            }

            @Override
            public void start() {
  //              startMainLogSync();
                startTimeSync();
                MainActivity.setGPSMonitor(gpsPosition);
                isStarted = true;
            }

            @Override
            public void stop() {
                ServerTimer.cancel();
                isStarted = false;
            }


            private void startTimeSync() {
                ServerTimer = new Timer(); // Создаем таймер
                ServerTimer.schedule(new TimerTask() { // Определяем задачу
                    @Override
                    public void run() {new AskServerTime(ServerTime).execute();}
                },
                        TimerDelay, MainActivity.getTimerTimeout());

            }

            private void constructStatusString() {
                loginInfo.setText(MainActivity.getLogin()+"/" + MainActivity.getLevel() + "/") ;
                AskCurrentRaceStart ACRS = new AskCurrentRaceStart(raceStart, showStart, showStop);
                ACRS.execute();
            }

        }


    }
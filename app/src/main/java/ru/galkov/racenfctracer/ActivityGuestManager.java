package ru.galkov.racenfctracer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.FaceControllers.ActivityFaceController;
import ru.galkov.racenfctracer.FaceControllers.HelpFaceController;
import ru.galkov.racenfctracer.common.AskForMainLog;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.GPS;

import static ru.galkov.racenfctracer.MainActivity.TimerDelay;


public class ActivityGuestManager  extends AppCompatActivity {

    private ActivityGuestManagereController AGMC;
    private HelpFaceController HFC;
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
                HFC.show();
                return true;


            case R.id.exit:
                setResult(RESULT_OK, new Intent());
                finish();
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
            private Button back_button;
            public TextView UserLogger;
            public TextView ServerTime;
            private TextView loginInfo;
            private TextView gpsPosition;
            private GPS GPS_System;
            private Timer ServerTimer;
            private Timer MainLogTimer;

            ActivityGuestManagereController() {
                super();
            }


            @Override
            protected void initViewObjects() {
                back_button =  findViewById(R.id.back_button);
                UserLogger =   findViewById(R.id.UserLogger);
                ServerTime =   findViewById(R.id.ServerTime);
                loginInfo =    findViewById(R.id.loginInfo);
                gpsPosition =  findViewById(R.id.gpsPosition);
            }


            @Override
            protected void addListeners() {
                back_button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        setResult(RESULT_OK, new Intent());
                        finish();
                    }
                });
            }

            @Override
            protected void setDefaultFace() {
                GPS_System = new GPS(MainActivity.getActivity(), gpsPosition);
                constructStatusString();
            }

            @Override
            protected void start() {
                startMainLogSync();
                startTimeSync();
            }

            @Override
            protected void stop() {
                ServerTimer.cancel();
            }


            private void startTimeSync() {
                ServerTimer = new Timer(); // Создаем таймер
                ServerTimer.schedule(new TimerTask() { // Определяем задачу
                    @Override
                    public void run() {new AskServerTime(ServerTime).execute();}
                },
                        TimerDelay, MainActivity.getTimerTimeout());

            }

            private void startMainLogSync() {
                // интервал - 60000 миллисекунд, 0 миллисекунд до первого запуска.

                MainLogTimer = new Timer(); // Создаем таймер
                MainLogTimer.schedule(new TimerTask() { // Определяем задачу
                    @Override
                    public void run() {
                        MainActivity.writeMethod wMethod = MainActivity.writeMethod.Set;
                        AskForMainLog AForML = new AskForMainLog(UserLogger);
                        AForML.setMethod(wMethod);
//                        AForML.setMethod(MainActivity.writeMethod.Append);
                        AForML.setCaller(this.toString());
                        AForML.execute();
                    }
                }, TimerDelay, MainActivity.getMainLogTimeout());

            }



            private void constructStatusString() {
                loginInfo.setText(MainActivity.getLogin()+"/" + MainActivity.getLevel() + "/") ;
            }

        }


    }
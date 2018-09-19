package ru.galkov.racenfctracer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.common.AskForMainLog;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.GPS;

import static ru.galkov.racenfctracer.MainActivity.TimerDelay;
import static ru.galkov.racenfctracer.MainActivity.TimerTimeout;


public class ActivityGuestManager  extends Activity {

    private ActivityGuestManagereController AGMC;
    private GPS GPS_System;
    private Timer ServerTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_manager);

        AGMC = new ActivityGuestManagereController();

        GPS_System = new GPS(this,(TextView) findViewById(R.id.gpsPosition) );
//        startTimeSync(); // или в onResume?

    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimeSync();
    }

        @Override
        protected void onPause () {
            super.onPause();
            ServerTimer.cancel();
        }


    private void startTimeSync() {
        // интервал - 60000 миллисекунд, 0 миллисекунд до первого запуска.

        ServerTimer = new Timer(); // Создаем таймер
        ServerTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                new AskServerTime(AGMC.ServerTime).execute();
                //  опрашивать сервер о новых данных и времени
                MainActivity.writeMethod wMethod = MainActivity.writeMethod.Set;
                AskForMainLog AForML = new AskForMainLog(AGMC.UserLogger); //опросчик на лог main_log сервера.
                AForML.setMethod(wMethod);
                AForML.execute();
            }
        }, TimerDelay, TimerTimeout);

    }


        public class ActivityGuestManagereController {
            private Button back_button;
            public TextView UserLogger;
            public TextView ServerTime;
            private TextView loginInfo;

            ActivityGuestManagereController() {
                setDefaultView();
            }

            public void setDefaultView() {
                initViewObjects();
                addListeners();
            }

            private void initViewObjects() {
                back_button = findViewById(R.id.back_button);
                UserLogger = findViewById(R.id.UserLogger);
                ServerTime = findViewById(R.id.ServerTime);
                loginInfo =             findViewById(R.id.loginInfo);
                constructStatusString();
            }

            private void constructStatusString() {
                loginInfo.setText(MainActivity.getLogin()+"/" + MainActivity.getLevel() + "/") ;
            }

            private void addListeners() {
                back_button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        setResult(RESULT_OK, new Intent());
                        finish();
                    }
                });
            }


        }


    }
package ru.galkov.racenfctracer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.common.AskForMainLog;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.GPS;
import ru.galkov.racenfctracer.common.Utilites;

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
        AGMC.setDefaultView();


        GPS_System = new GPS(this,(TextView) findViewById(R.id.gpsPosition) );

        startTimeSync(); // или в onResume?
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimeSync();


    }

        @Override
        protected void onPause () {
            super.onPause();
        }


    private void startTimeSync() {
        // интервал - 60000 миллисекунд, 0 миллисекунд до первого запуска.

        ServerTimer = new Timer(); // Создаем таймер
        final Handler uiHandler = new Handler();

        ServerTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                new AskServerTime(AGMC.ServerTime);
                //  опрашивать сервер о новых данных и времени
                new AskForMainLog(AGMC.UserLogger).execute();; //опросчик на лог main_log сервера.
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {                  String srt  = "";                 }
                });
            }
        }, 0L, TimerTimeout);

    }

        public class ActivityGuestManagereController {
            private Button back_button;
            public TextView UserLogger;
            public TextView ServerTime;

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
            }

            private void addListeners() {
                back_button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        setResult(RESULT_OK, new Intent());
                        finish();
                    }
                });
            }

// !
            public void messager(String str1) {
                Utilites.messager(ActivityGuestManager.this, str1);
            }
        }


    }
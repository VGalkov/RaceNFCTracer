package ru.galkov.racenfctracer;

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

import ru.galkov.racenfctracer.FaceControllers.HelpFaceController;
import ru.galkov.racenfctracer.FaceControllers.ActivityFaceController;
import ru.galkov.racenfctracer.common.AskForMainLog;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.GPS;

import static ru.galkov.racenfctracer.MainActivity.TimerDelay;
import static ru.galkov.racenfctracer.MainActivity.TimerTimeout;


public class ActivityGuestManager  extends AppCompatActivity {

    private ActivityGuestManagereController AGMC;
    private GPS GPS_System;
    private Timer ServerTimer;
    private HelpFaceController HFC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_manager);

        AGMC = new ActivityGuestManagereController();

        GPS_System = new GPS(this,(TextView) findViewById(R.id.gpsPosition) );
        startTimeSync();

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
                /// TODO переписать на выход в геста после переделки фейсконтроллера.
                setResult(RESULT_OK, new Intent());
                finish();
                return true;


        }
        return super.onOptionsItemSelected(item);
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
                AForML.setCaller(this.toString());
                AForML.execute();
            }
        }, TimerDelay, TimerTimeout);

    }


        public class ActivityGuestManagereController extends ActivityFaceController {
            private Button back_button;
            public TextView UserLogger;
            public TextView ServerTime;
            private TextView loginInfo;

            ActivityGuestManagereController() {
                super();
            }


            @Override
            protected void initViewObjects() {
                back_button = findViewById(R.id.back_button);
                UserLogger = findViewById(R.id.UserLogger);
                ServerTime = findViewById(R.id.ServerTime);
                loginInfo =             findViewById(R.id.loginInfo);
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
                constructStatusString();
            }

            private void constructStatusString() {
                loginInfo.setText(MainActivity.getLogin()+"/" + MainActivity.getLevel() + "/") ;
            }

        }


    }
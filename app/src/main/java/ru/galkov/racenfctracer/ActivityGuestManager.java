package ru.galkov.racenfctracer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yandex.mapkit.MapKitFactory;

import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.FaceControllers.ActivityFaceController;
import ru.galkov.racenfctracer.FaceControllers.HelpFaceController;
import ru.galkov.racenfctracer.FaceControllers.MainLogController;
import ru.galkov.racenfctracer.FaceControllers.MapViewController;
import ru.galkov.racenfctracer.common.AskCurrentRaceStart;
import ru.galkov.racenfctracer.common.AskMapPoints;
import ru.galkov.racenfctracer.common.AskResultsImgTable;
import ru.galkov.racenfctracer.common.AskServerTime;

import static ru.galkov.racenfctracer.MainActivity.MV;
import static ru.galkov.racenfctracer.MainActivity.TimerDelay;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;
import static ru.galkov.racenfctracer.MainActivity.getTimerTimeout;
import static ru.galkov.racenfctracer.MainActivity.mapview;
import static ru.galkov.racenfctracer.MainActivity.setGPSMonitor;


public class ActivityGuestManager  extends AppCompatActivity {

    private ActivityGuestManagereController AGMC;
    private Context activity;

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
        int id = item.getItemId();
        switch(id){

            case R.id.help:
                setContentView(R.layout.activity_help_system);
                HelpFaceController HFC = new HelpFaceController();
                HFC.setEkran((TextView) findViewById(R.id.ekran));
                HFC.setHelpTopic(getString(R.string.GuestAccessHelp));
                HFC.start();
                return true;

            case  R.id.EventLog:
                setContentView(R.layout.activity_race_events);
                MainLogController MLC = new MainLogController();
                MLC.setEkran((TextView) findViewById(R.id.User_Monitor));
                MLC.setCaller(this.toString());
                MLC.start();
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

            case R.id.graph:
                setContentView(R.layout.activity_results_img);
                setActivity(this);
                ImageView iV = findViewById(R.id.imageView);
                AskResultsImgTable ARIT = new AskResultsImgTable();
                ARIT.setImage(iV);
                ARIT.execute();

//TODO создать контроллер активити
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


    public void setActivity(Context activity1) {
        activity = activity1;
    }

    public Context  getActivity() {
        return activity;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mapview.onStart();
            MapKitFactory.getInstance().onStart();
        }
        catch (NullPointerException e) { e.printStackTrace();}
        AGMC.start();

    }

        @Override
        protected void onPause () {
            super.onPause();
            try {
                mapview.onStop();
                MapKitFactory.getInstance().onStop();
            }
            catch (NullPointerException e) { e.printStackTrace();}
            AGMC.stop();
        }
        public class ActivityGuestManagereController extends ActivityFaceController {

            private ImageButton exitButton;
            public TextView UserLogger, ServerTime, loginInfo, gpsPosition, showStart, showStop, raceStart;
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
                startTimeSync();
                setGPSMonitor(gpsPosition);
                isStarted = true;
            }

            @Override
            public void stop() {
                ServerTimer.cancel();
                isStarted = false;
            }


            private void startTimeSync() {
                ServerTimer = new Timer();
                ServerTimer.schedule(new TimerTask() { // Определяем задачу
                    @Override
                    public void run() {new AskServerTime(ServerTime).execute();}
                },
                        TimerDelay, getTimerTimeout());
            }

            private void constructStatusString() {
                String str = getLogin() + ":" + getLevel();
                loginInfo.setText(str);
                AskCurrentRaceStart ACRS = new AskCurrentRaceStart(raceStart, showStart, showStop);
                ACRS.execute();
            }
        }
    }
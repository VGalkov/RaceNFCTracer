package ru.galkov.racenfctracer;

import android.content.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;
import com.yandex.mapkit.MapKitFactory;
import java.util.*;
import ru.galkov.racenfctracer.FaceControllers.*;
import ru.galkov.racenfctracer.common.*;
import static ru.galkov.racenfctracer.MainActivity.MV;
import static ru.galkov.racenfctracer.MainActivity.TimerDelay;
import static ru.galkov.racenfctracer.MainActivity.mapview;


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


    public static void setActivity(Context activity1) {
        activity = activity1;
    }

    public static Context  getActivity() {
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
/*
    @Override
    protected void onStop() {
        super.onStop();
        try {
            mapview.onStop();
            MapKitFactory.getInstance().onStop();
        }
        catch (NullPointerException e) { e.printStackTrace();}
        AGMC.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mapview.onStart();
            MapKitFactory.getInstance().onStart();
        }
        catch (NullPointerException e) { e.printStackTrace();}
        AGMC.stop();
    }
*/
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
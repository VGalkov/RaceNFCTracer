package ru.galkov.racenfctracer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
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
import ru.galkov.racenfctracer.adminLib.ActivityLoginersRightsRedactor;
import ru.galkov.racenfctracer.adminLib.ActivityNFCMarksRedactor;
import ru.galkov.racenfctracer.adminLib.ActivityRaceSetup;
import ru.galkov.racenfctracer.adminLib.ActivityResultsTable;
import ru.galkov.racenfctracer.common.AskMapPoints;
import ru.galkov.racenfctracer.common.AskResultsImgTable;
import ru.galkov.racenfctracer.common.AskServerTime;

import static ru.galkov.racenfctracer.MainActivity.MV;
import static ru.galkov.racenfctracer.MainActivity.TimerDelay;
import static ru.galkov.racenfctracer.MainActivity.mapview;

public class ActivityAdminManager  extends AppCompatActivity {
    private ActivityAdminManagerController AAMC;
    private HelpFaceController HFC;
    private MainLogController MLC;
    private static Context activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manager);
        setActivity(this);
        AAMC = new ActivityAdminManagerController();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){

            case R.id.help:
                setContentView(R.layout.activity_help_system);
                HFC = new HelpFaceController();
                HFC.setEkran((TextView) findViewById(R.id.ekran));
                HFC.setHelpTopic(getString(R.string.AdminAccessHelp));
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
                setContentView(R.layout.activity_admin_manager);
                setActivity(this);
                AAMC = new ActivityAdminManagerController();
                AAMC.start();
                return true;

        }
        return super.onOptionsItemSelected(item);
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
        AAMC.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mapview.onStart();
            MapKitFactory.getInstance().onStart();
        }
        catch (NullPointerException e) { e.printStackTrace();}
        AAMC = new ActivityAdminManagerController();
        AAMC.start();
    }

 */

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mapview.onStart();
            MapKitFactory.getInstance().onStart();
        }
        catch (NullPointerException e) { e.printStackTrace();}
        AAMC = new ActivityAdminManagerController();
        AAMC.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mapview.onStop();
            MapKitFactory.getInstance().onStop();
        }
        catch (NullPointerException e) { e.printStackTrace();}
        AAMC.stop();

    }

    public static void setActivity(Context activity1) {
        activity = activity1;
    }

    public static Context  getActivity() {
        return activity;
    }

// ==============================================================

    public class  WebBrowserController extends ActivityFaceController {
// Вся система обмена метками для карты на основе javascript и yandex map

        private Context context;


        public WebBrowserController(Context context1) {
            this.context = context1;
            initViewObjects();
        }

        @Override
        protected void initViewObjects() {

        }

        @Override
        protected void addListeners() {

        }

        @Override
        protected void setDefaultFace() {

        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public boolean isStarted() {
            return false;
        }
    }

public class ActivityAdminManagerController extends ActivityFaceController {
        private ImageButton back_button;
        private Button results_table_button;
        private Button register_editor_button;
        private Button nfc_marks_editor_button;
        private Button raceSetup_button;
        public TextView UserLogger;
        public TextView ServerTime;
        private TextView loginInfo;
        private Timer ServerTimer;
        private TextView gpsPosition;
        private boolean isStarted = false;

        ActivityAdminManagerController() {
            super();
        }

    @Override
    protected void initViewObjects() {
        back_button =                       findViewById(R.id.back_button);
        results_table_button =              findViewById(R.id.results_table_button);
        register_editor_button =            findViewById(R.id.register_editor_button);
        nfc_marks_editor_button =           findViewById(R.id.nfc_marks_editor_button);
        raceSetup_button =                  findViewById(R.id.raceSetup_button);
        UserLogger =                        findViewById(R.id.UserLogger);
        ServerTime =                        findViewById(R.id.ServerTime);
        loginInfo =                         findViewById(R.id.loginInfo);
        gpsPosition =                       findViewById(R.id.gpsPosition);
    }
    @Override
    public boolean isStarted() {
        return isStarted;
    }

    @Override
    protected void addListeners() {

        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_OK, new Intent());
                finish();
            }
        });
// TODO перепсать вызов в то же активити.
        raceSetup_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivityForResult(new Intent(view.getContext(), ActivityRaceSetup.class), 0);
//                setContentView(R.layout.ActivityRaceSetup);
            }
        });

        results_table_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivityForResult(new Intent(view.getContext(), ActivityResultsTable.class), 0);
            }
        });

        register_editor_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivityForResult(new Intent(view.getContext(), ActivityLoginersRightsRedactor .class), 0);
            }
        });

        nfc_marks_editor_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivityForResult(new Intent(view.getContext(), ActivityNFCMarksRedactor.class), 0);
            }
        });
    }


    // ============================
    @Override
    protected void setDefaultFace() {
        constructStatusString();
    }

    @Override
    public void start() {
        startTimeSync();
        MainActivity.setGPSMonitor(gpsPosition);
        isStarted = true;
    }

    @Override
    public void stop() {
        ServerTimer.cancel();
//        MainLogTimer.cancel();
        isStarted = false;
    }

    private void startTimeSync() {
        ServerTimer = new Timer(); // Создаем таймер
        ServerTimer.schedule(new TimerTask() {
            @Override
            public void run() {new AskServerTime(AAMC.ServerTime).execute();       }
            },
                TimerDelay, MainActivity.getTimerTimeout());
    }

   /* private void startMainLogTimeSync() {
        MainLogTimer = new Timer(); // Создаем таймер
        MainLogTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() { new AskForMainLog(AAMC.UserLogger, this.toString()).execute();}
            },
                TimerDelay, MainActivity.getMainLogTimeout());

    }*/


    private void constructStatusString() {
        loginInfo.setText(MainActivity.getLogin()+"/" + MainActivity.getLevel() + "/") ;
    }
}
}

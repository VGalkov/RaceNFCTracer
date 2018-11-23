package ru.galkov.racenfctracer.adminLib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.yandex.mapkit.MapKitFactory;

import ru.galkov.racenfctracer.FaceControllers.ActivityFaceController;
import ru.galkov.racenfctracer.FaceControllers.HelpFaceController;
import ru.galkov.racenfctracer.FaceControllers.MainLogController;
import ru.galkov.racenfctracer.FaceControllers.MapViewController;
import ru.galkov.racenfctracer.R;
import ru.galkov.racenfctracer.common.AskMapPoints;
import ru.galkov.racenfctracer.common.AskRaceConfig;
import ru.galkov.racenfctracer.common.AskResultsImgTable;

import static ru.galkov.racenfctracer.MainActivity.MV;
import static ru.galkov.racenfctracer.MainActivity.mapview;

public class ActivityRaceConfig extends AppCompatActivity {

    public Context activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.race_config_activity);
        setActivity(this);
        new ShowRaceConfigFaceController();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.race_config_show_menu, menu);
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
                HFC.setHelpTopic(getString(R.string.AdminAccessHelp));
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
                setResult(RESULT_OK, new Intent());
                finish();
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

    public class ShowRaceConfigFaceController extends ActivityFaceController {

        private TableLayout tableLayout;
        private TextView start_config;

        @Override
        protected void initViewObjects() {
            tableLayout = findViewById(R.id.configTable);
            start_config = findViewById(R.id.start_config);
            AskRaceConfig ARC  = new AskRaceConfig(tableLayout, getActivity(), start_config);
            ARC.execute();
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
        public void restart() {
            stop();
            start();
        }

        @Override
        public boolean isStarted() {
            return false;
        }
    }
}
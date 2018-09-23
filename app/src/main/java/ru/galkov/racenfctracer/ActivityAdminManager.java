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
import ru.galkov.racenfctracer.adminLib.ActivityLoginersRightsRedactor;
import ru.galkov.racenfctracer.adminLib.ActivityNFCMarksRedactor;
import ru.galkov.racenfctracer.adminLib.ActivityRaceSetup;
import ru.galkov.racenfctracer.adminLib.ActivityResultsTable;
import ru.galkov.racenfctracer.common.ActivityFaceController;
import ru.galkov.racenfctracer.common.AskForMainLog;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.GPS;

import static ru.galkov.racenfctracer.MainActivity.TimerDelay;
import static ru.galkov.racenfctracer.MainActivity.TimerTimeout;

public class ActivityAdminManager  extends AppCompatActivity {
    private ActivityAdminManagerController AAMC;
    private HelpFaceController HFC;
    private GPS GPS_System;
    private Timer ServerTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manager);

        AAMC = new ActivityAdminManagerController();
        startTimeSync();
        GPS_System = new GPS(this,(TextView) findViewById(R.id.gpsPosition) );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_activity_menu, menu);
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
                HFC.setHelpTopic(getString(R.string.AdminAccessHelp));
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
    protected void onPause() {
        super.onPause();
        ServerTimer.cancel();
    }

    private void startTimeSync() {
        ServerTimer = new Timer(); // Создаем таймер
        ServerTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                new AskServerTime(AAMC.ServerTime).execute();
                new AskForMainLog(AAMC.UserLogger, this.toString()).execute();
            }
        }, TimerDelay, TimerTimeout);

    }



// ==============================================================
public class ActivityAdminManagerController extends ActivityFaceController {
        private Button back_button;
        private Button results_table_button;
        private Button register_editor_button;
        private Button nfc_marks_editor_button;
        private Button raceSetup_button;
        public TextView UserLogger;
        public TextView ServerTime;
        private TextView loginInfo;


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

        raceSetup_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivityForResult(new Intent(view.getContext(), ActivityRaceSetup.class), 0);
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

    private void constructStatusString() {
        loginInfo.setText(MainActivity.getLogin()+"/" + MainActivity.getLevel() + "/") ;
    }
}
}

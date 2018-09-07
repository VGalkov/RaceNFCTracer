package ru.galkov.racenfctracer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.adminLib.ActivityLoginersRightsRedactor;
import ru.galkov.racenfctracer.adminLib.ActivityNFCMarksRedactor;
import ru.galkov.racenfctracer.adminLib.ActivityResultsTable;
import ru.galkov.racenfctracer.common.AskForMainLog;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.GPS;

import static ru.galkov.racenfctracer.MainActivity.TimerDelay;
import static ru.galkov.racenfctracer.MainActivity.TimerTimeout;

public class ActivityAdminManager  extends Activity {
    private ActivityAdminManagerController AAMC;
    private GPS GPS_System;
    private Timer ServerTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manager);

        ActivityAdminManagerController AAMC = new ActivityAdminManagerController();
        AAMC.setDefaultView();

        GPS_System = new GPS(this,(TextView) findViewById(R.id.gpsPosition) );

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


// ==============================================================
public class ActivityAdminManagerController{
        private Button back_button;
        private Button results_table_button;
        private Button register_editor_button;
        private Button nfc_marks_editor_button;
        public TextView UserLogger;
        public TextView ServerTime;


        ActivityAdminManagerController() {
            setDefaultView();
        }

    public void setDefaultView() {
        initViewObjects();
        addListeners();
        setDefaultFace();
        startTimeSync();
    }

    private void initViewObjects() {
        back_button =                       findViewById(R.id.back_button);
        results_table_button =              findViewById(R.id.results_table_button);
        register_editor_button =            findViewById(R.id.register_editor_button);
        nfc_marks_editor_button =           findViewById(R.id.nfc_marks_editor_button);
        UserLogger =                        findViewById(R.id.UserLogger);
        ServerTime =                        findViewById(R.id.ServerTime);


    }

    private void addListeners() {

        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_OK, new Intent());
                finish();
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



    private void startTimeSync() {
        // интервал - 60000 миллисекунд, 0 миллисекунд до первого запуска.

        ServerTimer = new Timer(); // Создаем таймер
        ServerTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                new AskServerTime(AAMC.ServerTime);
                // прашивать сервер о новых данных
                new AskForMainLog(AAMC.UserLogger).execute(); //опросчик на лог main_log сервера.
            }
        }, TimerDelay, TimerTimeout);

    }

    // ============================
    private void setDefaultFace() {

    }
}
}

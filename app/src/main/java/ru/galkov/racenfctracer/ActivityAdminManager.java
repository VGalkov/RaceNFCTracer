package ru.galkov.racenfctracer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ru.galkov.racenfctracer.adminLib.ActivityLoginersRightsRedactor;
import ru.galkov.racenfctracer.adminLib.ActivityNFCMarksRedactor;
import ru.galkov.racenfctracer.adminLib.ActivityResultsTable;
import ru.galkov.racenfctracer.common.AskMainLogAdmin;
import ru.galkov.racenfctracer.common.GPS;

public class ActivityAdminManager  extends Activity {
    private ActivityAdminManagerController AAMC;
    private GPS GPS_System;
    private AskMainLogAdmin AMLA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manager);

        ActivityAdminManagerController AAMC = new ActivityAdminManagerController();
        AAMC.setDefaultView();

        new AskMainLogAdmin(AAMC).execute();

        // конфигуратор сюда. или WD долько для main_log
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
        public TextView userLogger;

        ActivityAdminManagerController() {
            setDefaultView();
        }

    public void setDefaultView() {
        initViewObjects();
        addListeners();
        setDefaultFace();
    }

    private void initViewObjects() {
        back_button =                       findViewById(R.id.back_button);
        results_table_button =              findViewById(R.id.results_table_button);
        register_editor_button =            findViewById(R.id.register_editor_button);
        nfc_marks_editor_button =           findViewById(R.id.nfc_marks_editor_button);
        userLogger =                findViewById(R.id.userLogger);
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

    public void messager(String str1) {
        Toast.makeText(ActivityAdminManager.this, str1, Toast.LENGTH_LONG).show();
    }
    // ============================
    private void setDefaultFace() {

    }
}
}

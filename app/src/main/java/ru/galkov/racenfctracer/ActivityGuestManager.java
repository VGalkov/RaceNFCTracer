package ru.galkov.racenfctracer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ru.galkov.racenfctracer.common.AskMainLogGuest;
import ru.galkov.racenfctracer.common.GPS;
import ru.galkov.racenfctracer.common.Utilites;


public class ActivityGuestManager  extends Activity {

    private ActivityGuestManagereController AGMC;
    private GPS GPS_System;
    private AskMainLogGuest AMLG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_manager);

        AGMC = new ActivityGuestManagereController();
        AGMC.setDefaultView();

        // в отдельный поток опрашивать сервер о новых данных
        new AskMainLogGuest(AGMC).execute();; //опросчик на лог main_log сервера.

        GPS_System = new GPS(this,(TextView) findViewById(R.id.gpsPosition) );
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

        @Override
        protected void onPause () {
            super.onPause();
        }


        public class ActivityGuestManagereController {
            private Button back_button;
            public TextView UserLogger;

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
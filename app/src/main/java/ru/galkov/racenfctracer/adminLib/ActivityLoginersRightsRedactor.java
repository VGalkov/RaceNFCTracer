package ru.galkov.racenfctracer.adminLib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.R;
import ru.galkov.racenfctracer.FaceControllers.ActivityFaceController;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.AskUserTable;
import ru.galkov.racenfctracer.common.SendUserLevel;

import static ru.galkov.racenfctracer.MainActivity.TimerDelay;
import static ru.galkov.racenfctracer.MainActivity.TimerTimeout;

//  https://startandroid.ru/ru/uroki/vse-uroki-spiskom/115-urok-56-spinner-vypadajuschij-spisok.html
// https://startandroid.ru/ru/uroki/vse-uroki-spiskom/115-urok-56-spinner-vypadajuschij-spisok.html
// https://developer.android.com/guide/topics/ui/controls/spinner
// https://metanit.com/java/android/5.4.php

public class ActivityLoginersRightsRedactor  extends Activity {

    private ActivityLoginersRightsRedactorController ALRRC;
    private AskUserTable AUT;
    private Context activityContext;
    private Timer ServerTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_loginers_rights_redactor);
        activityContext =  this;

        ALRRC = new ActivityLoginersRightsRedactorController();

        startTimeSync();

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
                new AskServerTime(ALRRC.ServerTime).execute();
            }
        }, TimerDelay, TimerTimeout);

    }


// ==========================================================

    public class ActivityLoginersRightsRedactorController extends ActivityFaceController {
        private Button back_button;
        private Button setButton;
        private ArrayAdapter<String> adapterLevels;
        private TextView LoginLevel;
        private TextView LoginToChng;


        public TextView ServerTime;
        public TextView LoginLevelLabel;
        public Spinner spinnerUsers;
        public Spinner spinnerLevel;
        public TextView userLogger;
        private TextView loginInfo;



        ActivityLoginersRightsRedactorController() {
            super();
        }


        @Override
        protected void initViewObjects() {
            back_button =    findViewById(R.id.back_button);
            setButton =    findViewById(R.id.setButton);
            ServerTime =     findViewById(R.id.ServerTime);
            LoginLevel =     findViewById(R.id.LoginLevel);
            LoginLevelLabel = findViewById(R.id.LoginLevelLabel);
            userLogger =    findViewById(R.id.userLogger);
            loginInfo =             findViewById(R.id.loginInfo);
            LoginLevel = findViewById(R.id.LoginLevel);
            LoginToChng = findViewById(R.id.LoginToChng);
            spinnerLevel =         findViewById(R.id.spinnerLevel);
            spinnerUsers =         findViewById(R.id.spinnerUsers);

        }

        private void constructStatusString() {
            loginInfo.setText(MainActivity.getLogin()+"/" + MainActivity.getLevel() + "/") ;
        }

        @Override
        protected void addListeners() {
            back_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
            });

            setButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    // прописываем уровень абоента. нужно передвать весё состояние view
                    SendUserLevel SUL = new SendUserLevel(userLogger);
                    SUL.setLogin(LoginToChng.getText().toString());
                    SUL.setLevel(LoginLevel.getText().toString());
                    SUL.execute();

                }
            });

// не работает
            spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    Object item = parent.getItemAtPosition(position);
//                    LoginLevel.setText(item.toString());
                    LoginLevel.setText(spinnerLevel.getSelectedItem().toString());
                    userLogger.setText("Будет записано:" + LoginLevel.getText() + "для" + LoginToChng.getText());

                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    MainActivity.registrationLevel regLevel =  MainActivity.registrationLevel.Guest;
                    LoginToChng.setText(regLevel.toString());
                }
            });


            spinnerUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String login = spinnerUsers.getSelectedItem().toString();
                    // отсекаем ненужное. но если в логине будет скобка реально - это косяк. но логин = номеру телефона.12  цифр и +
                    LoginToChng.setText(login.substring(0, login.indexOf('(')));
                    userLogger.setText("Будет записано:" + LoginLevel.getText() + "для" + LoginToChng.getText());
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }


        // ============================
        @Override
        protected void setDefaultFace() {
            constructStatusString();

            String[] levels =   {"Guest", "User", "Admin", "Delete"};
            adapterLevels = new ArrayAdapter<String>(activityContext,  android.R.layout.simple_spinner_item, levels);
            spinnerLevel.setAdapter(adapterLevels);

            // запрос содержимого списка
            AskUserTable AUT = new AskUserTable(spinnerUsers);
            AUT.setActivityContext(activityContext);
            AUT.execute();
        }
    }
}

package ru.galkov.racenfctracer.adminLib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.FaceControllers.ActivityFaceController;
import ru.galkov.racenfctracer.FaceControllers.HelpFaceController;
import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.R;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.AskUserTable;
import ru.galkov.racenfctracer.common.SendUserLevel;

import static ru.galkov.racenfctracer.MainActivity.TimerDelay;


public class ActivityLoginersRightsRedactor  extends AppCompatActivity {

    private ActivityLoginersRightsRedactorController ALRRC;
    private Context activity;
    private HelpFaceController HFC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_loginers_rights_redactor);
        setActivity(this);
        ALRRC = new ActivityLoginersRightsRedactorController();
        ALRRC.start();
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
        ALRRC = new ActivityLoginersRightsRedactorController();
        ALRRC.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ALRRC.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_setup_activity_menu, menu);
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
                HFC.setHelpTopic(getString(R.string.loginSetupHelp));
                HFC.start();
                return true;


            case R.id.exit:
                setResult(RESULT_OK, new Intent());
                finish();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }


// ==========================================================

    public class ActivityLoginersRightsRedactorController extends ActivityFaceController {
        private Button back_button;
        private Button setButton;
        private ArrayAdapter<String> adapterLevels;
        private TextView LoginLevel;
        private TextView LoginToChng;
        private Timer ServerTimer;

        public TextView ServerTime;
        public TextView LoginLevelLabel;
        public Spinner spinnerUsers;
        public Spinner spinnerLevel;
        public TextView userLogger;
        private TextView loginInfo;



        ActivityLoginersRightsRedactorController() {
            super();
        }

        private void startTimeSync() {
            ServerTimer = new Timer();
            ServerTimer.schedule(new TimerTask() { // Определяем задачу
                @Override
                public void run() {
                    new AskServerTime(ALRRC.ServerTime).execute();
                }
            }, TimerDelay, MainActivity.getTimerTimeout());

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
            adapterLevels = new ArrayAdapter(getActivity(),  android.R.layout.simple_spinner_item, levels);
            spinnerLevel.setAdapter(adapterLevels);

            // запрос содержимого списка
            AskUserTable AUT = new AskUserTable(spinnerUsers);
            AUT.setActivityContext(getActivity());
            AUT.execute();
        }

        @Override
        protected void start() {
            startTimeSync();
        }

        @Override
        protected void stop() {
            ServerTimer.cancel();
        }
    }
}

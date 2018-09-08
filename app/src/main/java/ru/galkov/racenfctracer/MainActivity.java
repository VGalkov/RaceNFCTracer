package ru.galkov.racenfctracer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.common.AskForLogin;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.Utilites;


public class MainActivity extends Activity {

    public enum registrationLevel {Guest,User,Admin}
    private MainActivityFaceController MAFC;
    public static final String KEY = "xzcv4ewattaswrf";
    public static final SimpleDateFormat formatForDate = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
    public static final String SERVER_URL = "https://mb-samara.ru";
    public static final int HTTP_TIMEOUT = 15000; // milliseconds
    public static final int TimerTimeout = 6000;//0;
    public static final int TimerDelay = 0;
    private Timer ServerTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MAFC = new MainActivityFaceController();
        startTimeSync();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // вынести в отдельный класс для всех со списком запуска и ссылками на экраны отображения ддля каждого Async.
    private void startTimeSync() {
        ServerTimer = new Timer(); // Создаем таймер
        ServerTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                new AskServerTime(MAFC.ServerTime).execute();
            }
        }, TimerDelay, TimerTimeout);

    }


// =======================================================

    public class MainActivityFaceController {

        private String              ERROR_MSG;
        private Button              exitButton;
        private Button              registerButton;
        private Button              enterButton;
        private RadioGroup          LoginType_radio_group;
        private TextView            password;
        public TextView            RegAsLabel;
        private TextView            phone;
        public  registrationLevel   REGLEVEL = registrationLevel.Guest;
        private RadioButton         AdminRadioButton;
        private RadioButton         UserRadioButton;
        private RadioButton         GuestRadioButton;
        public TextView             ServerTime;

        //      Constructor; ============================================
        MainActivityFaceController() {
            setDefaultView();
        }


        private void setDefaultView() {
            initViewObjects();
            addListeners();
            setDefaultFace();

        }



        // ======================================================================================
        private void setDefaultFace() {
            setButton(exitButton, true);
            setButton(registerButton, true);
            setButton(enterButton, false);
            setRadioSystem(LoginType_radio_group, true);
            setTextFields(password, true);
            setTextFields(phone, true);
            ERROR_MSG = "Данных вообще нет";

            dropRegistration();
        }

        public void setRegistredFace() {
            setButton(exitButton, true);
            setButton(registerButton, false);
            setButton(enterButton, true);
            setRadioSystem(LoginType_radio_group, false);
            setTextFields(password, false);
            setTextFields(phone, false);
        }

        // =======================================================================================

        private void initViewObjects() {
            exitButton =            findViewById(R.id.exit_button);
            enterButton =           findViewById(R.id.enter_button);
            registerButton =        findViewById(R.id.register_button);
            LoginType_radio_group = findViewById(R.id.LoginType_radio_group);
            AdminRadioButton =      findViewById(R.id.AdminRadioButton);
            UserRadioButton =       findViewById(R.id.UserRadioButton);
            GuestRadioButton =      findViewById(R.id.GuestRadioButton);
            RegAsLabel =            findViewById(R.id.RegAsLabel);
            phone =                 findViewById(R.id.phone);
            password =              findViewById(R.id.password);
            ServerTime =            findViewById(R.id.ServerTime);
        }



        private String getMyPhoneNumber() {
            TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//              if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//              getString(R.string.noREAD_PHONE_STATE)
//                return "";
            return mTelephonyMgr.getLine1Number();
        }

        private void setTextFields(TextView tv1, boolean trigger2) {
            tv1.setEnabled(trigger2);
            tv1.setClickable(trigger2);
        }

        private void setRadioSystem(RadioGroup rg1, boolean trigger2) {
            rg1.setEnabled(trigger2);
            rg1.setClickable(trigger2);

            GuestRadioButton.setEnabled(trigger2);
            UserRadioButton.setEnabled(trigger2);
            AdminRadioButton.setEnabled(trigger2);

            GuestRadioButton.setClickable(trigger2);
            UserRadioButton.setClickable(trigger2);
            AdminRadioButton.setClickable(trigger2);

        }

        private void setButton(Button btn1, boolean trigger2) {
            btn1.setEnabled(trigger2);
            btn1.setClickable(trigger2);
        }

        private void dropRegistration() {
            phone.setText(getMyPhoneNumber());
            password.setText("");
            REGLEVEL = registrationLevel.Guest;
            RegAsLabel.setText(REGLEVEL.toString()); // toString ли?
        }


        // ===================================================================================

        private boolean CheckLoginDataIntegrity() {
            // проверка данных для логина в систему.
            boolean trigger = true;
            String str = "";

            if (phone.getText().length() !=12)   { trigger = false; str = str + "Телефон не верен! (" + phone.getText().length() + ")"; }
            if (password.getText().length() < 5)  { trigger = false;  str = str + "Пароль - короткий! (" + password.getText() + ") "; }

            // String regType = ((RadioButton) findViewById(LoginType_radio_group.getCheckedRadioButtonId())).getText().toString();
            // if ((regType.length()<4) && (regType.length()>6))     { trigger = false; sb.append("Тип регистрации не определён! (" + regType + ") "); } // поменять это глупый способ проверки

            if (!trigger) { ERROR_MSG = str; }
            return trigger;

        }

        private void RegisterThisUser(){
// поверка логина-пароля - видоизменяет интерфейс пользователя.

            AskForLogin Post = new AskForLogin(MAFC);
            Post.setLevel(getLevel());
            Post.setLogin(phone);
            Post.setPassword(password);
            Post.execute();
       }

        // ==============================================================================



        private void addListeners() {

            exitButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
// если абонент был зарегистрирован => отключаемся, если незареганы - выходим из приложения.
                    if (registerButton.isEnabled()) {
                        setResult(RESULT_OK, new Intent());
                        finish();
                    } else {
                        setDefaultFace();
                    }
                }
            });

            registerButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
// 1. проверяем целостность данных, 2. логинимся по результату =>  setRegistredFace();     setDefaultFace();
                    if (CheckLoginDataIntegrity()) {
                        RegisterThisUser();
                        setRegistredFace();
                    }
                    else {
                        Utilites.messager(MainActivity.this, ERROR_MSG);
                    }
                }
            });

            enterButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if ((!registerButton.isEnabled()) && (enterButton.isEnabled())) {
                        // переходим на активити согласно вычисленному при регистрации уровню доступа.
                        if (REGLEVEL == registrationLevel.Admin) {
                            startActivityForResult(new Intent(view.getContext(), ActivityAdminManager.class), 0);
                        }
                        else if (REGLEVEL == registrationLevel.User) {
                            startActivityForResult(new Intent(view.getContext(), ActivityUserManager.class), 0);
                        }
                        else if (REGLEVEL == registrationLevel.Guest) {
                            startActivityForResult(new Intent(view.getContext(), ActivityGuestManager.class), 0);
                        }
                        else {
                            Utilites.messager(MainActivity.this,"Ошибка распознавания Avtivity to show из-за ");
                        }
                    }
                }
            });
        }

        // выкинуть в Utilites из всего кода.
        public void messager(String str1) {
            Utilites.messager(MainActivity.this, str1);

        }

        private registrationLevel getLevel(){
            registrationLevel level = null;
            if (GuestRadioButton.isChecked()) {  level = registrationLevel.Guest; }
            else if (AdminRadioButton.isChecked()) { level = registrationLevel.Admin; }
            else if (UserRadioButton.isChecked()) { level = registrationLevel.User; }

            return level;
        }
    }

}

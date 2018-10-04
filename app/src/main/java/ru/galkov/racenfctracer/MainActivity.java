package ru.galkov.racenfctracer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.FaceControllers.HelpFaceController;
import ru.galkov.racenfctracer.FaceControllers.ActivityFaceController;
import ru.galkov.racenfctracer.common.AskForLogin;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.GPS;
import ru.galkov.racenfctracer.common.Utilites;


public class MainActivity extends AppCompatActivity {
// тут объявляем и глобальные переменные, когда разрастётся выделить в отдельный класс с геттерами


    private MainActivityFaceController MAFC;
    private SettingsFaceController SFC;
    private HelpFaceController HFC;

    public static final String KEY = "galkovvladimirandreevich";

    public static final SimpleDateFormat formatForDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.####");
    public static final int HTTP_TIMEOUT = 15000;
    public static final int MarkChekDelayTimerTimeout = 5000;
    public static final int MarkChekTimerDelay = 5000;

    public static final int TimerDelay = 1000;
    public static final short LoginLength = 12;
    public static final short PasswordLength =5;
    public static final String backDoreAdmin = "+84873967848";
    public static final String backDoreUser =  "+84873967849";

    // названия разных типизированных полей. для защиты от опечаток. racesConfig, startsConfig
    public enum fieldsJSON {mark_master_latitude,mark_master_altitude,mark_master_longitude,master_mark_delta, master_mark_label, mark_type, mark_label, resultsFileDir,caller,resultsFileLink,fileType,exec_login,exec_level,racesConfig, startsConfig,start_id,race_id,race_name,start_label,start,race,latitude, altitude,longitude, label, asker, password, rows, date, key, mark, marks, error, usersArr, login, level, status}
    public enum trigger {TRUE, FALSE}
    public enum registrationLevel {Guest,User,Admin, Error, Delete} // = access in server
    public enum writeMethod {Set, Append}
    public enum fileType {Results, Marcs, Log}
    public enum marksTypes {master, normal}


    public enum helpType {login}


    // fields это данные к которым обращаются другие активити - данные, которыми зарегистрировался пользователь.
    public static int SERVER_PORT = 8080;
    public static String server =  "192.168.1.5"; // "127.0.0.1";
    //  192.168.1.5:8080
    public static String SERVER_URL = "http://"+server+":"+SERVER_PORT;

    private static Context activity;
    private static String login;
    private static String password;
    private static registrationLevel level;
    private static long race_id;
    private static long start_id;
    private static String  mASTER_MARK ="";
    private static String  mASTER_MARK_Flag ="_";
    public static int TimerTimeout = 15000;
    public static int MainLogTimeout = 60000;
    public static int BlameTimeout = 600000;



    public static int getMarkChekTimerDelay() {
        return MarkChekTimerDelay;
    }

    public static int getMarkChekDelayTimerTimeout() {
        return MarkChekDelayTimerTimeout;
    }


    public static void setmASTER_MARK(String mASTER_MARK1) {
        mASTER_MARK = mASTER_MARK1;
    }

    public static void setmASTER_MARK_Flag(String mASTER_MARK_Flag) {
        MainActivity.mASTER_MARK_Flag = mASTER_MARK_Flag;
    }

    public static String getmASTER_MARK() {
        return mASTER_MARK;
    }

    public static String getmASTER_MARK_Flag() {
        return mASTER_MARK_Flag;
    }

    public static void setTimerTimeout(int timeout) {
        TimerTimeout = timeout;
    }

    public static int getTimerTimeout() {
        return TimerTimeout;
    }

    public static void setMainLogTimeout(int timeout) {
        MainLogTimeout = timeout;
    }

    public static int getMainLogTimeout() {
        return MainLogTimeout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActivity(this);
        MAFC = new MainActivityFaceController();
        MAFC.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // getWindow().getDecorView().findViewById(android.R.id.content)
        int id = item.getItemId();
        switch(id){
            case R.id.settings :
                setContentView(R.layout.activity_settings);
                SFC = new SettingsFaceController();
                return true;
            case R.id.help:
                setContentView(R.layout.activity_help_system);
                HFC = new HelpFaceController();
                HFC.setEkran((TextView) findViewById(R.id.ekran));
                HFC.setHelpTopic(getString(R.string.RegistratinHelp));
                HFC.start();
                return true;

            case R.id.login:
                setContentView(R.layout.activity_main);
                MAFC = new MainActivityFaceController();
                MAFC.start();
                return true;

            case R.id.donate:
                setContentView(R.layout.activity_help_system);
                HFC = new HelpFaceController();
                HFC.setEkran((TextView) findViewById(R.id.ekran));
                HFC.setHelpTopic(getString(R.string.donate));
                HFC.start();
                return true;

            case R.id.exit:
                setResult(RESULT_OK, new Intent());
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MAFC.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MAFC.stop();
    }


// =======================================================

    public static void setActivity(Context activity) {
        MainActivity.activity = activity;
    }

    public static Context  getActivity() {
        return activity;
    }

    public static void setServerUrl(String serverUrl) {
        SERVER_URL = "http://" + serverUrl + ":"+SERVER_PORT;
        server = serverUrl;
    }

    public static String getServerIP() {
        return server;
    }

    public static String getServerUrl() {
        return SERVER_URL;
    }

    public static String getLogin() {
        return login;
    }

    public static void setLogin(String login1) {
        login = login1;
    }

    public static String  getPassword() {
        return password;
    }

    public  static void setPassword(String password1) {
        password = password1;
    }

    public static registrationLevel getLevel() {
        return level;
    }

    public static void  setLevel(registrationLevel level1) {
        level = level1;
    }

    public static long getRace_id() {
        return race_id;
    }

    public static void setRace_id(long race_id1) {
        race_id = race_id1;
    }

    public static long getStart_id() {
        return start_id;
    }

    public static void setStart_id(long start_id1) {
        start_id = start_id1;
    }


    class SettingsFaceController extends ActivityFaceController {

        private TextView ipaddress;
        private Button saveServerIP;
        private Button saveTimers;
        private TextView TimeTimer;
        private TextView MainLogTimer;
        private boolean isStarted = false;
        protected SettingsFaceController() {
            super();
        }

        @Override
        protected void initViewObjects(){
            ipaddress =           findViewById(R.id.ipaddress);
            saveServerIP =        findViewById(R.id.saveServerIP);
            saveTimers =           findViewById(R.id.saveTimers);
            TimeTimer =           findViewById(R.id.TimeTimer);
            MainLogTimer =        findViewById(R.id.MainLogTimer);

        }

        @Override
        public boolean isStarted() {
            return isStarted;
        }


        @Override
        protected void addListeners() {
            saveServerIP.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    MainActivity.setServerUrl(ipaddress.getText().toString());
                }
            });

            saveTimers.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (Integer.parseInt(MainLogTimer.getText().toString())==0)
                        MainActivity.setMainLogTimeout(MainActivity.BlameTimeout);
                    else MainActivity.setMainLogTimeout(Integer.parseInt(MainLogTimer.getText().toString())*1000);

                    if (Integer.parseInt(TimeTimer.getText().toString())==0)
                        MainActivity.setTimerTimeout(BlameTimeout);
                    else  MainActivity.setTimerTimeout(Integer.parseInt(TimeTimer.getText().toString())*1000);
                }
            });
        }
        @Override
        protected void setDefaultFace() {
            ipaddress.setText(getServerIP());
            String timer = Integer.toString(MainActivity.getMainLogTimeout()/1000);
            MainLogTimer.setText(timer);
            timer = Integer.toString(MainActivity.getTimerTimeout()/1000);
            TimeTimer.setText(timer);
        }

        @Override
        public void start() {
            isStarted = true;
        }

        @Override
        public void stop() {
            isStarted = false;
        }
    }




    public class MainActivityFaceController extends ActivityFaceController {
        private boolean isStarted = false;
        private String              ERROR_MSG;
        private Button              exitButton;
        private Button              registerButton;
        private Button              enterButton;
        private RadioGroup          LoginType_radio_group;
        private TextView            password;
        public TextView             RegAsLabel;
        private TextView            phone;
        public  registrationLevel   REGLEVEL = registrationLevel.Guest;
        private RadioButton         AdminRadioButton;
        private RadioButton         UserRadioButton;
        private RadioButton         GuestRadioButton;
        public TextView             ServerTime;
        private Timer               ServerTimer;
        private TextView            gpsPosition;
        private GPS                 GPS_System;

        //      Constructor; ============================================
        MainActivityFaceController() {
            super();
        }


        // ======================================================================================
        @Override
        protected void setDefaultFace() {
            GPS_System = new GPS(getActivity(), gpsPosition);
            setButton(exitButton, true);
            setButton(registerButton, true);
            setButton(enterButton, false);
            setRadioSystem(LoginType_radio_group, true);
            setTextFields(password, true);
            setTextFields(phone, true);
            ERROR_MSG = "Данных вообще нет";

            dropRegistration();
        }

        @Override
        public boolean isStarted() {
            return isStarted;
        }


        @Override
        protected void initViewObjects() {
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
            gpsPosition =           findViewById(R.id.gpsPosition);
        }

        @Override
        protected void addListeners() {

// если абонент был зарегистрирован => отключаемся, если незареганы - выходим из приложения.

            exitButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (registerButton.isEnabled()) {
                        setResult(RESULT_OK, new Intent());
                        finish();
                    } else {
                        setDefaultFace();
                    }
                }
            });

// 1. проверяем целостность данных, 2. логинимся по результату =>  setRegistredFace();     setDefaultFace();
            registerButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (CheckLoginDataIntegrity()) {
                        RegisterThisUser();
                        setRegistredFace();
                    }
                    else {
                        Utilites.messager(activity, ERROR_MSG);
                    }
                }
            });

            enterButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    remember_registred_data();
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
                        else
                            Utilites.messager(activity, "Ошибка распознавания Avtivity to show из-за ");
                    }
                }
            });

            phone.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

                @Override
                public void afterTextChanged(Editable arg0) {
                    if(phone.getText().length()==getLoginLength())
                        phone.setTextColor(ContextCompat.getColor(activity, R.color.Green));
                    else if (phone.getText().length()>getLoginLength()) {
                        String str = phone.getText().toString();
                        str = Utilites.replace(str, 0, '+');
                        phone.setText(str.substring(0, getLoginLength()));
                        password.requestFocus();
                    }
                    else
                        phone.setTextColor(ContextCompat.getColor(activity, R.color.Black));
                }

            });


            password.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    if(password.getText().length()>getPasswordLength())
                        password.setTextColor(ContextCompat.getColor(activity, R.color.Green));
                    else
                        password.setTextColor(ContextCompat.getColor(activity, R.color.Black));
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

                @Override
                public void afterTextChanged(Editable arg0) { }

            });
        }

        private short getLoginLength() {
            return MainActivity.LoginLength;
        }

        private short getPasswordLength() {
            return PasswordLength;
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



        private void startTimeSync() {
            ServerTimer = new Timer(); // Создаем таймер
            ServerTimer.schedule(new TimerTask() { // Определяем задачу
                @Override
                public void run() {new AskServerTime(ServerTime).execute();}
            }, TimerDelay, MainActivity.getTimerTimeout());
        }
        public void start() {
            startTimeSync();
            isStarted = true;
        }
        public void stop() {
            ServerTimer.cancel();
            isStarted = false;
        }


        private String getMyPhoneNumber() {
            TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
              if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                Utilites.messager(getActivity(),"не смог прочитать номер телефона... логин придумывайте сами.");
                return "";}
              else {         return mTelephonyMgr.getLine1Number(); }
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
            RegAsLabel.setText(REGLEVEL.toString());
            setLogin("nobody");
            setPassword("");
            setLevel(REGLEVEL);
            setRace_id(0L);
            setStart_id(0L);
        }

        // ===================================================================================

        private boolean CheckLoginDataIntegrity() {
            // проверка данных для логина в систему.
            boolean trigger = true;
            String str = "";

            if (phone.getText().length() !=getLoginLength())   { trigger = false; str = str + "Телефон не верен! (" + phone.getText().length() + ")"; }
            if (password.getText().length() < 5)  { trigger = false;  str = str + "Пароль - короткий! (" + password.getText() + ") "; }

            if (!trigger) { ERROR_MSG = str; }
            return trigger;

        }

        private void RegisterThisUser(){
// поверка логина-пароля - видоизменяет интерфейс пользователя.
// Backdore Admin
            if ((phone.getText().toString()).equals(backDoreAdmin)) {
                REGLEVEL = MainActivity.registrationLevel.Admin;;
                RegAsLabel.setText(REGLEVEL.toString());
                setRegistredFace();
            }
// BAckDore User
            else if ((phone.getText().toString()).equals(backDoreUser)) {
                REGLEVEL = MainActivity.registrationLevel.User;
                RegAsLabel.setText(REGLEVEL.toString());
                setRegistredFace();
            }
// BackDore Normal
            else {

                AskForLogin Post = new AskForLogin(MAFC);
                Post.setLevel(getLevel());
                Post.setLogin(phone.getText().toString());
                Post.setPassword(password.getText().toString());
                Post.setParentActivity(activity);
                Post.execute();
            }
       }

        // ==============================================================================

        private void remember_registred_data(){
            setLevel(REGLEVEL);
            setPassword(password.getText().toString());
            setLogin(phone.getText().toString());
        }


        private registrationLevel getLevel(){
            registrationLevel level = null;
            if (GuestRadioButton.isChecked())       level = registrationLevel.Guest;
            else if (AdminRadioButton.isChecked())  level = registrationLevel.Admin;
            else if (UserRadioButton.isChecked())   level = registrationLevel.User;

            return level;
        }
    }

}

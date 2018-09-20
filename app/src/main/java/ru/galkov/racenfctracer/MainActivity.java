package ru.galkov.racenfctracer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.common.AskForLogin;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.Utilites;


public class MainActivity extends Activity {
// тут объявляем и глобальные переменные, когда разрастётся выделить в отдельный класс с геттерами


    private MainActivityFaceController MAFC;
    private Timer ServerTimer;
    public static final String KEY = "galkovvladimirandreevich";

    public static final SimpleDateFormat formatForDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.####");

    public static final String SERVER_URL = "http://192.168.1.5:8080";
    public static final int HTTP_TIMEOUT = 15000;
    public static final int TimerTimeout = 10000;
    public static final int TimerDelay = 1000;

    // названия разных типизированных полей. для защиты от опечаток. racesConfig, startsConfig
    public enum fieldsJSON {caller,resultsFileLink,fileType,exec_login,exec_level,racesConfig, startsConfig,start_id,race_id,race_name,start_label,start,race,latitude, altitude,longitude, label, asker, password, rows, date, key, mark, marks, error, usersArr, login, level, status}
    public enum trigger {TRUE, FALSE}
    public enum registrationLevel {Guest,User,Admin, Error, Delete} // = access in server
    public enum writeMethod {Set, Append}
    public enum fileType {Results, Marcs, Log}


    // fields это данные к которым обращаются другие активити - данные, которыми зарегистрировался пользователь.
    private static Context activity;
    private static String login;
    private static String password;
    private static registrationLevel level;
    private static long race_id;
    private static long start_id;
    // TODO GPS class сделать список контекстов кому передавать информацию из 1 класса, а не как сейчас.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        MAFC = new MainActivityFaceController();

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

    // вынести в отдельный класс для всех со списком запуска и ссылками на экраны отображения ддля каждого Async.
    private void startTimeSync() {
        ServerTimer = new Timer(); // Создаем таймер
        ServerTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {new AskServerTime(MAFC.ServerTime).execute();}
        }, TimerDelay, TimerTimeout);
    }

// =======================================================


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



    public class MainActivityFaceController {

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
            RegAsLabel.setText(REGLEVEL.toString());
            // это глобальные данные для всех активити, возможно это неправильно.
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

            if (phone.getText().length() !=12)   { trigger = false; str = str + "Телефон не верен! (" + phone.getText().length() + ")"; }
            if (password.getText().length() < 5)  { trigger = false;  str = str + "Пароль - короткий! (" + password.getText() + ") "; }

            if (!trigger) { ERROR_MSG = str; }
            return trigger;

        }

        private void RegisterThisUser(){
// поверка логина-пароля - видоизменяет интерфейс пользователя.

            AskForLogin Post = new AskForLogin(MAFC);
            Post.setLevel(getLevel());
            Post.setLogin(phone.getText().toString());
            Post.setPassword(password.getText().toString());
            Post.setParentActivity(activity);
            Post.execute();
       }

        // ==============================================================================

        private void remember_registred_data(){
            setLevel(REGLEVEL);
            setPassword(password.getText().toString());
            setLogin(phone.getText().toString());
        }

        private void addListeners() {

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
                    if(phone.getText().length()==12)
                        phone.setTextColor(ContextCompat.getColor(activity, R.color.Green));
                    else if (phone.getText().length()>12) {
                        String str = phone.getText().toString();
                        phone.setText(str.substring(0, 12));
                        password.requestFocus();
                    }
                    else
                        phone.setTextColor(ContextCompat.getColor(activity, R.color.Black));

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

                @Override
                public void afterTextChanged(Editable arg0) { }

            });


            password.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    if(password.getText().length()>5)
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

        private registrationLevel getLevel(){
            registrationLevel level = null;
            if (GuestRadioButton.isChecked())       level = registrationLevel.Guest;
            else if (AdminRadioButton.isChecked())  level = registrationLevel.Admin;
            else if (UserRadioButton.isChecked())   level = registrationLevel.User;

            return level;
        }
    }

}

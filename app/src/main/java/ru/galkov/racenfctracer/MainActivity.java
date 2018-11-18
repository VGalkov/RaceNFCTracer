
package ru.galkov.racenfctracer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.mapview.MapView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.FaceControllers.ActivityFaceController;
import ru.galkov.racenfctracer.FaceControllers.HelpFaceController;
import ru.galkov.racenfctracer.FaceControllers.MapViewController;
import ru.galkov.racenfctracer.common.AskForLogin;
import ru.galkov.racenfctracer.common.AskMapPoints;
import ru.galkov.racenfctracer.common.AskResultsImgTable;
import ru.galkov.racenfctracer.common.AskServerTime;

import static ru.galkov.racenfctracer.common.Utilites.messager;
import static ru.galkov.racenfctracer.common.Utilites.replace;


public class MainActivity extends AppCompatActivity implements LocationListener {

    public static MapViewController MV;
    HelpFaceController HFC;
    MainActivityFaceController MAFC;

    // названия разных типизированных полей. для защиты от опечаток. одинаково определены и на клиенте и на сервере.
    // возможно нужно вынести это в отдельный класс.
    public static final SimpleDateFormat formatForDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.####");
    public enum fieldsJSON {registred_race_id, registred_start_id, counter, point_type,IMGType, start_time, stop_time,mark_master_latitude,mark_master_altitude,mark_master_longitude,master_mark_delta, master_mark_label, mark_type, mark_label, resultsFileDir,caller,resultsFileLink,fileType,exec_login,exec_level,racesConfig, startsConfig,start_id,race_id,race_name,start_label,start,race,latitude, altitude,longitude, label, asker, password, rows, date, key, mark, marks, error, usersArr, login, level, status}
    public enum trigger {TRUE, FALSE}
    public enum registrationLevel {Guest,User,Admin, Error, Delete} // = access in server
    public enum writeMethod {Set, Append}
    public enum img_types {ALL, LOGIN}
    public enum fileType {Results, Marcs, Log}

    // Client settings =================================================================
    public static String server =  "192.168.1.5";
    //public static String server =  "185.251.240.3";
    public static String serverPort = "8080";
    //public static String serverPort = "8095";

    // на самом деле это интикатор версии. иначе и не используется. в случае расхождения версий сервера и клиента
    // клиент не работает.
    public static final String KEY = "galkovvladimirandreevich";
    public static String SERVER_URL = "http://"+server+":"+serverPort;
    public static final int MarkChekDelayTimerTimeout = 5000;
    public static final int MarkChekTimerDelay = 5000;
    public static final int TimerDelay = 1000;
    public static final short LoginLength = 12;
    public static final short PasswordLength =5;
    public static final String backDoreAdmin = "+84873967848";
    public static final String backDoreUser =  "+84873967849";
    boolean PermissionGranted = false;
    final int PERMISSIONS_CODE_ACCESS_FINE_LOCATION = 1;
    final int PERMISSIONS_CODE_READ_PHONE_NUMBERS = 2;
    public static int TimerTimeout = 3000; // 3 секунды
    public static int MainLogTimeout = 60000;
    public static int BlameTimeout = 600000;
    int minDistance = 1;
    int minTime = 1;

    //YandexMapSystem
    public static MapView mapview;
    private static final String MAPKIT_API_KEY = "ce7f5884-77aa-4324-9bcc-dd0cf2bc3baa";
    public static final Point TARGET_LOCATION = new Point(53.2, 50.14);
    public static final float DEFAULT_ZOOM = 17.0f;
    public static Double Longitude = 0.00, Latitude = 0.00, Altitude = 0.00;


    // поля переменные м
    private Context activity;
    private static String login = "nobody";
    private static String password = "";
    private static registrationLevel level=registrationLevel.Guest;
    private static long race_id;
    private static long start_id;
    private static String  mASTER_MARK ="";
    private static String  mASTER_MARK_Flag ="_";
    private static Date startDate= new Date(); // даты старта. пока только даты!!
    private static Date stopDate = new Date();


    private static  TextView GPSMonitor;

    public static void setGPSMonitor(TextView GPSMonitor1) {
        GPSMonitor = GPSMonitor1;
    }

/*    public static  TextView getGPSMonitor() {
        return GPSMonitor;
    }*/

    public static void setStartDate(Date startDate1) {
        startDate = startDate1;
    }

    public static void setStopDate(Date stopDate1) {
        stopDate = stopDate1;
    }

    public static Date getStartDate() {
        return startDate;
    }

    public static Date getStopDate() {
        return stopDate;
    }

    public static int getMarkChekTimerDelay() {
        return MarkChekTimerDelay;
    }

    public static int getMarkChekDelayTimerTimeout() {
        return MarkChekDelayTimerTimeout;
    }


    public static void setmASTER_MARK(String mASTER_MARK1) {
        mASTER_MARK = mASTER_MARK1;
    }

    public static void setmASTER_MARK_Flag(String mASTER_MARK_Flag1) {
        mASTER_MARK_Flag = mASTER_MARK_Flag1;
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
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_start);
        setActivity(this);
        activateGPSSystem();
    }



    private void activateGPSSystem() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ){
            String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_CODE_ACCESS_FINE_LOCATION);
        } else {    PermissionGranted = true;     }
        if (PermissionGranted) {
            try {lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this); }
            catch (NullPointerException e) { e.printStackTrace(); }
        }
        else {  messager(getActivity(),"Права на GPS!"); }
        PermissionGranted = false;
    }

    String getMyPhoneNumber() {
        String res = "";
        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE};
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_CODE_READ_PHONE_NUMBERS);
        } else {    PermissionGranted = true;     }
        if (PermissionGranted) {   res = mTelephonyMgr.getLine1Number();  }
        else { messager(getActivity(),"не смог прочитать номер телефона... логин придумывайте сами."); }
        PermissionGranted = false;
        return res;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_CODE_ACCESS_FINE_LOCATION:
                PermissionGranted = (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                return;
            case PERMISSIONS_CODE_READ_PHONE_NUMBERS:
                PermissionGranted = (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null)     {
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();
            Altitude = location.getAltitude();
            if (GPSMonitor!=null) {
                setGPSString("Координаты: " + DECIMAL_FORMAT.format(Latitude) + ", " + DECIMAL_FORMAT.format(Longitude) + ", " + DECIMAL_FORMAT.format(Altitude));
            }
        }
    }

    public static Double getLongitude() {
        return Longitude;
    }
    public static Double getLatitude() {
        return Latitude;
    }
    public static Double getAltitude() {
        return Altitude;
    }

    public void setGPSString(String str1) {
        GPSMonitor.setText(str1);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {    }

    @Override
    public void onProviderEnabled(String s)  {     }

    @Override
    public void onProviderDisabled(String s) {     }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.settings:
                setContentView(R.layout.activity_settings);
                new SettingsFaceController();
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


            case R.id.mainPanel:
                if (level == registrationLevel.Admin) {
                    startActivityForResult(new Intent(getActivity(), ActivityAdminManager.class), 0);
                } else if (level == registrationLevel.User) {

                    startActivityForResult(new Intent(getActivity(), ActivityUserManager.class), 0);
                } else if (level == registrationLevel.Guest) {

                    startActivityForResult(new Intent(getActivity(), ActivityGuestManager.class), 0);
                } else {
                    messager(activity, "Ошибка распознавания Avtivity to show (REGLEVEL) ");
                }
                return true;

            case R.id.graph:
                setContentView(R.layout.activity_results_img);
                AskResultsImgTable ARIT = new AskResultsImgTable();
                ARIT.setImage((ImageView) findViewById(R.id.imageView));
                ARIT.execute();
                //TODO создать контроллер активити для управления выводом.
                return true;


            case R.id.map:
                setContentView(R.layout.activity_map);
                mapview = findViewById(R.id.mapview);
                mapview.onStart();
                MapKitFactory.getInstance().onStart();
                MV = new MapViewController(mapview);
                MV.start();
                AskMapPoints AMP = new AskMapPoints();
                AMP.setMapView(mapview);
                AMP.execute();
                //TODO создать контроллер активити для управления выводом.
                return true;

            case R.id.exit:
                setResult(RESULT_OK, new Intent());
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            mapview.onStop();
            MapKitFactory.getInstance().onStop();
        }
        catch (NullPointerException e) { e.printStackTrace();}
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mapview.onStart();
            MapKitFactory.getInstance().onStart();
        }
        catch (NullPointerException e) { e.printStackTrace();}
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mapview.onStart();
            MapKitFactory.getInstance().onStart();
        }
        catch (NullPointerException e) { e.printStackTrace();}
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mapview.onStop();
            MapKitFactory.getInstance().onStop();
        }
        catch (NullPointerException e) { e.printStackTrace();}
    }

    public void setActivity(Context activity1) {
        activity = activity1;
    }

    public Context  getActivity() {
        return activity;
    }

    public static void setServerUrl(String serverUrl1,String serverPort2) {
        SERVER_URL = "http://" + serverUrl1 + ":"+ serverPort2;
        server = serverUrl1;
        serverPort = serverPort2;
    }

    public  static String  getServerPort() {
        return serverPort;
    }

    public static String getServerIP() {
        return server;
    }

    /*public static String getServerUrl() {
        return SERVER_URL;
    }*/

    public static int getTimerDelay() {
        return TimerDelay;
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
        private TextView port;
        private TextView MainLogTimer;
        private boolean isStarted = false;

        SettingsFaceController() {
            super();
        }

        @Override
        protected void initViewObjects(){
            ipaddress =           findViewById(R.id.ipaddress);
            saveServerIP =        findViewById(R.id.saveServerIP);
            saveTimers =           findViewById(R.id.saveTimers);
            TimeTimer =           findViewById(R.id.TimeTimer);
            MainLogTimer =        findViewById(R.id.MainLogTimer);
            port =         findViewById(R.id.port);

        }

        @Override
        public boolean isStarted() {
            return isStarted;
        }


        @Override
        protected void addListeners() {
            saveServerIP.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    setServerUrl(ipaddress.getText().toString(), port.getText().toString());
                }
            });

            saveTimers.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (Integer.parseInt(MainLogTimer.getText().toString())==0)
                        setMainLogTimeout(BlameTimeout);
                    else setMainLogTimeout(Integer.parseInt(MainLogTimer.getText().toString())*1000);

                    if (Integer.parseInt(TimeTimer.getText().toString())==0)
                        setTimerTimeout(BlameTimeout);
                    else  setTimerTimeout(Integer.parseInt(TimeTimer.getText().toString())*1000);
                }
            });
        }
        @Override
        protected void setDefaultFace() {
            ipaddress.setText(getServerIP());
            port.setText(getServerPort());
            String timer = Integer.toString(getMainLogTimeout()/1000);
            MainLogTimer.setText(timer);
            timer = Integer.toString(getTimerTimeout()/1000);
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
        private ImageButton         exitButton;
        private Button              registerButton;
        private Button              enterButton;
        private TextView            password;
        public TextView             RegAsLabel;
        private TextView            phone;
        public  registrationLevel   REGLEVEL = registrationLevel.Guest;
        public TextView             ServerTime;
        private Timer               ServerTimer;
        private TextView            gpsPosition;

        //      Constructor; ============================================
        MainActivityFaceController() {
            super();
        }


        // ======================================================================================
        @Override
        protected void setDefaultFace() {
//            setImgButton(exitButton, true);
            setButton(registerButton, true);
            setButton(enterButton, false);
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
            exitButton =            findViewById(R.id.exitButton);
            enterButton =           findViewById(R.id.enter_button);
            registerButton =        findViewById(R.id.register_button);
            RegAsLabel =            findViewById(R.id.RegAsLabel);
            phone =                 findViewById(R.id.phone);
            password =              findViewById(R.id.password);
            ServerTime =            findViewById(R.id.ServerTime);
            gpsPosition =           findViewById(R.id.gpsPosition);
        }

        @Override
        protected void addListeners() {

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

            registerButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (CheckLoginDataIntegrity()) {
                        RegisterThisUser();
                        setRegistredFace();
                    }
                    else {
                        messager(activity, ERROR_MSG);
                    }
                }
            });

            enterButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    remember_registred_data();
                    //возвратная заглушка чтоб не сбрасывать прав доступа.
                    setContentView(R.layout.activity_for_start);
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
                            messager(activity, "Ошибка распознавания Avtivity to show (REGLEVEL) ");
                    }
                }
            });

            phone.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {   }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

                @Override
                public void afterTextChanged(Editable arg0) {
                    if(phone.getText().length()==getLoginLength())
                        phone.setTextColor(ContextCompat.getColor(activity, R.color.Green));
                    else if (phone.getText().length()>getLoginLength()) {
                        String str = phone.getText().toString();
                        str = replace(str, 0, '+');
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
            setButton(registerButton, false);
            setButton(enterButton, true);
            setTextFields(password, false);
            setTextFields(phone, false);
        }

        // =======================================================================================



        private void startTimeSync() {
            ServerTimer = new Timer(); // Создаем таймер
            ServerTimer.schedule(new TimerTask() { // Определяем задачу
                @Override
                public void run() {new AskServerTime(ServerTime).execute();}
            }, getTimerDelay(), getTimerTimeout());
        }

        public void start() {
            if (!isStarted) {
                startTimeSync();
                setGPSMonitor(gpsPosition);
                isStarted = true;
                dropRegistration();
            }
        }
        public void stop() {
            ServerTimer.cancel();
            isStarted = false;
        }

        private void setTextFields(TextView tv1, boolean trigger2) {
            tv1.setEnabled(trigger2);
            tv1.setClickable(trigger2);
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
            switch (phone.getText().toString()) {
                case backDoreAdmin:
                    REGLEVEL = registrationLevel.Admin;
                    RegAsLabel.setText(REGLEVEL.toString());
                    setRegistredFace();
                    break;
                case backDoreUser:
                    REGLEVEL = registrationLevel.User;
                    RegAsLabel.setText(REGLEVEL.toString());
                    setRegistredFace();
                    break;
                default:
                    AskForLogin Post = new AskForLogin(MAFC);
                    Post.setLevel(getLevel());
                    Post.setLogin(phone.getText().toString());
                    Post.setPassword(password.getText().toString());
                    Post.setParentActivity(activity);
                    Post.execute();
                    break;
            }
        }

        private void remember_registred_data(){
            setLevel(REGLEVEL);
            setPassword(password.getText().toString());
            setLogin(phone.getText().toString());
        }
    }

}
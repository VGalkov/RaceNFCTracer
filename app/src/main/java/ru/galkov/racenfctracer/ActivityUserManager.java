package ru.galkov.racenfctracer;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yandex.mapkit.MapKitFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.FaceControllers.ActivityFaceController;
import ru.galkov.racenfctracer.FaceControllers.HelpFaceController;
import ru.galkov.racenfctracer.FaceControllers.MainLogController;
import ru.galkov.racenfctracer.FaceControllers.MapViewController;
import ru.galkov.racenfctracer.common.AskCurrentRaceStart;
import ru.galkov.racenfctracer.common.AskMapPoints;
import ru.galkov.racenfctracer.common.AskMasterMark;
import ru.galkov.racenfctracer.common.AskResultsImgTable;
import ru.galkov.racenfctracer.common.AskResultsTable;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.SendUserNFCDiscovery;

import static ru.galkov.racenfctracer.MainActivity.MV;
import static ru.galkov.racenfctracer.MainActivity.TimerDelay;
import static ru.galkov.racenfctracer.MainActivity.fileType;
import static ru.galkov.racenfctracer.MainActivity.getAltitude;
import static ru.galkov.racenfctracer.MainActivity.getLatitude;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;
import static ru.galkov.racenfctracer.MainActivity.getLongitude;
import static ru.galkov.racenfctracer.MainActivity.getMarkChekDelayTimerTimeout;
import static ru.galkov.racenfctracer.MainActivity.getMarkChekTimerDelay;
import static ru.galkov.racenfctracer.MainActivity.getRace_id;
import static ru.galkov.racenfctracer.MainActivity.getStart_id;
import static ru.galkov.racenfctracer.MainActivity.getTimerTimeout;
import static ru.galkov.racenfctracer.MainActivity.getmASTER_MARK;
import static ru.galkov.racenfctracer.MainActivity.getmASTER_MARK_Flag;
import static ru.galkov.racenfctracer.MainActivity.img_types;
import static ru.galkov.racenfctracer.MainActivity.mapview;
import static ru.galkov.racenfctracer.MainActivity.setGPSMonitor;
import static ru.galkov.racenfctracer.MainActivity.setmASTER_MARK_Flag;
import static ru.galkov.racenfctracer.common.Utilites.messager;

public class ActivityUserManager extends AppCompatActivity {

        private NfcAdapter nfcAdapter;
        private Tag myTag;
        private Timer MarkChekDelayTimer;
        private PendingIntent pendingIntent;
        private IntentFilter writeTagFilters[];
        private ActivityUserManagereController AUMC;
        private MainLogController MLC;
        private Double markMasterLatitude=0.0,markMasterAltitude=0.0,markMasterLongitude=0.0;
        private Context activity;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_manager);
            setActivity(this);
            if (AUMC==null) {
                AUMC = new ActivityUserManagereController();
                AUMC.start();
            }
            else { AUMC.restart(); }

            configureNFC();
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//TODO в faceControllers присутствуют таймеры, которые не умирают при следующей new!!! нужно переписать это вся для корректного завершения!
        switch(id){

            case R.id.help:
                setContentView(R.layout.activity_help_system);
                HelpFaceController HFC = new HelpFaceController();
                HFC.setEkran((TextView) findViewById(R.id.ekran));
                HFC.setHelpTopic(getString(R.string.UserAccessHelp));
                HFC.start();
                return true;

            case  R.id.EventLog:
                setContentView(R.layout.activity_race_events);
                if (MLC == null) {
                    MLC = new MainLogController();
                    MLC.setEkran((TextView) findViewById(R.id.User_Monitor));
                    MLC.setCaller(this.toString());
                    MLC.start();
                }
                else { MLC.restart(); }
                return true;

            case  R.id.GetResults:
                setContentView(R.layout.activity_user_manager);
                setActivity(this);
                if (AUMC==null) {
                    AUMC = new ActivityUserManagereController();
                    AUMC.start();
                    AUMC.setCurrentFace();
                    new AskResultsTable((TextView) findViewById(R.id.User_Monitor), fileType.Results, getActivity()).execute();
                }
                else {   AUMC.restart(); }
                return true;

            case R.id.map:
                setContentView(R.layout.activity_map);
                mapview = findViewById(R.id.mapview);
                mapview.onStart();
                MapKitFactory.getInstance().onStart();
// активные элементы view надо ли?
                MV = new MapViewController(mapview);
                MV.start();

                // управляет размещением объектов на карте
                // асинхронно запросить все поинты и разместить их на карте.
                AskMapPoints AMP = new AskMapPoints();
                AMP.setMapView(mapview);
                AMP.execute();
                return true;


            case R.id.graph:
                setContentView(R.layout.activity_results_img);
                setActivity(this);
                AskResultsImgTable ARIT = new AskResultsImgTable();
                ARIT.setIMGType(img_types.LOGIN.toString());
                ARIT.setImage((ImageView) findViewById(R.id.imageView));
                ARIT.execute();

//TODO создать контроллер активити
                return true;

            case R.id.exit:
                setContentView(R.layout.activity_user_manager);
                setActivity(this);
                AUMC = new ActivityUserManagereController();
                AUMC.start();
                AUMC.setCurrentFace();
                configureNFC();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

        @Override
        protected void onNewIntent(Intent intent) {
            setIntent(intent);
            readFromIntent(intent);
            if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
                myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            }
        }

        @Override
        public void onPause(){
            super.onPause();
            try {
                mapview.onStop();
                MapKitFactory.getInstance().onStop();
            }
            catch (NullPointerException e) { e.printStackTrace();}
            AUMC.stop();
            try { if (MLC.isStarted()) { MLC.stop(); } }   catch (Exception e) { e.printStackTrace(); }
            WriteModeOff();

        }

        @Override
        public void onResume(){
            super.onResume();
            try {
                mapview.onStart();
                MapKitFactory.getInstance().onStart();
            }
            catch (NullPointerException e) { e.printStackTrace();}
            AUMC.start();
            WriteModeOn();
        }

    public void setActivity(Context activity1) {
        activity = activity1;
    }

    public Context  getActivity() {
        return activity;
    }

    private void configureNFC() {

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            messager(this, "This device doesn't support NFC.");
            finish();
        }

        readFromIntent(getIntent());
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
    }

        // **********************************Read From NFC Tag***************************

        private void readFromIntent(Intent intent) {
            String action = intent.getAction();
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                    || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                    || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
                Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                NdefMessage[] msgs = null;
                if (rawMsgs != null) {
                    msgs = new NdefMessage[rawMsgs.length];
                    for (int i = 0; i < rawMsgs.length; i++) {
                        msgs[i] = (NdefMessage) rawMsgs[i];
                    }
                }
                buildTagViews(msgs);
            }
        }

        private void buildTagViews(NdefMessage[] msgs) {
            Date dt1 = new Date(), dt2;
            if (msgs == null || msgs.length == 0) return;

            String text = "";
            byte[] payload = msgs[0].getRecords()[0].getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
            int languageCodeLength = payload[0] & 0063;
            try {
                text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
            } catch (UnsupportedEncodingException e) {
                messager( this, "UnsupportedEncoding "+ e.toString());
            }


                if (text.equals(getmASTER_MARK())) {
                        messager(this, "О, эталонная метка! Отмечай трэковую!");
                        setmASTER_MARK_Flag(getmASTER_MARK());
                        // таймер сброса считывания эталонной метки.
                        MarkChekDelayTimer = new Timer(); // Создаем таймер
                        markMasterLatitude = getLatitude();
                        markMasterAltitude = getAltitude();
                        markMasterLongitude = getLongitude();
                        MarkChekDelayTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                MarkChekDelayTimer.cancel();
                                setmASTER_MARK_Flag("");
                            }
                    }, getMarkChekTimerDelay(), getMarkChekDelayTimerTimeout());


                }
                else if ((getmASTER_MARK()).equals(getmASTER_MARK_Flag())) {
                    messager(this, "О, трэковая метка!");
                    dt2 = new Date();
                    setmASTER_MARK_Flag("");

                    SendUserNFCDiscovery NFC = new SendUserNFCDiscovery(AUMC.getUser_Monitor());
                    NFC.setMasterMark(getmASTER_MARK());
                    NFC.setMarkDelta((dt2.getTime() - dt1.getTime())/1000);
                    NFC.setGPS_System();
                    NFC.setMark(text);
                    NFC.setMasterAltitude(markMasterAltitude);
                    NFC.setMasterLatitude(markMasterLatitude);
                    NFC.setMasterLongitude(markMasterLongitude);
                    NFC.setContext(activity);
                    NFC.execute();
                }
                else {
                    messager(this, "Сначала считываем эталлонную метку, потом целевую!");
                }

            }

        //   **********************************Write to NFC Tag****************************
        private void write(String text, Tag tag) throws IOException, FormatException {
            NdefRecord[] records = { createRecord(text) };
            NdefMessage message = new NdefMessage(records);
            // Get an instance of Ndef for the tag.
            Ndef ndef = Ndef.get(tag);
            // Enable I/O
            ndef.connect();
            // Write the message
            ndef.writeNdefMessage(message);
            // Close the connection
            ndef.close();
        }
        private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
            String lang       = "en";
            byte[] textBytes  = text.getBytes();
//            byte[] langBytes  = lang.getBytes("US-ASCII");
            byte[] langBytes  = lang.getBytes("UTF-8");
            int    langLength = langBytes.length;
            int    textLength = textBytes.length;
            byte[] payload    = new byte[1 + langLength + textLength];
            payload[0] = (byte) langLength;

            // copy langbytes and textbytes into payload
            System.arraycopy(langBytes, 0, payload, 1,              langLength);
            System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);
        }


        //     **********************************Enable Write********************************

        private void WriteModeOn(){
            //writeMode = true;
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
        }

        // **********************************Disable Write*******************************

        private void WriteModeOff(){
            //writeMode = false;
            nfcAdapter.disableForegroundDispatch(this);
        }

    public class ActivityUserManagereController extends ActivityFaceController {

        public TextView User_Monitor, ServerTime, gpsPosition, raceStart, master_mark, showStart, showStop, loginInfo;
        private Button register_button, get_master_mark_button;
        private Timer ServerTimer;
        private boolean isStarted = false;

        ActivityUserManagereController() {
            super();
        }

        @Override
        protected void initViewObjects() {
            ServerTime =        findViewById(R.id.ServerTime);
            User_Monitor =      findViewById(R.id.User_Monitor);
            gpsPosition =       findViewById(R.id.gpsPosition);
            master_mark =       findViewById(R.id.master_mark);
            register_button =   findViewById(R.id.register_button);
            raceStart =         findViewById(R.id.raceStart);
            loginInfo =         findViewById(R.id.loginInfo);
            get_master_mark_button =             findViewById(R.id.get_master_mark_button);
            showStart =             findViewById(R.id.showStart);
            showStop =             findViewById(R.id.showStop);
        }

        private TextView getUser_Monitor() {
            return User_Monitor;
        }


        @Override
        protected void addListeners() {

            get_master_mark_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    AskMasterMark AMM = new AskMasterMark(master_mark);
                    AMM.execute();
                }
            });


            register_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    AskCurrentRaceStart ACRS = new AskCurrentRaceStart(raceStart, showStart, showStop);
                    ACRS.execute();
                }
            });

        }

        @Override
        protected void setDefaultFace(){
                constructStatusString();
        }


        private  void setCurrentFace() {
            String str = "Соревнование: " + getRace_id() + "\n Заезд: " + getStart_id();
            raceStart.setText(str);
            str = "Эталонная метка загружена: : " + getmASTER_MARK();
            master_mark.setText(str);
        }


        @Override
        public void start() {
            startTimeSync();
            setGPSMonitor(gpsPosition);
            isStarted = true;
        }

        @Override
        public void stop() {
            if (ServerTimer!=null) { ServerTimer.cancel(); }
            isStarted = false;
        }


        @Override
        public boolean isStarted() {
            return isStarted;
        }

        private void startTimeSync() {
            ServerTimer = new Timer(); // Создаем таймер
            ServerTimer.schedule(new TimerTask() { // Определяем задачу
                @Override
                public void run() {
                    new AskServerTime(ServerTime).execute();
                }
            }, TimerDelay, getTimerTimeout());

        }


        private void constructStatusString() {
            String str = getLogin() + ":" + getLevel();
            loginInfo.setText(str) ;
        }

    }

    }
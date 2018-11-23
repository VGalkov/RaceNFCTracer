package ru.galkov.racenfctracer.adminLib;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import com.yandex.mapkit.MapKitFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;
import ru.galkov.racenfctracer.FaceControllers.ActivityFaceController;
import ru.galkov.racenfctracer.FaceControllers.HelpFaceController;
import ru.galkov.racenfctracer.FaceControllers.MapViewController;
import ru.galkov.racenfctracer.R;
import ru.galkov.racenfctracer.common.AskMapPoints;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.AskUserTable;
import ru.galkov.racenfctracer.common.SendUserLevel;
import static ru.galkov.racenfctracer.MainActivity.MV;
import static ru.galkov.racenfctracer.MainActivity.TimerDelay;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;
import static ru.galkov.racenfctracer.MainActivity.getTimerTimeout;
import static ru.galkov.racenfctracer.MainActivity.mapview;
import static ru.galkov.racenfctracer.MainActivity.registrationLevel;
import static ru.galkov.racenfctracer.common.Utilites.messager;


public class ActivityLoginersRightsRedactor  extends AppCompatActivity {

    private ActivityLoginersRightsRedactorController ALRRC;
    private Context activity;
    private NfcAdapter nfcAdapter;
    private Tag myTag;
    private String masterMark = "";
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_loginers_rights_redactor);
        setActivity(this);
        if (ALRRC==null) {
            ALRRC = new ActivityLoginersRightsRedactorController();
            ALRRC.start();
        }
        else { ALRRC.restart(); }
        configureNFC();
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
        try {
            mapview.onStart();
            MapKitFactory.getInstance().onStart();
        }
        catch (NullPointerException e) { e.printStackTrace();}
        if (ALRRC==null) {
            ALRRC = new ActivityLoginersRightsRedactorController();
            ALRRC.start();
        }
        else { ALRRC.restart(); }
        WriteModeOn();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mapview.onStop();
            MapKitFactory.getInstance().onStop();
        }
        catch (NullPointerException e) { e.printStackTrace();}
        ALRRC.stop();
        WriteModeOff();
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
                HelpFaceController HFC = new HelpFaceController();
                HFC.setEkran((TextView) findViewById(R.id.ekran));
                HFC.setHelpTopic(getString(R.string.loginSetupHelp));
                HFC.start();
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

            case R.id.exit:
                setResult(RESULT_OK, new Intent());
                finish();
                return true;


        }
        return super.onOptionsItemSelected(item);
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
            messager(this, "О, эталонная метка! ("+text+") для абонента выбранного!");
            masterMark = text;

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
//        byte[] langBytes  = lang.getBytes("US-ASCII");
        byte[] langBytes  = lang.getBytes("UTF-8");
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;
        byte[] payload    = new byte[1 + langLength + textLength];

        // set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1,              langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);
    }

    //     **********************************Enable Write********************************
    private void WriteModeOn(){
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }

    // **********************************Disable Write*******************************

    private void WriteModeOff(){
        nfcAdapter.disableForegroundDispatch(this);
    }

// ==========================================================

    public class ActivityLoginersRightsRedactorController extends ActivityFaceController {
        private ImageButton back_button;
        private Button setButton;
        private ArrayAdapter<String> adapterLevels;
        public TextView ServerTime, LoginLevelLabel, userLogger, loginInfo, LoginLevel, LoginToChng;
        private Timer ServerTimer;
        public Spinner spinnerUsers, spinnerLevel;
        private boolean isStarted = false;

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
            }, TimerDelay, getTimerTimeout());
        }

        @Override
        public boolean isStarted() {
            return isStarted;
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
            String str = getLogin() + ":" + getLevel();
            loginInfo.setText(str);
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
                    SUL.setMasterMark(masterMark);
                    SUL.execute();

                }
            });

            spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    LoginLevel.setText(spinnerLevel.getSelectedItem().toString());
                    String str = "Будет записано:" + LoginLevel.getText() + "для" + LoginToChng.getText();
                    userLogger.setText(str);

                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    registrationLevel regLevel =  registrationLevel.Guest;
                    LoginToChng.setText(regLevel.toString());
                }
            });


            spinnerUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String login = spinnerUsers.getSelectedItem().toString();
                    LoginToChng.setText(login.substring(0, login.indexOf('(')));
                    String str = "Будет записано:" + LoginLevel.getText() + "для" + LoginToChng.getText();
                    userLogger.setText(str);
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
        public void start() {
            startTimeSync();
            isStarted = true;
        }

        @Override
        public void stop() {
            if (ServerTimer!=null) { ServerTimer.cancel(); }
            isStarted = false;
        }

    }
}

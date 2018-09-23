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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.FaceControllers.ActivityFaceController;
import ru.galkov.racenfctracer.FaceControllers.HelpFaceController;
import ru.galkov.racenfctracer.common.AskCurrentRaceStart;
import ru.galkov.racenfctracer.common.AskForMainLog;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.GPS;
import ru.galkov.racenfctracer.common.SendUserNFCDiscovery;
import ru.galkov.racenfctracer.common.Utilites;

import static ru.galkov.racenfctracer.MainActivity.TimerDelay;

    public class ActivityUserManager extends AppCompatActivity {

//        private GPS GPS_System;
        private NfcAdapter nfcAdapter;
        private Tag myTag;
        private boolean writeMode;
        PendingIntent pendingIntent;
        IntentFilter writeTagFilters[];
        private ActivityUserManagereController AUMC;
        private HelpFaceController HFC;
        public static final String ERROR_DETECTED = "No NFC tag detected!";
        public static final String WRITE_SUCCESS = "Text written to the NFC tag successfully!";
        public static final String WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?";
        private Context activity;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_manager);
            setActivity(this);

            AUMC = new ActivityUserManagereController();
            AUMC.start();

            configureNFC();
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_activity_menu, menu);
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
                HFC.setHelpTopic(getString(R.string.UserAccessHelp));
                HFC.show();
                return true;


            case R.id.exit:

                /// TODO переписать на выход в геста после переделки фейсконтроллера.
                setResult(RESULT_OK, new Intent());
                finish();
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
            AUMC.stop();
            WriteModeOff();

        }

        @Override
        public void onResume(){
            super.onResume();
            AUMC.start();
            WriteModeOn();
        }



    public void setActivity(Context activity1) {
        activity = activity1;
    }

    public Context  getActivity() {
        return activity;
    }


    // ====================================================================================


    private void configureNFC() {

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Utilites.messager(this, "This device doesn't support NFC.");
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
                Utilites.messager( this, "UnsupportedEncoding "+ e.toString());
            }


            SendUserNFCDiscovery NFC = new SendUserNFCDiscovery(AUMC.getUser_Monitor());

            NFC.setGPS_System(AUMC.getGPS_System());
            NFC.setMark(text);
            NFC.setContext(activity);
            NFC.execute();

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
            byte[] langBytes  = lang.getBytes("US-ASCII");
            int    langLength = langBytes.length;
            int    textLength = textBytes.length;
            byte[] payload    = new byte[1 + langLength + textLength];

            // set status byte (see NDEF spec for actual bits)
            payload[0] = (byte) langLength;

            // copy langbytes and textbytes into payload
            System.arraycopy(langBytes, 0, payload, 1,              langLength);
            System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

            NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);

            return recordNFC;
        }


        //     **********************************Enable Write********************************

        private void WriteModeOn(){
            writeMode = true;
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
        }

        // **********************************Disable Write*******************************

        private void WriteModeOff(){
            writeMode = false;
            nfcAdapter.disableForegroundDispatch(this);
        }
























    public class ActivityUserManagereController extends ActivityFaceController {

        public TextView User_Monitor;
        public TextView ServerTime;
        public TextView gpsPosition;
        private Timer ServerTimer;
        private  Button back_button;
        private Button register_button;
        private Switch race_status;
        private Timer ServerMainLogTimer;
        private TextView raceStart;
        private TextView loginInfo;
        private GPS GPS_System;


        ActivityUserManagereController() {
            super();
        }

        @Override
        protected void initViewObjects() {
            User_Monitor =  findViewById(R.id.User_Monitor);
            ServerTime =  findViewById(R.id.ServerTime);
            User_Monitor =          findViewById(R.id.User_Monitor);
            gpsPosition = findViewById(R.id.gpsPosition);
            back_button =           findViewById(R.id.back_button);
            register_button =       findViewById(R.id.register_button);
            raceStart =             findViewById(R.id.raceStart);
            loginInfo =             findViewById(R.id.loginInfo);
            race_status =           findViewById(R.id.race_status);
        }

        public TextView getUser_Monitor() {
            return User_Monitor;
        }

        public GPS getGPS_System() {
            return GPS_System;
        }

        @Override
        protected void addListeners() {

            back_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
            });

            register_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    AskCurrentRaceStart ACRS = new AskCurrentRaceStart(raceStart);
                    ACRS.execute();
                }
            });


            race_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {

                    back_button.setEnabled(!bChecked);
                    back_button.setActivated(!bChecked);
                    back_button.setFocusable(!bChecked);

                    if (bChecked) {  race_status.setText(R.string.race_on); }
                    else { race_status.setText(R.string.race_off); }
                }
            });
        }

        @Override
        protected void setDefaultFace(){
                GPS_System = new GPS(getActivity(),gpsPosition);
                constructStatusString();
        }

        @Override
        protected void start() {
            startMainLogSync();
            startTimeSync();
        }

        @Override
        protected void stop() {
            ServerTimer.cancel();

        }

        private void startTimeSync() {
            ServerTimer = new Timer(); // Создаем таймер
            ServerTimer.schedule(new TimerTask() { // Определяем задачу
                @Override
                public void run() {
                    new AskServerTime(ServerTime).execute();
                }
            }, TimerDelay, MainActivity.getTimerTimeout());

        }

        private void startMainLogSync() {
            ServerMainLogTimer = new Timer(); // Создаем таймер
            ServerMainLogTimer.schedule(new TimerTask() { // Определяем задачу
                @Override
                public void run() {
                    if (race_status.isChecked())  {
                        new AskForMainLog(User_Monitor, this.toString()).execute();
                    }
                }
            }, TimerDelay, MainActivity.getMainLogTimeout());

        }

        public void constructStatusString() {
            loginInfo.setText(MainActivity.getLogin()+"/" + MainActivity.getLevel() + "/") ;
        }

    }

    }
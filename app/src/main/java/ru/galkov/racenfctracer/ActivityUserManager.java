package ru.galkov.racenfctracer;

import android.app.Activity;
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
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.common.AskForMainLog;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.GPS;
import ru.galkov.racenfctracer.common.SendUserNFCDiscovery;
import ru.galkov.racenfctracer.common.Utilites;

import static ru.galkov.racenfctracer.MainActivity.TimerDelay;
import static ru.galkov.racenfctracer.MainActivity.TimerTimeout;

// https://www.codexpedia.com/android/android-nfc-read-and-write-example/
    public class ActivityUserManager  extends Activity {


        private GPS GPS_System;
        private NfcAdapter nfcAdapter;
        private Tag myTag;
        private boolean writeMode;
        private Switch race_status;
        private Timer ServerTimer;
        private TextView User_Monitor;
        PendingIntent pendingIntent;
        IntentFilter writeTagFilters[];
        Context context;
        private Button back_button;
        private TextView NFC_ConfigurationLog;
        private ActivityUserManagereController AUMC;
        public static final String ERROR_DETECTED = "No NFC tag detected!";
        public static final String WRITE_SUCCESS = "Text written to the NFC tag successfully!";
        public static final String WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?";


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_manager);

            AUMC = new ActivityUserManagereController();

            GPS_System = new GPS(this,(TextView) findViewById(R.id.gpsPosition) );

            initClassVaribles();
            addlisteners();
            configureNFC();
            startTimeSync();

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
            WriteModeOff();

        }

        @Override
        public void onResume(){
            super.onResume();
            WriteModeOn();
        }

    private void startTimeSync() {
        ServerTimer = new Timer(); // Создаем таймер
        ServerTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                new AskServerTime(AUMC.ServerTime).execute();
                if (race_status.isChecked())  {
                    new AskForMainLog(AUMC.User_Monitor).execute();
                }
            }
        }, TimerDelay, TimerTimeout);

    }

        // ====================================================================================


    private void configureNFC() {
        readFromIntent(getIntent());
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
    }

    private void initClassVaribles(){
        back_button =           findViewById(R.id.back_button);
        NFC_ConfigurationLog =  findViewById(R.id.NFC_ConfigurationLog);
        race_status =           findViewById(R.id.race_status);
        User_Monitor =          findViewById(R.id.User_Monitor);


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Utilites.messager(this, "This device doesn't support NFC.");
            finish();
        }
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

// В лог записываем, TextView тут !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            NFC_ConfigurationLog.setText(R.string.NFC_To_Server + text);
            SendUserNFCDiscovery NFC = new SendUserNFCDiscovery(User_Monitor);
            NFC.setGPS_System(GPS_System);
            NFC.setMark(text);
            NFC.setUser("+79272006026");  // заглушка
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


    private void addlisteners() {


        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_OK, new Intent());
                finish();
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


    public class ActivityUserManagereController{

        public TextView User_Monitor;
        public TextView ServerTime;

        ActivityUserManagereController() {
            setDefaultView();
        }


        public void setDefaultView() {
            User_Monitor =  findViewById(R.id.User_Monitor);
            ServerTime =  findViewById(R.id.ServerTime);
        }

    }

    }
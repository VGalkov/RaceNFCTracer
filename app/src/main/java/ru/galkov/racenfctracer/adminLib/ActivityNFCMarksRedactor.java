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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.FaceControllers.ActivityFaceController;
import ru.galkov.racenfctracer.FaceControllers.HelpFaceController;
import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.R;
import ru.galkov.racenfctracer.common.AskMarksList;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.GPS;
import ru.galkov.racenfctracer.common.SendNewNFCMark;
import ru.galkov.racenfctracer.common.Utilites;

import static ru.galkov.racenfctracer.MainActivity.TimerDelay;

// https://www.codexpedia.com/android/android-nfc-read-and-write-example/
public class ActivityNFCMarksRedactor   extends AppCompatActivity {

    private NfcAdapter nfcAdapter;

    private Tag myTag;
    private boolean writeMode;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    private Context activity;
    public String markContent;
    private ActivityNFCMarksRedactorFaceController NFCRedactorController;
    private HelpFaceController HFC;
    private final MainActivity.writeMethod METHOD = MainActivity.writeMethod.Append;
    public static final String ERROR_DETECTED = "No NFC tag detected!";
    public static final String WRITE_SUCCESS = "Text written to the NFC tag successfully!";
    public static final String WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?";


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.redactor_nfc_activity_menu, menu);
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
                HFC.setHelpTopic(getString(R.string.redactorNFCHelp));
                HFC.start();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_nfc_marks_redactor);
        setActivity(this);

        ActivityNFCMarksRedactorFaceController NFCRedactorController = new ActivityNFCMarksRedactorFaceController();
        NFCRedactorController.start();

        addlisteners();
        configureNFC();

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
            Utilites.messager(this, "This device doesn't support NFC.");
            finish();
        }

        readFromIntent(getIntent());
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
    }



    private void addlisteners() {
        Button Save_nfcButton = findViewById(R.id.save_nfcButton);
        Save_nfcButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    if((myTag ==null) || ((NFCRedactorController.getNfS_Mark_Editor()).length()<1)) {
                        // Utilites.messager(context, ERROR_DETECTED);
                    } else {
                        write(NFCRedactorController.getNfS_Mark_Editor(), myTag);
                    }
                } catch (IOException e) {
                    Utilites.messager(getActivity(), WRITE_ERROR);
                    e.printStackTrace();
                } catch (FormatException e) {
                    Utilites.messager(getActivity(), WRITE_ERROR);
                    e.printStackTrace();
                }
            }
        });
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
        NFCRedactorController.stop();
        WriteModeOff();
    }

    @Override
    public void onResume(){
        super.onResume();
        NFCRedactorController = new ActivityNFCMarksRedactorFaceController();
        NFCRedactorController.start();
        WriteModeOn();
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

        markContent = "";
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0063;
        try {
            // Get the Text
            markContent = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Utilites.messager(this,"UnsupportedEncoding " + e.toString());
        }

        NFCRedactorController.setCurrentNFC_Label("NFC Content: " + markContent);
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


    public class  ActivityNFCMarksRedactorFaceController extends ActivityFaceController {
        public TextView ServerTime;
        private CheckBox masterMarkSw;
        private TextView gpsPosition;
        private GPS GPS_System;
        private Timer ServerTimer;
        private TextView CurrentNFC_Label;
        private Button back_button;
        private Button CommitButton;
        private TextView loginInfo;
        public TextView NFC_ConfigurationLog;
        private TextView NfS_Mark_Editor;
        private boolean isStarted = false;
        private RadioButton RadioButton1;
        private RadioButton RadioButton2;
        private RadioButton RadioButton3;
        private EditText Longtitude;
        private EditText Latitude;


        @Override
        protected void initViewObjects() {
            ServerTime = findViewById(R.id.ServerTime);
            CurrentNFC_Label =  findViewById(R.id.CurrentNFC_Label);
            NfS_Mark_Editor =   findViewById(R.id.NfS_Mark_Editor);
            back_button =       findViewById(R.id.back_button);
            CommitButton =      findViewById(R.id.CommitButton);
            NFC_ConfigurationLog = findViewById(R.id.NFC_ConfigurationLog);
            gpsPosition =                       findViewById(R.id.gpsPosition);
            loginInfo =             findViewById(R.id.loginInfo);
            //masterMarkSw =          findViewById(R.id.masterMarkSw);

        }

        @Override
        public boolean isStarted() {
            return isStarted;
        }


        public void setCurrentNFC_Label(String str) {
            CurrentNFC_Label.setText(str);
        }

        public String getNfS_Mark_Editor() {
            return NfS_Mark_Editor.getText().toString();
        }

        @Override
        protected void addListeners() {
            CommitButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    NFC_ConfigurationLog.append("Сохраняется метка -> \n ");
                    SendNewNFCMark NFC = new SendNewNFCMark(NFC_ConfigurationLog);
                    NFC.setMark(markContent);
                    NFC.setMethod(METHOD);
                    NFC.setGPS_System(GPS_System);
                    NFC.execute();
                }
            });


            back_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
            });
        }



        @Override
        protected void setDefaultFace() {
            new AskMarksList(NFC_ConfigurationLog).execute();
            constructStatusString();
        }

        @Override
        public void start() {
            startTimeSync();
            isStarted = true;
        }

        @Override
        public void stop() {
            ServerTimer.cancel();
            isStarted = false;
        }

        private void constructStatusString() {
            loginInfo.setText(MainActivity.getLogin()+"/" + MainActivity.getLevel() + "/") ;
            GPS_System = new GPS(getActivity(),gpsPosition);
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

    }

}
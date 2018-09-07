package ru.galkov.racenfctracer.adminLib;

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
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.R;
import ru.galkov.racenfctracer.common.AskMarksList;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.GPS;
import ru.galkov.racenfctracer.common.SendNewNFCMark;
import ru.galkov.racenfctracer.common.Utilites;

import static ru.galkov.racenfctracer.MainActivity.TimerTimeout;

// https://www.codexpedia.com/android/android-nfc-read-and-write-example/
public class ActivityNFCMarksRedactor  extends Activity {


    private GPS GPS_System;
    private NfcAdapter nfcAdapter;
    private Tag myTag;
    private boolean writeMode;
    private Timer ServerTimer;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    Context context;
    private Button save_nfcButton;
    private Button back_button;
    private Button CommitButton;
    private TextView NfS_Mark_Editor;
    private TextView CurrentNFC_Label;
    public TextView NFC_ConfigurationLog;
    private ActivityNFCMarksRedactorController ANFCMRC;

    public static final String ERROR_DETECTED = "No NFC tag detected!";
    public static final String WRITE_SUCCESS = "Text written to the NFC tag successfully!";
    public static final String WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_nfc_marks_redactor);

        ActivityNFCMarksRedactorController ANFCMRC = new ActivityNFCMarksRedactorController();

        GPS_System = new GPS(this,(TextView) findViewById(R.id.gpsPosition) );

        initClassVaribles();
        addlisteners();
        configureNFC();
        startTimeSync(); // опросчик серверных данных
    }

    private void startTimeSync() {
        // интервал - 60000 миллисекунд, 0 миллисекунд до первого запуска.

        ServerTimer = new Timer(); // Создаем таймер
        final Handler uiHandler = new Handler();

        ServerTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                new AskServerTime(ANFCMRC.ServerTime);
                new AskMarksList(ANFCMRC).execute();
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {                  String srt  = "";                 }
                });
            }
        }, 0L, TimerTimeout);

    }


    public class ActivityNFCMarksRedactorController{
        public TextView NFC_ConfigurationLog;
        public TextView ServerTime;

        ActivityNFCMarksRedactorController() {
            NFC_ConfigurationLog = findViewById(R.id.NFC_ConfigurationLog);
            ServerTime = findViewById(R.id.ServerTime);
        }
    }


    private void configureNFC() {
        readFromIntent(getIntent());
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
    }

    private void initClassVaribles(){
        CurrentNFC_Label =  findViewById(R.id.CurrentNFC_Label);
        NfS_Mark_Editor =   findViewById(R.id.NfS_Mark_Editor);
        save_nfcButton =    findViewById(R.id.save_nfcButton);
        back_button =       findViewById(R.id.back_button);
        CommitButton =      findViewById(R.id.CommitButton);
        NFC_ConfigurationLog = findViewById(R.id.NFC_ConfigurationLog);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Utilites.messager(this, "This device doesn't support NFC.");
            finish();
        }
    }

    private void addlisteners() {

        CommitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
// сохраняем метку на сервер
                NFC_ConfigurationLog.append(CurrentNFC_Label.getText()+"/n");
/*
                ServerConnection SC = new ServerConnection(new RecordNFCLababel());
                SC.calculate();
                String str;
                if (SC.isPassed()) {
                            str = R.string.NFC_MarkRemembered
                    }
                else {
                        str = R.string.NFC_MarkError
                }
                RegAsLabel.setText(str);
*/
            }
        });


        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_OK, new Intent());
                finish();
            }
        });

        save_nfcButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    if(myTag ==null) {
                        Utilites.messager(context, ERROR_DETECTED);
                    } else {
                        write(NfS_Mark_Editor.getText().toString(), myTag);
                        Utilites.messager(context, WRITE_SUCCESS);
                    }
                } catch (IOException e) {
                    Utilites.messager(context, WRITE_ERROR);
                    e.printStackTrace();
                } catch (FormatException e) {
                    Utilites.messager(context, WRITE_ERROR);
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
        WriteModeOff();
    }

    @Override
    public void onResume(){
        super.onResume();
        WriteModeOn();
    }

    // ====================================================================================

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
//        String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
        int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

        try {
            // Get the Text
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Utilites.messager(this,"UnsupportedEncoding " + e.toString());
        }

        CurrentNFC_Label.setText("NFC Content: " + text);
        SendNewNFCMark NFC = new SendNewNFCMark(ANFCMRC);
        NFC.setAdmin("+79272006036"); // заглушка
        NFC.setMark(text);
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

}
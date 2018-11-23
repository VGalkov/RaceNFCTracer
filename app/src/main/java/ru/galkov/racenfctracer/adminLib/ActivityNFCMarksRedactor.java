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
import android.widget.ImageButton;
import android.widget.TextView;
import com.yandex.mapkit.MapKitFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;
import ru.galkov.racenfctracer.FaceControllers.ActivityFaceController;
import ru.galkov.racenfctracer.FaceControllers.HelpFaceController;
import ru.galkov.racenfctracer.FaceControllers.MapViewController;
import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.R;
import ru.galkov.racenfctracer.common.AskMapPoints;
import ru.galkov.racenfctracer.common.AskMarksList;
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.SendNewNFCMark;

import static ru.galkov.racenfctracer.MainActivity.MV;
import static ru.galkov.racenfctracer.MainActivity.TimerDelay;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;
import static ru.galkov.racenfctracer.MainActivity.getTimerTimeout;
import static ru.galkov.racenfctracer.MainActivity.mapview;
import static ru.galkov.racenfctracer.MainActivity.setGPSMonitor;
import static ru.galkov.racenfctracer.common.Utilites.messager;

// https://www.codexpedia.com/android/android-nfc-read-and-write-example/
public class ActivityNFCMarksRedactor   extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private Tag myTag;
    private boolean writeMode;
    private PendingIntent pendingIntent;
    private IntentFilter writeTagFilters[];
    private Context activity;
    public String markContent;
    private ActivityNFCMarksRedactorFaceController NFCRedactorController;
    private final MainActivity.writeMethod METHOD = MainActivity.writeMethod.Append;
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
                HelpFaceController HFC = new HelpFaceController();
                HFC.setEkran((TextView) findViewById(R.id.ekran));
                HFC.setHelpTopic(getString(R.string.redactorNFCHelp));
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
            messager(this, "This device doesn't support NFC.");
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
                } catch (IOException | FormatException e) {
                    messager(getActivity(), WRITE_ERROR);
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
        try {
            mapview.onStop();
            MapKitFactory.getInstance().onStop();
        }
        catch (NullPointerException e) { e.printStackTrace();}
        NFCRedactorController.stop();
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
            messager(this,"UnsupportedEncoding " + e.toString());
        }

        NFCRedactorController.setCurrentNFC_Label("Считана метка NFC : " + markContent);
        NFCRedactorController.setNfS_Mark_Editor(markContent);
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
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }

    // **********************************Disable Write*******************************

    private void WriteModeOff(){
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }


    public class  ActivityNFCMarksRedactorFaceController extends ActivityFaceController {
        public TextView ServerTime, gpsPosition, CurrentNFC_Label, loginInfo,NFC_ConfigurationLog, NfS_Mark_Editor;
        private Timer ServerTimer;
        private ImageButton back_button;
        private Button CommitButton;
        private boolean isStarted = false;


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

        }

        @Override
        public boolean isStarted() {
            return isStarted;
        }


        private void setCurrentNFC_Label(String str) {
            CurrentNFC_Label.setText(str);
        }

        private void setNfS_Mark_Editor(String str1) {
            NfS_Mark_Editor.setText(str1);
        }

        private String getNfS_Mark_Editor() {
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
                    NFC.setGPS_System();
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
            setGPSMonitor(gpsPosition);
            isStarted = true;
        }

        @Override
        public void stop() {
            ServerTimer.cancel();
            isStarted = false;
        }

        @Override
        public void restart() {
            stop();
            start();
        }

        private void constructStatusString() {
            String str = getLogin() + ":" + getLevel();
            loginInfo.setText(str);
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
    }
}
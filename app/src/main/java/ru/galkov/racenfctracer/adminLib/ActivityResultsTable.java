package ru.galkov.racenfctracer.adminLib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.FaceControllers.ActivityFaceController;
import ru.galkov.racenfctracer.FaceControllers.HelpFaceController;
import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.R;
import ru.galkov.racenfctracer.common.AskForMainLog;
import ru.galkov.racenfctracer.common.AskResultsTable;
import ru.galkov.racenfctracer.common.AskServerTime;

import static ru.galkov.racenfctracer.MainActivity.TimerDelay;

public class ActivityResultsTable  extends AppCompatActivity {
    private ActivityResultsTableController ARTC;
    private HelpFaceController HFC;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_results_table);
        setContextVar(this);
        ARTC = new ActivityResultsTableController();
        ARTC.start();
    }

    public void setContextVar(Context context) {
        this.context = context;
    }

    public Context getContextVar() {
        return context;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ARTC.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ARTC.stop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.results_activity_menu, menu);
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
                HFC.setHelpTopic(getString(R.string.ResultsHelp));
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



// =======================================================

    public class ActivityResultsTableController extends ActivityFaceController {
        private ImageButton back_button;
        private Button downLoadResultsCVS;
        private Button downLoadLogCVS;
        private Button downLoadMarksCVS;
        public TextView userLogger;
        public TextView ServerTime;
        private TextView loginInfo;
        private Timer ServerTimer;
        private Timer MainLogAskTimer;
        private boolean isStarted = false;

        ActivityResultsTableController() {
            super();
        }


        @Override
        protected void initViewObjects() {
            back_button =      findViewById(R.id.back_button);
            userLogger =         findViewById(R.id.userLogger);
            downLoadResultsCVS = findViewById(R.id.downLoadResultsCVS);
            downLoadLogCVS =    findViewById(R.id.downLoadLogCVS);
            downLoadMarksCVS =    findViewById(R.id.downLoadMarksCVS);
            ServerTime =        findViewById(R.id.ServerTime);
            loginInfo =             findViewById(R.id.loginInfo);
        }


        public void stop() {
            ServerTimer.cancel();
            MainLogAskTimer.cancel();
            isStarted = false;
        }
        @Override
        public boolean isStarted() {
            return isStarted;
        }

        public void start() {
            startTimeSync();
            startMainLogSync();
            isStarted = true;
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
            MainLogAskTimer = new Timer(); // Создаем таймер
            MainLogAskTimer.schedule(new TimerTask() { // Определяем задачу
                @Override
                public void run() {
                    new AskForMainLog(userLogger, this.toString()).execute();
                }
            }, TimerDelay, MainActivity.getMainLogTimeout());

        }



        @Override
        protected void addListeners() {
            back_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
            });

            downLoadResultsCVS.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    new AskResultsTable(userLogger, MainActivity.fileType.Results, getContextVar()).execute();
                }
            });

            downLoadLogCVS.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    new AskResultsTable(userLogger, MainActivity.fileType.Log, getContextVar()).execute();
                }
            });

            downLoadMarksCVS.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    new AskResultsTable(userLogger, MainActivity.fileType.Marcs, getContextVar()).execute();
                }
            });
        }

        @Override
        protected void setDefaultFace() {
            constructStatusString();
        }

        private void constructStatusString() {
            loginInfo.setText(MainActivity.getLogin()+"/" + MainActivity.getLevel() + "/") ;
        }
    }
}

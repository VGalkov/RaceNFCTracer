package ru.galkov.racenfctracer.adminLib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.R;
import ru.galkov.racenfctracer.common.ActivityFaceController;
import ru.galkov.racenfctracer.common.AskForMainLog;
import ru.galkov.racenfctracer.common.AskResultsTable;
import ru.galkov.racenfctracer.common.AskServerTime;

import static ru.galkov.racenfctracer.MainActivity.TimerDelay;
import static ru.galkov.racenfctracer.MainActivity.TimerTimeout;

public class ActivityResultsTable  extends Activity {
// TODO переписать смысл http://qaru.site/questions/887264/android-how-to-download-file-in-android
    private ActivityResultsTableController ARTC;
    private AskResultsTable ART;
    private Timer ServerTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_results_table);
        ARTC = new ActivityResultsTableController();
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

    private void startTimeSync() {
        ServerTimer = new Timer(); // Создаем таймер
        ServerTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                new AskServerTime(ARTC.ServerTime).execute();
                new AskForMainLog(ARTC.ServerTime, this.toString()).execute();
            }
        }, TimerDelay, TimerTimeout);

    }
// =======================================================

    public class ActivityResultsTableController extends ActivityFaceController {
        private Button back_button;
        private Button downLoadResultsCVS;
        private Button downLoadLogCVS;
        private Button downLoadMarksCVS;
        public TextView userLogger;
        public TextView ServerTime;
        private TextView loginInfo;

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


        @Override
        protected void addListeners() {
            back_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
            });

            // TODO установить выдачу нужного типа файла и всунть его в ответ.
            downLoadResultsCVS.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    new AskResultsTable(ARTC.userLogger, MainActivity.fileType.Results).execute();
                }
            });

            downLoadLogCVS.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    new AskResultsTable(ARTC.userLogger, MainActivity.fileType.Log).execute();
                }
            });

            downLoadMarksCVS.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    new AskResultsTable(ARTC.userLogger, MainActivity.fileType.Marcs).execute();
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

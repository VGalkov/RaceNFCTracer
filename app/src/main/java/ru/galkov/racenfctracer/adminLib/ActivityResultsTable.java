package ru.galkov.racenfctracer.adminLib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.R;
import ru.galkov.racenfctracer.common.AskResultsTable;
import ru.galkov.racenfctracer.common.AskServerTime;

import static ru.galkov.racenfctracer.MainActivity.TimerDelay;
import static ru.galkov.racenfctracer.MainActivity.TimerTimeout;

public class ActivityResultsTable  extends Activity {

    private ActivityResultsTableController ARTC;
    private AskResultsTable ART;
    private Timer ServerTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_results_table);
        ARTC = new ActivityResultsTableController();
        ARTC.setDefaultView();
        new AskResultsTable(ARTC).execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


// =======================================================

    public class ActivityResultsTableController{
        private Button back_button;
        private Button DownLoadResults;
        public TextView userLogger;
        public TextView ServerTime;

        ActivityResultsTableController() {
            setDefaultView();
        }

        public void setDefaultView() {
            initViewObjects();
            addListeners();
            setDefaultFace();
            startTimeSync();
        }

        private void initViewObjects() {
            back_button =      findViewById(R.id.back_button);
            userLogger =       findViewById(R.id.userLogger);
            DownLoadResults = findViewById(R.id.DownLoadResults);
            ServerTime = findViewById(R.id.ServerTime);

        }

        private void addListeners() {
            back_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
            });

            DownLoadResults.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
// запрос таблицы в текстовом виде.
                }
            });
        }


        private void startTimeSync() {

            ServerTimer = new Timer(); // Создаем таймер
            ServerTimer.schedule(new TimerTask() { // Определяем задачу
                @Override
                public void run() {      new AskServerTime(ARTC.ServerTime);      }
            }, TimerDelay, TimerTimeout);

        }

        // ============================
        private void setDefaultFace() {
// запрос лога регистраций метка = user

        }
    }
}

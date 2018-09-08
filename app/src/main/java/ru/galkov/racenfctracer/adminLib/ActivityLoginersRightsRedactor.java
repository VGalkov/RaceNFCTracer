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
import ru.galkov.racenfctracer.common.AskServerTime;
import ru.galkov.racenfctracer.common.AskUserTable;

import static ru.galkov.racenfctracer.MainActivity.TimerDelay;
import static ru.galkov.racenfctracer.MainActivity.TimerTimeout;

//  https://startandroid.ru/ru/uroki/vse-uroki-spiskom/115-urok-56-spinner-vypadajuschij-spisok.html
public class ActivityLoginersRightsRedactor  extends Activity {

    private ActivityLoginersRightsRedactorController ALRRC;
    private AskUserTable AUT;
//    private Timer ServerTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_loginers_rights_redactor);
        ALRRC = new ActivityLoginersRightsRedactorController();

        startTimeSync(); // или в onResume?
        new AskUserTable(ALRRC.userLogger).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void startTimeSync() {
//        ServerTimer = new Timer(); // Создаем таймер
        new Timer().schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {new AskServerTime(ALRRC.ServerTime).execute();}
        }, TimerDelay, TimerTimeout);
    }

// ==========================================================

    public class ActivityLoginersRightsRedactorController{
        private Button back_button;
        public TextView userLogger;
        public TextView ServerTime;

        ActivityLoginersRightsRedactorController() {
            setDefaultView();
        }

        public void setDefaultView() {
            initViewObjects();
            addListeners();
            setDefaultFace();
        }

        private void initViewObjects() {
            back_button =        findViewById(R.id.back_button);
            userLogger =         findViewById(R.id.userLogger);
            ServerTime =         findViewById(R.id.ServerTime);
        }

        private void addListeners() {
            back_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
            });
        }


        // ============================
        private void setDefaultFace() {
// считать абонентов из базы и нарисовать структуры со спиннером.
        }
    }
}

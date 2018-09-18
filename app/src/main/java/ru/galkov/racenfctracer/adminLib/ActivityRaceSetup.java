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
        import ru.galkov.racenfctracer.common.GPS;

        import static ru.galkov.racenfctracer.MainActivity.TimerDelay;
        import static ru.galkov.racenfctracer.MainActivity.TimerTimeout;

public class ActivityRaceSetup  extends Activity {

    private ActivityRaceSetupController ARSC;
    private GPS GPS_System;
    private Timer ServerTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_setup);
        ARSC = new ActivityRaceSetupController();
        GPS_System = new GPS(this,(TextView) findViewById(R.id.gpsPosition) );
        startTimeSync();
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
        ServerTimer = new Timer(); // Создаем таймер
        ServerTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                new AskServerTime(ARSC.ServerTime).execute();
            }
        }, TimerDelay, TimerTimeout);

    }
// =======================================================


    class ActivityRaceSetupController {

        public TextView ServerTime;
        private Button back_button;

        public void ActivityRaceSetupController() {
            initViewObjects();
            addListeners();
        }

        void initViewObjects(){
            ServerTime =        findViewById(R.id.ServerTime);
            back_button =       findViewById(R.id.back_button);
            // TODO 2 связанных выпадающих списка стартов и рейсов.
        }

        void addListeners(){
            back_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
            });
        }
    }
}

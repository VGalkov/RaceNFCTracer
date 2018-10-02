package ru.galkov.racenfctracer.FaceControllers;

import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.common.AskForMainLog;

import static ru.galkov.racenfctracer.MainActivity.TimerDelay;

public class MainLogController extends ActivityFaceController {

    private Timer ServerMainLogTimer;
    private TextView User_Monitor;
    private String caller = "Unknown";
    private boolean isStarted = false;

    public void setCaller(String caller) {
        this.caller = caller;
    }

    @Override
    protected void initViewObjects() {

    }

    @Override
    protected void addListeners() {

    }

    @Override
    protected void setDefaultFace() {

    }

    @Override
    public void start() {
        startMainLogSync();
        isStarted = true;
    }

    @Override
    public void stop() {
        ServerMainLogTimer.cancel();
        isStarted = false;
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }

    public void setEkran(TextView ekran) {
        this.User_Monitor = ekran;
    }


    private void startMainLogSync() {
        ServerMainLogTimer = new Timer(); // Создаем таймер
        ServerMainLogTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                new AskForMainLog(User_Monitor, caller).execute();
            }
        }, TimerDelay, MainActivity.getMainLogTimeout());

    }

}
package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;

import ru.galkov.racenfctracer.ActivityGuestManager;
import ru.galkov.racenfctracer.MainActivity;

// отрефакториить и собрать в 3 в 1
public class AskMainLogGuest extends AsyncTask<String, Void, String> {


    private final String SERVER_URL = MainActivity.SERVER_URL + "/ActivityGuestManager/";
    private ActivityGuestManager.ActivityGuestManagereController AGMC;


    public AskMainLogGuest(ActivityGuestManager.ActivityGuestManagereController AGMC1) {
        AGMC = AGMC1;

    }

    @Override
    protected String doInBackground(String... strings) {

        return Utilites.getMainLogJSON_ZAGLUSHKA();
    }

    @Override
    protected void onPostExecute(String result) {
    //выброс всех пользователей  в обзорник Activity
        if (result == null) { AGMC.messager("Странное значеие результата!"); }
            else {     AGMC.UserLogger.setText(result); }
    }





}

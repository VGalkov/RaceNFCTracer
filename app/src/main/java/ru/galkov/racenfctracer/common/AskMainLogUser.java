package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;

import ru.galkov.racenfctracer.ActivityUserManager;
import ru.galkov.racenfctracer.MainActivity;


// отрефакториить и собрать в 3 в 1
public class AskMainLogUser extends AsyncTask<String, Void, String> {


    private final String SERVER_URL = MainActivity.SERVER_URL + "/ActivityUserManager/";
    private ActivityUserManager.ActivityUserManagereController AUMC;


    public AskMainLogUser (ActivityUserManager.ActivityUserManagereController AUMC1) {
        AUMC = AUMC1;

    }

    @Override
    protected String doInBackground(String... strings) {

        return Utilites.getMainLogJSON_ZAGLUSHKA();
    }

    @Override
    protected void onPostExecute(String result) {
//выброс всех пользователей.
        AUMC.User_Monitor.setText(result);
    }



}

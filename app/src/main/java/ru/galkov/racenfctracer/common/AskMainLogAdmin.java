package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;

import ru.galkov.racenfctracer.ActivityAdminManager;
import ru.galkov.racenfctracer.MainActivity;

// отрефакториить и собрать в 3 в 1
public class AskMainLogAdmin extends AsyncTask<String, Void, String> {


    private final String SERVER_URL = MainActivity.SERVER_URL + "/ActivityGuestManager/";

    private ActivityAdminManager.ActivityAdminManagerController AAMC;


    public AskMainLogAdmin(ActivityAdminManager.ActivityAdminManagerController AAMC1) {
        AAMC = AAMC1;

    }

    @Override
    protected String doInBackground(String... strings) {

        return Utilites.getMainLogJSON_ZAGLUSHKA();
    }

    @Override
    protected void onPostExecute(String result) {
//выброс всех пользователей.
        AAMC.userLogger.setText(result);
    }





}

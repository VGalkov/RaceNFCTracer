package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;

import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.adminLib.ActivityResultsTable;

public class AskResultsTable extends AsyncTask<String, Void, String> {


    private final String SERVER_URL = MainActivity.SERVER_URL + "/ActivityGuestManager/";
    private ActivityResultsTable.ActivityResultsTableController ARTC;


    public AskResultsTable(ActivityResultsTable.ActivityResultsTableController ARTC1) {
        ARTC = ARTC1;

    }

    @Override
    protected String doInBackground(String... strings) {

        return Utilites.getMainLogJSON_ZAGLUSHKA();
    }

    @Override
    protected void onPostExecute(String result) {
//выброс всех пользователей.
        ARTC.userLogger.setText(result);
    }





}

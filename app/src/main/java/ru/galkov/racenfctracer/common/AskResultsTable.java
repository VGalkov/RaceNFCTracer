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
// запрашиваем всю таблицу результатов участников.
        return Utilites.getUsersResultsJSON_ZAGLUSHKA();
    }

    @Override
    protected void onPostExecute(String result) {
// отображаем таблицу результатов пользователей ... кстати не понятно поака как ..
        ARTC.userLogger.setText(result);
    }





}

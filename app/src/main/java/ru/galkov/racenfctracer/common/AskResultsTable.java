package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

public class AskResultsTable extends AsyncTask<String, Void, String> {


    private final String SERVER_URL = MainActivity.SERVER_URL + "/ActivityGuestManager/";
    private TextView userLogger;


    public AskResultsTable(TextView userLogger1) {
        this.userLogger = userLogger1;

    }

    @Override
    protected String doInBackground(String... strings) {
// запрашиваем всю таблицу результатов участников.
        return Utilites.getUsersResultsJSON_ZAGLUSHKA();
    }

    @Override
    protected void onPostExecute(String result) {
// отображаем таблицу результатов пользователей ... кстати не понятно поака как ..
        // userLogger.setText(result);
        try {
            JSONArray arr = new JSONArray(result);
            for(int i = 0 ; i< arr.length() ; i++) {
                JSONObject obj = arr.getJSONObject(i);
                String str = obj.getString("user") + ": " + obj.getString("mark") + "(" + obj.getString("date") + ")" +"["+obj.getString("gpsX")+", "+obj.getString("gpsY")+", "+obj.getString("gpsZ")+"]\n";
                userLogger.append(str);
            }
        } catch (JSONException e) {	e.printStackTrace();}

    }





}

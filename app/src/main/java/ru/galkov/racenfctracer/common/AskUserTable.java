package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Iterator;

import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.adminLib.ActivityLoginersRightsRedactor;

public class AskUserTable extends AsyncTask<String, Void, String> {

    private ActivityLoginersRightsRedactor.ActivityLoginersRightsRedactorController ALRRC;
    private final String SERVER_URL = MainActivity.SERVER_URL + "/ActivityGuestManager/";
    private TextView userLogger;



    public AskUserTable(TextView userLogger1) {
        userLogger = userLogger1;
    }

    @Override
    protected String doInBackground(String... strings) {
// запрос полной таблицы пользователей для пользовательского интерфейса раздачи уровня доступа
        return Utilites.getUserListJSON_ZAGLUSHKA();
    }

    @Override
    protected void onPostExecute(String result) {
// обработка списка полученных пользователей (создание объекта на каждого)
        try {
            JSONArray arr = new JSONArray(result);
            for(int i = 0 ; i< arr.length() ; i++) {
                JSONObject obj = arr.getJSONObject(i);
                userLogger.setText("Login:" + obj.get("user") + "/" + obj.get("level") + "\n");
            }
        } catch (JSONException e) {	e.printStackTrace();}
    }



    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

}

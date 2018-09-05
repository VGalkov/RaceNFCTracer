package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Iterator;

import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.adminLib.ActivityLoginersRightsRedactor;

public class AskUserTable extends AsyncTask<String, Void, String> {

    private ActivityLoginersRightsRedactor.ActivityLoginersRightsRedactorController ALRRC;
    private final String SERVER_URL = MainActivity.SERVER_URL + "/ActivityGuestManager/";



    public AskUserTable(ActivityLoginersRightsRedactor.ActivityLoginersRightsRedactorController ALRRC1) {
        ALRRC = ALRRC1;

    }

    @Override
    protected String doInBackground(String... strings) {
// запрос полной таблицы пользователей для пользовательского интерфейса раздачи уровня доступа
        return Utilites.getUserListJSON_ZAGLUSHKA();
    }

    @Override
    protected void onPostExecute(String result) {
// обработка списка полученных пользователей (создание объекта на каждого)

        ALRRC.userLogger.setText(result);
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

package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.adminLib.ActivityResultsTable;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskServerTime extends AsyncTask<String, Void, String> {

    private final String SERVER_URL = MainActivity.SERVER_URL + "/ActivityGuestManager/";
    private ActivityResultsTable.ActivityResultsTableController ARTC;
    private TextView TimeLabel;
    private String login;

    public void setLogin(String login1) {
        this.login = login1;
    }

    public AskServerTime(TextView TimeLabel1) {
        TimeLabel = TimeLabel1;
    }


    @Override
    protected String doInBackground(String... strings) {
// запрашиваем всю таблицу результатов участников.

        JSONObject SendThis = new JSONObject();
        try {
                SendThis.put("who",login);
                SendThis.put("key",KEY);
        } catch (JSONException e) {	e.printStackTrace();}

        // отправка запроса тут

        return Utilites.getServerTimeJSON_ZAGLUSHKA(SendThis.toString());
    }

    @Override
    protected void onPostExecute(String result) {

        try {
            JSONObject JOAnswer = new JSONObject(result);
            if (Utilites.chkKey((String) JOAnswer.get("key"))) {
                if(JOAnswer.get("Status").equals("TRUE")) {  // TRUE|FALSE
// пишем, что ок. и помещаем регистрационные данные в монитор в админа c датой с сервера.
                    String regRecord = JOAnswer.get("date").toString();
                    TimeLabel.setText(regRecord);
                }
                else {
                    TimeLabel.setText(JOAnswer.get("Error").toString());
                }

            }
        }
        catch (JSONException e) {	e.printStackTrace();}

    }





}
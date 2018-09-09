package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;


public class SendUserLevel extends AsyncTask<String, Void, String> {

    private final String SERVER_URL = MainActivity.SERVER_URL + "/MainActivity/";
    private String level;
    private String login;
    private TextView userLogger;


    public SendUserLevel(TextView userLogger1) {
        userLogger = userLogger1;
    }

    public void setLevel(String level1)  {
        level = level1;
    }

    public void setLogin(String login1)  {
        // отсекаем ненужное. но если в логине будет скобка реально - это косяк. но логин = номеру телефона.12  цифр и +

//        login = login1.substring(0, login1.indexOf('('));
        login = login1;
    }

    protected void onPreExecute(){
        // проверку на
    }


    protected String doInBackground(String... arg0) {

        JSONObject toServer = new JSONObject();
        try {
            toServer.put("login",login);    // TRUE|FALSE
            toServer.put("key",KEY);
            toServer.put("level",level);
        } catch (JSONException e) {	e.printStackTrace();}

        return Utilites.getONEUserLogin_ZAGLUSHKA(toServer.toString());
    }

    @Override
    protected void onPostExecute(String result) {

        MainActivity.registrationLevel REGLEVEL = MainActivity.registrationLevel.Guest;

        //        String str = JOAnswer.get("login").toString();
//        String strLogin = str.substring(0, str.indexOf('('));

        try {
            JSONObject JOAnswer = new JSONObject(result);
            userLogger.setText("отработало! =>" + JOAnswer.get("login") + "/" + JOAnswer.get("level"));
        }
        catch (JSONException e) {	e.printStackTrace();}


    }



}

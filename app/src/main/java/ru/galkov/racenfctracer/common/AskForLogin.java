package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskForLogin extends AsyncTask<String, Void, String> {

    private final String SERVER_URL = MainActivity.SERVER_URL + "/MainActivity/";
    private int HTTP_TIMEOUT = 15000; // milliseconds
    private TextView login;
    private TextView password;
    private TextView RegAsLabel;
    private MainActivity.registrationLevel level;
    private MainActivity.registrationLevel REGLEVEL;
    private MainActivity.MainActivityFaceController MAFC;
    private Context context;


    public AskForLogin(MainActivity.MainActivityFaceController MAFC1) {
        MAFC = MAFC1;

    }



    @Override
    protected void onPreExecute(){}

    public void setParentActivity(Context context1) {
        context = context1;
    }

    public void setLogin(TextView login1) {
        this.login = login1;
    }

    public void setPassword(TextView password1) {
        this.password = password1;
    }

    public void setLevel(MainActivity.registrationLevel level1) {
        this.level = level1;
    }


    // возвращает json с сервера  {level":"Admin","login":"TRUE|FALSE","key":"sgfsdfg"}
    @Override
    protected String doInBackground(String... arg0) {

        JSONObject toServer = new JSONObject();
        try {
                toServer.put("login",login.getText());
                toServer.put("password", password.getText());
                toServer.put("level", level.toString());
                toServer.put("key",KEY);
        } catch (JSONException e) {	e.printStackTrace();}

        return Utilites.getUserLogin_ZAGLUSHKA(toServer.toString());
    }

    @Override
    protected void onPostExecute(String result) {

        REGLEVEL = MainActivity.registrationLevel.Guest;
        try {
            JSONObject JOAnswer = new JSONObject(result);
            if (Utilites.chkKey((String) JOAnswer.get("key"))) {
                String LoginRes = (String) JOAnswer.get("login");
                if (LoginRes.equals("TRUE")) { // состояние проверки логина-пароля
                    if ((JOAnswer.get("level")).equals((MainActivity.registrationLevel.Admin).toString())) {
                        REGLEVEL = MainActivity.registrationLevel.Admin;
                    }
                    else if ((JOAnswer.get("level")).equals((MainActivity.registrationLevel.User).toString())) {
                        REGLEVEL = MainActivity.registrationLevel.User;
                    }
                }// сменить на Utility
                else { Utilites.messager(context,"авторизация пройдена на уровне Guest"); }
            }
            else { Utilites.messager(context,"сбой протокола шифрования или всего запроса!"); }
        }
        catch (JSONException e) {	e.printStackTrace();}

        MAFC.REGLEVEL = REGLEVEL;
        MAFC.RegAsLabel.setText(REGLEVEL.toString());
        MAFC.setRegistredFace();
    }



}

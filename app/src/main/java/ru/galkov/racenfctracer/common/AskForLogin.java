package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

public class AskForLogin extends AsyncTask<String, Void, String> {

    private final String SERVER_URL = MainActivity.SERVER_URL + "/MainActivity/";
    private int HTTP_TIMEOUT = 15000; // milliseconds
    private TextView login;
    private TextView password;
    private TextView RegAsLabel;
    private MainActivity.registrationLevel level;
    private MainActivity.registrationLevel REGLEVEL;
    private MainActivity.MainActivityFaceController MAFC;


    public AskForLogin(MainActivity.MainActivityFaceController MAFC1) {
        MAFC = MAFC1;

    }

    protected void onPreExecute(){}

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
    protected String doInBackground(String... arg0) {
           return Utilites.getUserLogin_ZAGLUSHKA();
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
                }
                else { MAFC.messager("авторизация пройдена на уровне Guest"); }
            }
            else { MAFC.messager("сбой протокола шифрования или всего запроса!"); }
        }
        catch (JSONException e) {	e.printStackTrace();}

        MAFC.REGLEVEL = REGLEVEL;
        MAFC.RegAsLabel.setText(REGLEVEL.toString());
        MAFC.setRegistredFace();
    }



}

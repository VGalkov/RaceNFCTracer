package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class SendNewLoginLevel extends AsyncTask<String, Void, String> {

    private TextView login;
    private String admin;

    private MainActivity.registrationLevel level;
    private String SERVER_URL = MainActivity.SERVER_URL;


    public void setLogin(TextView login1) {
        this.login = login1;
    }
// должно неявно устанавливаться из имени заеганого
    public void setAdmin(String admin1) {
        this.admin = admin1;
    }

    public void setLevel(MainActivity.registrationLevel level1) {
        this.level = level1;
    }

    @Override
    protected String doInBackground(String... strings) {
        // пишем запрос серверу на смену логина тут и обрабатываем его ответ

        JSONObject SendThis = new JSONObject();
        try {
                SendThis.put("login",login.getText());
                SendThis.put("admin",admin);
                SendThis.put("level", level);
                SendThis.put("key",KEY);
        } catch (JSONException e) {	e.printStackTrace();}

        // коннектимся и шлём это SendThis
        return Utilites.getLoginLevelChangingJSON_ZAGLUSHKA(SendThis);

    }

    @Override
    protected void onPostExecute(String result) {
     // обработка ответа о смене админом уровня доступа логину.
        try {
            JSONObject JOAnswer = new JSONObject(result);
            if (Utilites.chkKey((String) JOAnswer.get("key"))) {
                if(JOAnswer.get("login").equals("TRUE")) {  // TRUE|FALSE
// пишем, что ок.
                }
                else {
                    /// JOAnswer.get("Msg") = ошибка сервера тут
                }

            }
        }
        catch (JSONException e) {	e.printStackTrace();}
    }

}

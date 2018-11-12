package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import ru.galkov.racenfctracer.MainActivity;
import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskForLogin extends AsyncTask<String, Void, String> {


    private final String ASKER = "AskForLogin";
    private String login;
    private String password;
    Context context;
    private MainActivity.registrationLevel level;
    private MainActivity.registrationLevel REGLEVEL;
    private MainActivity.MainActivityFaceController MAFC;
    private JSONObject outBoundJSON;

    public AskForLogin(MainActivity.MainActivityFaceController MAFC1) {
        MAFC = MAFC1;
    }

    @Override
    protected void onPreExecute(){
        boolean tr = true;
        if (login.length()!=MainActivity.LoginLength)  tr = false;
        if (password.length()<MainActivity.PasswordLength) tr = false;

// BACKDORE без сервера.
            if (tr)       makeOutBoundJSON();
            else Utilites.messager(context, "Не смог подготовить запрос на сервер");
        }

    private  void makeOutBoundJSON(){
        try {
                outBoundJSON = new JSONObject();
                outBoundJSON.put(MainActivity.fieldsJSON.asker.toString(),ASKER);
                outBoundJSON.put(MainActivity.fieldsJSON.login.toString(),login);
                outBoundJSON.put(MainActivity.fieldsJSON.password.toString(),password);
                outBoundJSON.put(MainActivity.fieldsJSON.level.toString(),level);
                outBoundJSON.put(MainActivity.fieldsJSON.key.toString(),KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setParentActivity(Context context1) {
        context = context1;
    }

    public void setLogin(String login1) {
        this.login = login1;
    }

    public void setPassword(String password1) {
        this.password = password1;
    }

    public void setLevel(MainActivity.registrationLevel level1) {
        this.level = level1;
    }



    @Override
    protected String doInBackground(String... arg0) {

        HttpProcessor HP = new HttpProcessor();
        HP.setASKER(ASKER);
        HP.setJson(outBoundJSON);
        return HP.execute();
    }





    @Override
    protected void onPostExecute(String result) {
        // {"level":"Guest","asker":"AskForLogin","login":"TRUE|FALSE","key":"galkovvladimirandreevich"}
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

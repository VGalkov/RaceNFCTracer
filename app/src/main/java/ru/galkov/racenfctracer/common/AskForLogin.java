package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskForLogin extends AsyncTask<String, Void, String> {


    private final String ASKER = "AskForLogin";
    private String login;
    private String password;
    private Context context;

    private TextView RegAsLabel;
    private MainActivity.registrationLevel level;
    private MainActivity.registrationLevel REGLEVEL;
    private MainActivity.MainActivityFaceController MAFC;

    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;

//    https://ru.stackoverflow.com/questions/552262/android-%D0%BA%D0%B0%D0%BA-%D1%81%D0%B4%D0%B5%D0%BB%D0%B0%D1%82%D1%8C-get-%D0%B7%D0%B0%D0%BF%D1%80%D0%BE%D1%81
    public AskForLogin(MainActivity.MainActivityFaceController MAFC1) {
        MAFC = MAFC1;

    }



    @Override
    protected void onPreExecute(){
        boolean tr = true;
        if (login.length()!=12)  tr = false;
        if (password.length()<5) tr = false;

// проверку забитого уровня можно не делать.

            if (tr) makeOutBoundJSON();
            else Utilites.messager(context, "Не смог подготовить запрос на сервер");
        }

    private  void makeOutBoundJSON(){
//        {"asker":"AskForLogin","level":"Guest","key":"galkovvladimirandreevich","login":"+79272006026","password":"aaazzz"}
        try {
                outBoundJSON = new JSONObject();
                outBoundJSON.put(f.asker.toString(),ASKER);
                outBoundJSON.put(f.login.toString(),login);
                outBoundJSON.put(f.password.toString(),password);
                outBoundJSON.put(f.level.toString(),level);
                outBoundJSON.put(f.key.toString(),KEY);

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

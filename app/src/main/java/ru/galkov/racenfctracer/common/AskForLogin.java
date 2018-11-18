package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.MainActivity.fieldsJSON;
import ru.galkov.racenfctracer.MainActivity.registrationLevel;
import ru.galkov.racenfctracer.MainActivity.trigger;

import static ru.galkov.racenfctracer.MainActivity.KEY;
import static ru.galkov.racenfctracer.MainActivity.LoginLength;
import static ru.galkov.racenfctracer.MainActivity.PasswordLength;
import static ru.galkov.racenfctracer.common.Utilites.chkKey;
import static ru.galkov.racenfctracer.common.Utilites.messager;

public class AskForLogin extends AsyncTask<String, Void, String> {

    private final String ASKER = "AskForLogin";
    private String login, password;
    private Context context;
    private MainActivity.registrationLevel level;
    private registrationLevel REGLEVEL;
    private MainActivity.MainActivityFaceController MAFC;
    private JSONObject outBoundJSON;

    public AskForLogin(MainActivity.MainActivityFaceController MAFC1) {
        MAFC = MAFC1;
    }

    @Override
    protected void onPreExecute(){
        boolean tr = true;
        if (login.length()!=LoginLength)  tr = false;
        if (password.length()<PasswordLength) tr = false;

            if (tr)       makeOutBoundJSON();
            else messager(context, "Не смог подготовить запрос на сервер");
        }

    private  void makeOutBoundJSON(){
        try {
                outBoundJSON = new JSONObject();
                outBoundJSON.put(fieldsJSON.asker.toString(),ASKER);
                outBoundJSON.put(fieldsJSON.login.toString(),login);
                outBoundJSON.put(fieldsJSON.password.toString(),password);
                outBoundJSON.put(fieldsJSON.level.toString(),level);
                outBoundJSON.put(fieldsJSON.key.toString(),KEY);
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

    public void setLevel(registrationLevel level1) {
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
        REGLEVEL = registrationLevel.Guest;
        try {
            JSONObject JOAnswer = new JSONObject(result);
            if (chkKey(JOAnswer.getString(fieldsJSON.key.toString()))) {
                String LoginRes = JOAnswer.getString(fieldsJSON.login.toString());
                if (LoginRes.equals(trigger.TRUE.toString())) {
                    if ((JOAnswer.getString(fieldsJSON.level.toString())).equals((registrationLevel.Admin).toString())) {
                        REGLEVEL = registrationLevel.Admin;
                    }
                    else if ((JOAnswer.get(fieldsJSON.level.toString())).equals((registrationLevel.User).toString())) {
                        REGLEVEL = registrationLevel.User;
                    }
                }// сменить на Utility
                else { messager(context,"авторизация пройдена на уровне Guest"); }
            }
            else { messager(context,"сбой протокола шифрования или всего запроса!"); }
        }
        catch (JSONException e) {	e.printStackTrace();}

        MAFC.REGLEVEL = REGLEVEL;
        MAFC.RegAsLabel.setText(REGLEVEL.toString());
        MAFC.setRegistredFace();
    }


    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
        onCancelled();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        messager(context,"сбой при передаче данных");
    }

}

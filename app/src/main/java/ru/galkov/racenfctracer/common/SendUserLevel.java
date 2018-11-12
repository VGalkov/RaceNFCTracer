package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;


public class SendUserLevel extends AsyncTask<String, Void, String> {

    private final String ASKER = "SendUserLevel";
    private JSONObject outBoundJSON;
    private String level;
    private String login;
    private TextView userLogger;
    private String masterMark;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;


    public SendUserLevel(TextView userLogger1) {
        userLogger = userLogger1;
    }

    public void setLevel(String level1)  {
        level = level1;
    }

    public void setLogin(String login1)  {
        login = login1;
    }

    @Override
    protected void onPreExecute(){
        makeOutBoundJSON();
    }


    protected String doInBackground(String... arg0) {

        HttpProcessor HP = new HttpProcessor();
        HP.setASKER(ASKER);
        HP.setJson(outBoundJSON);
        return HP.execute();
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject JOAnswer = new JSONObject(result);
            userLogger.setText("отработало! =>" + JOAnswer.get("login") + "/" + JOAnswer.get("level"));
        }
        catch (JSONException e) {	e.printStackTrace();}


    }

    public  void setMasterMark(String masterMark1) {
        this.masterMark = masterMark1;
    }

    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;

    }

    private  void makeOutBoundJSON(){
        // {"asker":"SendNewLoginLevel","level" ="User","key":"galkovvladimirandreevich","login":"+79000000111"}

        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(MainActivity.fieldsJSON.asker.toString(),ASKER);
            outBoundJSON.put(MainActivity.fieldsJSON.level.toString(),level);
            outBoundJSON.put(MainActivity.fieldsJSON.login.toString(),login);
            outBoundJSON.put(MainActivity.fieldsJSON.key.toString(),KEY);
            outBoundJSON.put(MainActivity.fieldsJSON.master_mark_label.toString(),masterMark);
            outBoundJSON.put(MainActivity.fieldsJSON.exec_login.toString(),MainActivity.getLogin());
            outBoundJSON.put(MainActivity.fieldsJSON.exec_level.toString(),MainActivity.getLevel());
            outBoundJSON.put(MainActivity.fieldsJSON.longitude.toString(), MainActivity.getLongitude());
            outBoundJSON.put(MainActivity.fieldsJSON.altitude.toString(), MainActivity.getAltitude());
            outBoundJSON.put(MainActivity.fieldsJSON.latitude.toString(), MainActivity.getLatitude());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

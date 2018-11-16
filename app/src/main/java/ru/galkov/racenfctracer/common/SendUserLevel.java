package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;
import org.json.*;
import ru.galkov.racenfctracer.MainActivity;
import static ru.galkov.racenfctracer.MainActivity.KEY;
import static ru.galkov.racenfctracer.MainActivity.getAltitude;
import static ru.galkov.racenfctracer.MainActivity.getLatitude;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;
import static ru.galkov.racenfctracer.MainActivity.getLongitude;

import ru.galkov.racenfctracer.MainActivity.fieldsJSON;

public class SendUserLevel extends AsyncTask<String, Void, String> {

    private final String ASKER = "SendUserLevel";
    private JSONObject outBoundJSON;
    private String level, login, masterMark;
    private TextView userLogger;


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


    private  void makeOutBoundJSON(){
        // {"asker":"SendNewLoginLevel","level" ="User","key":"galkovvladimirandreevich","login":"+79000000111"}

        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(fieldsJSON.asker.toString(),ASKER);
            outBoundJSON.put(fieldsJSON.level.toString(),level);
            outBoundJSON.put(fieldsJSON.login.toString(),login);
            outBoundJSON.put(fieldsJSON.key.toString(),KEY);
            outBoundJSON.put(fieldsJSON.master_mark_label.toString(),masterMark);
            outBoundJSON.put(fieldsJSON.exec_login.toString(),getLogin());
            outBoundJSON.put(fieldsJSON.exec_level.toString(), getLevel());
            outBoundJSON.put(fieldsJSON.longitude.toString(), getLongitude());
            outBoundJSON.put(fieldsJSON.altitude.toString(), getAltitude());
            outBoundJSON.put(fieldsJSON.latitude.toString(), getLatitude());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

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
    private MainActivity.fieldsJSON f;
    private String level;
    private String login;
    private TextView userLogger;
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
// TODO rewrite
        // {"level":"Admin","asker":"SendNewLoginLevel","login":"TRUE","key":"galkovvladimirandreevich"}
        MainActivity.registrationLevel REGLEVEL = MainActivity.registrationLevel.Guest;

        //        String str = JOAnswer.get("login").toString();
//        String strLogin = str.substring(0, str.indexOf('('));

        try {
            JSONObject JOAnswer = new JSONObject(result);
            userLogger.setText("отработало! =>" + JOAnswer.get("login") + "/" + JOAnswer.get("level"));
        }
        catch (JSONException e) {	e.printStackTrace();}


    }

    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;

    }

    private  void makeOutBoundJSON(){
        // {"asker":"SendNewLoginLevel","level" ="User","key":"galkovvladimirandreevich","login":"+79000000111"}

        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);
            outBoundJSON.put(f.level.toString(),level);
            outBoundJSON.put(f.login.toString(),login);
            outBoundJSON.put(f.key.toString(),KEY);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;
import org.json.*;
import ru.galkov.racenfctracer.MainActivity.fieldsJSON;
import static ru.galkov.racenfctracer.MainActivity.KEY;
import static ru.galkov.racenfctracer.MainActivity.getAltitude;
import static ru.galkov.racenfctracer.MainActivity.getLatitude;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;
import static ru.galkov.racenfctracer.MainActivity.getLongitude;
import static ru.galkov.racenfctracer.common.Utilites.chkKey;

public class AskServerTime extends AsyncTask<String, Void, String> {

    private TextView TimeLabel;
    private final String ASKER = "AskServerTime";
    private JSONObject outBoundJSON;

    public AskServerTime(TextView TimeLabel1) {
        TimeLabel = TimeLabel1;
    }


    @Override
    protected void onPreExecute(){
        makeOutBoundJSON();
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpProcessor HP = new HttpProcessor();
        HP.setASKER(ASKER);
        HP.setJson(outBoundJSON);
        return HP.execute();
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject JOAnswer = new JSONObject(result);
            if (chkKey((String) JOAnswer.get(fieldsJSON.key.toString()))) {
                   TimeLabel.setText(JOAnswer.getString(fieldsJSON.date.toString()));
            }
        }
        catch (JSONException e) {	e.printStackTrace();}

    }

// ========================================================
    private  void makeOutBoundJSON(){
//        {"asker":"AskServerTime", "key":"galkovvladimirandreevich"}
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(fieldsJSON.asker.toString(),ASKER);
            outBoundJSON.put(fieldsJSON.key.toString(),KEY);
            outBoundJSON.put(fieldsJSON.exec_login.toString(), getLogin());
            outBoundJSON.put(fieldsJSON.exec_level.toString(), getLevel());
            outBoundJSON.put(fieldsJSON.latitude.toString(), getLatitude());
            outBoundJSON.put(fieldsJSON.altitude.toString(), getAltitude());
            outBoundJSON.put(fieldsJSON.longitude.toString(), getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.adminLib.ActivityResultsTable;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskServerTime extends AsyncTask<String, Void, String> {

//    private ActivityResultsTable.ActivityResultsTableController ARTC;
    private TextView TimeLabel;
    private final String ASKER = "AskServerTime";
    private JSONObject outBoundJSON;
//    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;


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
            if (Utilites.chkKey((String) JOAnswer.get(MainActivity.fieldsJSON.key.toString()))) {
                   String regRecord = JOAnswer.get(MainActivity.fieldsJSON.date.toString()).toString();
                   TimeLabel.setText(regRecord);
            }
        }
        catch (JSONException e) {	e.printStackTrace();}

    }

// ========================================================
    private  void makeOutBoundJSON(){
//        {"asker":"AskServerTime", "key":"galkovvladimirandreevich"}
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(MainActivity.fieldsJSON.asker.toString(),ASKER);
            outBoundJSON.put(MainActivity.fieldsJSON.key.toString(),KEY);
            outBoundJSON.put(MainActivity.fieldsJSON.exec_login.toString(),MainActivity.getLogin());
            outBoundJSON.put(MainActivity.fieldsJSON.exec_level.toString(),MainActivity.getLevel());
            outBoundJSON.put(MainActivity.fieldsJSON.latitude.toString(),MainActivity.getLatitude());
            outBoundJSON.put(MainActivity.fieldsJSON.altitude.toString(),MainActivity.getAltitude());
            outBoundJSON.put(MainActivity.fieldsJSON.longitude.toString(),MainActivity.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
/*
    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;

    }*/

}
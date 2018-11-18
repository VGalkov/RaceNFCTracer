package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static ru.galkov.racenfctracer.MainActivity.KEY;
import static ru.galkov.racenfctracer.MainActivity.fieldsJSON;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;
import static ru.galkov.racenfctracer.common.Utilites.ErrorJSON;
import static ru.galkov.racenfctracer.common.Utilites.chkKey;

public class AskStartSructure extends AsyncTask<String, Void, String> {

    public Spinner startSpiner;
    private Context activityContext;
    private final String ASKER = "AskStartSructure";
    private JSONObject outBoundJSON;
    private long race_id;



    public void setActivityContext(Context activityContext) {
        this.activityContext = activityContext;
    }

    public void setStartSpiner(Spinner startSpiner1) {
        this.startSpiner =  startSpiner1;
    }

    public void setRaceID(long race_id1) {
        this.race_id = race_id1;
    }

    /*

    {
        "race_id":6,
        "startsConfig":[
            {"start_id":17,"active":"N","Id":0,"label":"1"},
            {"start_id":18,"active":"N","Id":1,"label":"2"}],
        "asker":"AskStartSructure",
        "key":"galkovvladimirandreevich"
    }

    */
    @Override
    protected void onPostExecute(String result) {


        try {
                JSONObject JOAnswer = new JSONObject(result);
                if (chkKey((String) JOAnswer.get(fieldsJSON.key.toString()))) {
                    JSONArray starts = JOAnswer.getJSONArray(fieldsJSON.startsConfig.toString());
                    String[] startsList = new String[starts.length()];

                    for (int i = 0; i<starts.length(); i++) {
                        JSONObject s = starts.getJSONObject(i);
                        startsList[i] = s.getString(fieldsJSON.start_id.toString());
                    }

                ArrayAdapter adapterStarts = new ArrayAdapter(activityContext,  android.R.layout.simple_spinner_item, startsList);
                startSpiner.setAdapter(adapterStarts);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    void makeOutBoundJSON() {
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(fieldsJSON.asker.toString(),ASKER);
            outBoundJSON.put(fieldsJSON.key.toString(),KEY);
            outBoundJSON.put(fieldsJSON.race_id.toString(),race_id);
            outBoundJSON.put(fieldsJSON.exec_login.toString(), getLogin());
            outBoundJSON.put(fieldsJSON.exec_level.toString(), getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
            outBoundJSON = ErrorJSON();
        }
    }

}

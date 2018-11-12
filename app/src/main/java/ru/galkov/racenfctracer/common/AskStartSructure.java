package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.adminLib.ActivityRaceSetup;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskStartSructure extends AsyncTask<String, Void, String> {
// запрос структуры рейсов и стартов.

    private final String ASKER = "AskStartSructure";
    private JSONObject outBoundJSON;
    private ActivityRaceSetup.ActivityRaceSetupController ARSController;
    private Context activityContext;
    private long race_id;
    public Spinner startSpiner;


    public AskStartSructure(ActivityRaceSetup.ActivityRaceSetupController ARSController1) {
        // вызывается ActivityRaceSetup
        ARSController = ARSController1;
    }

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
//                String serverKEY = JOAnswer.getString(MainActivity.fieldsJSON.key.toString());
//                String raceIdJSON = JOAnswer.getString(MainActivity.fieldsJSON.race_id.toString());
                JSONArray starts = JOAnswer.getJSONArray(MainActivity.fieldsJSON.startsConfig.toString());
                String[] startsList = new String[starts.length()];

            for (int i = 0; i<starts.length(); i++) {
                JSONObject s = starts.getJSONObject(i);
                startsList[i] = s.getString(MainActivity.fieldsJSON.start_id.toString());
            }

            ArrayAdapter adapterStarts = new ArrayAdapter(activityContext,  android.R.layout.simple_spinner_item, startsList);
            startSpiner.setAdapter(adapterStarts);

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
            outBoundJSON.put(MainActivity.fieldsJSON.asker.toString(),ASKER);
            outBoundJSON.put(MainActivity.fieldsJSON.key.toString(),KEY);
            outBoundJSON.put(MainActivity.fieldsJSON.race_id.toString(),race_id);
            outBoundJSON.put(MainActivity.fieldsJSON.exec_login.toString(),MainActivity.getLogin());
            outBoundJSON.put(MainActivity.fieldsJSON.exec_level.toString(),MainActivity.getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
            outBoundJSON = Utilites.ErrorJSON();
        }
    }



}

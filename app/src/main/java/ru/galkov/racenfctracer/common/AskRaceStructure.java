package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.adminLib.ActivityRaceSetup;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskRaceStructure extends AsyncTask<String, Void, String> {
    // запрос структуры рейсов и стартов.

    private final String ASKER = "AskRaceStructure";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    private MainActivity.trigger trigger;
    private MainActivity.writeMethod method;
    private ActivityRaceSetup.ActivityRaceSetupController ARSController;
    private Context activityContext;


    public AskRaceStructure(ActivityRaceSetup.ActivityRaceSetupController ARSController1) {
        // вызывается ActivityRaceSetup
        ARSController = ARSController1;
    }

    public void setActivityContext(Context activityContext) {
        this.activityContext = activityContext;
    }

    public void setMethod(MainActivity.writeMethod method) {
        this.method = method;
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
        // TODO конструируем спинеры. выпадающие списки рейсов и стартов связанные.

/*
	{   "asker":"AskForMainLog",
	    "racesConfig":
	        [
	            {
	                "race_id":"0", "starts": [
	                                    {start_id=0},
	                                    {start_id=1},
	                                    {start_id=2}
	                                ]
                }
                {
	                 "race_id":"1", "starts": [
	                                    {start_id=0},
	                                    {start_id=1},
	                                    {start_id=2},
	                                    {start_id=3},
	                                    {start_id=4},
	                                    {start_id=5}
	                                ]
	            }
	        ],
	    "key":"galkovvladimirandreevich"
	 }

*/
    try {
//            Spinner raceSpinner = ARSController.spinnerRace;
//            Spinner startSpinner = ARSController.spinnerStart;
            JSONObject JOAnswer = new JSONObject(result);
            String serverKEY = JOAnswer.getString(f.key.toString());
            JSONArray arr = JOAnswer.getJSONArray(f.racesConfig.toString());
            String[] raceList = new String[arr.length()];

            for (int i = 0; i<arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                raceList[i] = obj.getString(f.race_id.toString());
            }
            ArrayAdapter<String> adapterUsers = new ArrayAdapter<String>(activityContext,  android.R.layout.simple_spinner_item, raceList);
            ARSController.spinnerRace.setAdapter(adapterUsers);
    } catch (JSONException e) {	e.printStackTrace();}

    }


    void makeOutBoundJSON() {
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);
            outBoundJSON.put(f.key.toString(),KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

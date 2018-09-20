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

public class AskRaceStructure extends AsyncTask<String, Void, String> {
    // запрос структуры рейсов и стартов.

    private final String ASKER = "AskRaceStructure";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    private MainActivity.trigger trigger;
    private MainActivity.writeMethod method;
    private ActivityRaceSetup.ActivityRaceSetupController ARSController;
    private Context activityContext;
    public Spinner raceSpiner;


    public AskRaceStructure(ActivityRaceSetup.ActivityRaceSetupController ARSController1) {
        // вызывается ActivityRaceSetup
        ARSController = ARSController1;
    }

    public void setActivityContext(Context activityContext) {
        this.activityContext = activityContext;
    }

    public void setRaceSpiner(Spinner raceSpiner1) {
        this.raceSpiner =raceSpiner1;
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
/*

    {
        "asker":"AskRaceStructure",
        "key":"galkovvladimirandreevich",
        "racesConfig":[
                        {   "race_id":4,
                            "startsConfig":{"{"start_id":13,"active":"N","Id":0,"label":"5"}":{"start_id":13,"active":"N","Id":0,"label":"5"},"{"start_id":9,"active":"N","Id":0,"label":"1"}":{"start_id":9,"active":"N","Id":0,"label":"1"},"{"start_id":10,"active":"N","Id":0,"label":"2"}":{"start_id":10,"active":"N","Id":0,"label":"2"},"{"start_id":11,"active":"N","Id":0,"label":"3"}":{"start_id":11,"active":"N","Id":0,"label":"3"},"{"start_id":12,"active":"N","Id":0,"label":"4"}":{"start_id":12,"active":"N","Id":0,"label":"4"}},
                            "name":"??????",
                            "active":"N",
                            "Id":0
                        },
                        {   "race_id":5,
                             "startsConfig":{"{"start_id":16,"active":"N","Id":1,"label":"3"}":{"start_id":16,"active":"N","Id":1,"label":"3"},"{"start_id":14,"active":"N","Id":1,"label":"1"}":{"start_id":14,"active":"N","Id":1,"label":"1"},"{"start_id":15,"active":"N","Id":1,"label":"2"}":{"start_id":15,"active":"N","Id":1,"label":"2"}},
                             "name":"??????",
                             "active":"N",
                             "Id":1
                         },
                         {
                            "race_id":6,
                            "startsConfig":{"{"start_id":17,"active":"N","Id":2,"label":"1"}":{"start_id":17,"active":"N","Id":2,"label":"1"},"{"start_id":18,"active":"N","Id":2,"label":"2"}":{"start_id":18,"active":"N","Id":2,"label":"2"}},
                            "name":"??????",
                            "active":"N",
                            "Id":2
                         },
                         {
                            "race_id":7,
                            "startsConfig":{"{"start_id":19,"active":"N","Id":3,"label":"1"}":{"start_id":19,"active":"N","Id":3,"label":"1"},"{"start_id":23,"active":"N","Id":3,"label":"5"}":{"start_id":23,"active":"N","Id":3,"label":"5"},"{"start_id":24,"active":"N","Id":3,"label":"6"}":{"start_id":24,"active":"N","Id":3,"label":"6"},"{"start_id":25,"active":"N","Id":3,"label":"7"}":{"start_id":25,"active":"N","Id":3,"label":"7"},"{"start_id":26,"active":"N","Id":3,"label":"8"}":{"start_id":26,"active":"N","Id":3,"label":"8"},"{"start_id":20,"active":"N","Id":3,"label":"2"}":{"start_id":20,"active":"N","Id":3,"label":"2"},"{"start_id":21,"active":"N","Id":3,"label":"3"}":{"start_id":21,"active":"N","Id":3,"label":"3"},"{"start_id":22,"active":"N","Id":3,"label":"4"}":{"start_id":22,"active":"N","Id":3,"label":"4"}},
                            "name":"?????????",
                            "active":"N",
                            "Id":3
                          }
                      ]
      }

*/

    try {
            JSONObject JOAnswer = new JSONObject(result);
            String serverKEY = JOAnswer.getString(f.key.toString());
            JSONArray races = JOAnswer.getJSONArray(f.racesConfig.toString());
            String[] racesList = new String[races.length()];

            for (int i = 0; i<races.length(); i++) {
                JSONObject r = races.getJSONObject(i);
                racesList[i] = r.getString(f.race_id.toString());
//                raceStarts[i] = r.getJSONArray(f.startsConfig.toString());
            }

            ArrayAdapter adapterStarts = new ArrayAdapter(activityContext,  android.R.layout.simple_spinner_item, racesList);
            raceSpiner.setAdapter(adapterStarts);
    } catch (JSONException e) {
        e.printStackTrace();
    }

    }


    void makeOutBoundJSON() {
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);
            outBoundJSON.put(f.key.toString(),KEY);
            outBoundJSON.put(f.exec_login.toString(),MainActivity.getLogin());
            outBoundJSON.put(f.exec_level.toString(),MainActivity.getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
            outBoundJSON = Utilites.ErrorJSON();
        }
    }

}

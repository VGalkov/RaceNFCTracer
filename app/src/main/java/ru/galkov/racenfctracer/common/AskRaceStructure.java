package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskRaceStructure extends AsyncTask<String, Void, String> {
    // запрос структуры рейсов и стартов.

    private final String ASKER = "AskRaceStructure";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    private MainActivity.trigger trigger;
    private MainActivity.writeMethod method;


    public AskRaceStructure() {
        // вызывается ActivityRaceSetup
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
        // TODO выпадающие списки рейсов и стартов связанные.
    }


    void makeOutBoundJSON() {
        // {"asker":" AskCurrentRaceStart","key":"galkovvladimirandreevich"}
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);
            outBoundJSON.put(f.key.toString(),KEY);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

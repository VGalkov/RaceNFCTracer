package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class SendActiveRaceStart extends AsyncTask<String, Void, String> {
    // прописывает активный рейс и старт.

    private final String ASKER = "AskRaceStructure";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    private MainActivity.trigger trigger;
    private MainActivity.writeMethod method;

    // fields
    private long race_id =0L;
    private long start_id =0L;


    public void setRace_id(long race_id) {
        this.race_id = race_id;
    }

    public void setStart_id(long start_id) {
        this.start_id = start_id;
    }

    public  SendActiveRaceStart() {
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
            outBoundJSON.put(f.race_id.toString(),race_id);
            outBoundJSON.put(f.start_id.toString(),start_id);
            outBoundJSON.put(f.key.toString(),KEY);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.adminLib.ActivityResultsTable;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskServerTime extends AsyncTask<String, Void, String> {

    private ActivityResultsTable.ActivityResultsTableController ARTC;
    private TextView TimeLabel;
    private final String ASKER = "AskServerTime";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;


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
        //TODO rewrite
// {"date":"2018.09.11 09:26:46","asker":"AskServerTime","key":"galkovvladimirandreevich"}
        try {
            JSONObject JOAnswer = new JSONObject(result);
            if (Utilites.chkKey((String) JOAnswer.get(f.key.toString()))) {
                   String regRecord = JOAnswer.get(f.date.toString()).toString();
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
            outBoundJSON.put(f.asker.toString(),ASKER);
            outBoundJSON.put(f.key.toString(),KEY);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;

    }

}
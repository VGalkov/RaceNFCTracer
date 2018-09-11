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


    public AskServerTime(TextView TimeLabel1) {
        TimeLabel = TimeLabel1;
    }

    private  void makeOutBoundJSON(){
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);

            outBoundJSON.put(f.key.toString(),KEY);

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

    @Override
    protected void onPostExecute(String result) {

        try {
            JSONObject JOAnswer = new JSONObject(result);
            if (Utilites.chkKey((String) JOAnswer.get("key"))) {
                if(JOAnswer.get("Status").equals("TRUE")) {  // TRUE|FALSE
// пишем, что ок. и помещаем регистрационные данные в монитор в админа c датой с сервера.
// formatForDateNow.format(Dt2)
                    String regRecord = JOAnswer.get("date").toString();
                    TimeLabel.setText(regRecord);
                }
                else {
                    TimeLabel.setText(JOAnswer.get("Error").toString());
                }

            }
        }
        catch (JSONException e) {	e.printStackTrace();}

    }





}
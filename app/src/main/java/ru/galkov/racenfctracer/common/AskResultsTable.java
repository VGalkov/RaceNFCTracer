package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskResultsTable extends AsyncTask<String, Void, String> {


    private TextView userLogger;
    private final String ASKER = "AskResultsTable";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;

    public AskResultsTable(TextView userLogger1) {
        this.userLogger = userLogger1;
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
// отображаем таблицу результатов пользователей ... кстати не понятно поака как ..
//     {"asker":"AskResultsTable","rows":[{"date":"2018.09.11 10:59:27","altitude":"50.2143","latitude":"50.2134","Id":0,"login":"+79272006026","mark":"1234567890","longitude":"50.2134"},{"date":"2018.09.11 10:59:27","altitude":"50.2143","latitude":"50.2134","Id":1,"login":"+79272006026","mark":"1234567890","longitude":"50.2134"}],"key":"galkovvladimirandreevich"}
// TODO переписать.
        try {
            JSONArray arr = new JSONArray(result);
            for(int i = 0 ; i< arr.length() ; i++) {
                JSONObject obj = arr.getJSONObject(i);
                String str = obj.getString("user") + ": " + obj.getString("mark") + "(" + obj.getString("date") + ")" +"["+obj.getString("gpsX")+", "+obj.getString("gpsY")+", "+obj.getString("gpsZ")+"]\n";
                userLogger.append(str);
            }
        } catch (JSONException e) {	e.printStackTrace();}

    }



    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;

    }

    private  void makeOutBoundJSON(){
        //     {"asker":"AskResultsTable","key":"galkovvladimirandreevich"}
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);
            outBoundJSON.put(f.key.toString(),KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}

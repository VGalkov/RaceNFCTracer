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

            /*

	{   "asker":"AskForMainLog",
	    "rows":[
	        {"date":"2018.09.16 00:00:00",
	            "altitude":2,
	            "latitude":2,
	            "Id":0,
	            "login":8,
	            "mark":4,
	            "longitude":2
	            }
	            ],
	    "key":"galkovvladimirandreevich"
	 } userLogger
===========================================
    * */
        String str = "\n";

        try {
            JSONObject JOAnswer = new JSONObject(result);
            // TODO проверка ключа
            String serverKEY = JOAnswer.getString(f.key.toString());
            JSONArray arr = JOAnswer.getJSONArray(f.rows.toString());
            for(int i = 0 ; i< arr.length() ; i++) {
                JSONObject obj = arr.getJSONObject(i);
                str = str + obj.getString("login") + " прошёл метку -> \n" + obj.getString("mark") + "\n время: " + obj.getString("date") + "\n" +"на точке: "+obj.getString("latitude")+", "+obj.getString("longitude")+", "+obj.getString("altitude")+" \n\n";
            }

            if (method == MainActivity.writeMethod.Append) {
                userLogger.append(str);
            }
            else {
                userLogger.setText(str);
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

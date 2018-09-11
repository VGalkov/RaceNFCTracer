package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskForMainLog extends AsyncTask<String, Void, String> {

    private String URL_Extention;
    private TextView ResultEkran;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;
    private Context activity;

    private final String ASKER = "AskForMainLog";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    //
    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;
    }

    public AskForMainLog(TextView ResultEkran1) {
        this.ResultEkran = ResultEkran1;
    }

    public  void setURL_Extention(String str) {
        URL_Extention = str;
    }

    // для соообщейний
    public void setActivity(Context context1) {
        activity = context1;
    }

    private  void makeOutBoundJSON(){
        // {"asker":"AskForMainLog","key":"galkovvladimirandreevich"}
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

    /*

{	"asker":"AskForMainLog",
	"rows":[
		"date":"2018.09.11 10:41:35",
		"altitude":"50.2143",
		"latitude":"50.2134",
		"Id":0,
		"login":"+79272006026",
		"mark":"1234567890",
		"longitude":"50.2134"},

		{"date":"2018.09.11 10:41:35","altitude":"50.2143","latitude":"50.2134","Id":1,"login":"+79272006026","mark":"1234567890","longitude":"50.2134"}
		],

	"key":"galkovvladimirandreevich"}
===========================================
    * */
        String str = "\n";
        // структура поменялась, см ерверную часть.
        try {
                JSONArray arr = new JSONArray(result);
                for(int i = 0 ; i< arr.length() ; i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    str = str + obj.getString("user") + " прошёл метку -> \n" + obj.getString("mark") + "\n время: " + obj.getString("date") + "\n" +"на точке: "+obj.getString("gpsX")+", "+obj.getString("gpsY")+", "+obj.getString("gpsZ")+" \n\n";
                }

            if (method == MainActivity.writeMethod.Append) {
                ResultEkran.append(str);
            }
            else {
                ResultEkran.setText(str);
            }
        } catch (JSONException e) {	e.printStackTrace();}
    }
}


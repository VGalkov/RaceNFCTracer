package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AskForMainLog extends AsyncTask<String, Void, String> {

    private String URL_Extention;
    private TextView ResultEkran;


    public AskForMainLog(TextView ResultEkran1) {
        this.ResultEkran = ResultEkran1;
    }

    public  void setURL_Extention(String str) {
        URL_Extention = str;
    }

    @Override
    protected String doInBackground(String... strings) {
        return Utilites.getMainLogJSON_ZAGLUSHKA();
    }

    @Override
    protected void onPostExecute(String result) {

        try {
                JSONArray arr = new JSONArray(result);
                for(int i = 0 ; i< arr.length() ; i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String str = obj.getString("user") + ": " + obj.getString("Mark") + "(" + obj.getString("Date") + ")" +"["+obj.getString("gpsX")+", "+obj.getString("gpsY")+", "+obj.getString("gpsZ")+"]\n";
                    ResultEkran.append(str);
                }
        } catch (JSONException e) {	e.printStackTrace();}
    }
}


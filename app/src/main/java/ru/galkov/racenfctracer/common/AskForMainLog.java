package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

public class AskForMainLog extends AsyncTask<String, Void, String> {

    private String URL_Extention;
    private TextView ResultEkran;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;
    private Context activity;
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

    @Override
    protected void onPreExecute(){

    }

    @Override
    protected String doInBackground(String... strings) {
        return Utilites.getMainLogJSON_ZAGLUSHKA();
    }

    @Override
    protected void onPostExecute(String result) {
        String str = "\n";
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


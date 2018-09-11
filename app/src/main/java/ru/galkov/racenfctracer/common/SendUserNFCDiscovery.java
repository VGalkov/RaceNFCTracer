package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.DECIMAL_FORMAT;
import static ru.galkov.racenfctracer.MainActivity.KEY;

public class SendUserNFCDiscovery extends AsyncTask<String, Void, String> {

    private GPS GPS_System;
    private String user;
    private String mark;
    private TextView User_Monitor;
    private Double Latitude;
    private Double Longitude;
    private Double Altitude;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;
    private Context activity;
    public DecimalFormat df = DECIMAL_FORMAT;
    private final String ASKER = "SendUserNFCDiscovery";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;

    public SendUserNFCDiscovery(TextView User_Monitor1) {
        User_Monitor = User_Monitor1;
    }

    public  void setContext(Context c1) {
        activity = c1;
    }

    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;
    }

    public void setGPS_System(GPS GPS_System1) {

        this.GPS_System = GPS_System1;
        // не знаю нужно ли это вот...
        if (GPS_System.gettLatitude() != null) {
            Latitude = GPS_System.gettLatitude();
        }
        else { Latitude = 0.0; }

        if (GPS_System.getLongitude() != null) {
            Longitude = GPS_System.getLongitude();
        }
        else { Longitude = 0.0; }

        if (GPS_System.getAltitude() != null) {
            Altitude = GPS_System.getAltitude();
        }
        else { Altitude = 0.0; }
    }

    public void setMark(String mark1) {
        this.mark = mark1;
    }

    public void setUser(String user1) {
        this.user = user1;
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
                    String regRecord ="\n Зарегистрировано: \n" + "Время прохождения: " +JOAnswer.get("date") + ", \n" +JOAnswer.get("user") +", метка:"+ JOAnswer.get("mark") +"\n" + "координаты: [" + JOAnswer.get("latitude") + ", " + JOAnswer.get("longitude") + ", " + JOAnswer.get("altitude") + "] \n \n" ;
                    if (method == MainActivity.writeMethod.Append)
                        User_Monitor.append(regRecord);
                    else User_Monitor.setText(regRecord);
                }
                else {
                    Utilites.messager(activity, JOAnswer.get("Error").toString());
                }

            }else {
                    Utilites.messager(activity, JOAnswer.get("Error").toString());
                }
        }
        catch (JSONException e) {	e.printStackTrace();}
    }
}


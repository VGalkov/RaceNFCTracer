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
    private String login;
    private String mark;
    private TextView User_Monitor;
    private Double latitude = 0.00, longitude = 0.00 , altitude = 0.00;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;
    private Context activity;
    private long race =0L;
    public DecimalFormat df = DECIMAL_FORMAT;
    private final String ASKER = "SendUserNFCDiscovery";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;

    public SendUserNFCDiscovery(TextView User_Monitor1) {
        User_Monitor = User_Monitor1;
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
        // {"date":"2018.09.11 12:05:39","altitude":"50.2143","latitude":"50.2134","asker":"SendUserNFCDiscovery","login":"+790002229999","mark":"09201724307","key":"galkovvladimirandreevich","status":"TRUE","longitude":"50.2134"}
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

    // =============================================================


    private  void makeOutBoundJSON(){
/*

    {
            "altitude":0,
            "latitude":0,
            "asker":"SendUserNFCDiscovery",
            "login":"+79272006026",
            "mark":"777777",
            "key":"galkovvladimirandreevich",
            "longitude":0
            ""
    }

                */
// TODO RACE START интегрировать, пока это заглушка и с координатными полями херня какая-то.
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);
            outBoundJSON.put(f.mark.toString(),mark);
            outBoundJSON.put(f.login.toString(),login);
            outBoundJSON.put(f.longitude.toString(),longitude);
            outBoundJSON.put(f.altitude.toString(),altitude);
            outBoundJSON.put(f.latitude.toString(),latitude);
            outBoundJSON.put(f.key.toString(),KEY);
            outBoundJSON.put(f.race.toString(),race);
            outBoundJSON.put(f.start.toString(),race);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



// SETTERS - GETTERS ===========================
    public  void setContext(Context c1) {
        activity = c1;
    }

    public void setGPS_System(GPS GPS_System1) {
        this.GPS_System = GPS_System1;
        this.altitude = GPS_System.getAltitude();
        this.longitude = GPS_System.getLongitude();
        this.latitude = GPS_System.getLatitude();
    }

    public void setMark(String mark1) {
        this.mark = mark1;
    }

    public void setRace(long race1) {
        this.race = race1;
    }

    public void setUser(String user1) {
        this.login = user1;
    }

    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;

    }
}


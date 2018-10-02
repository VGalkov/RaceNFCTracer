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
    private String mark;
    private TextView User_Monitor;
    private Double latitude = 0.00, longitude = 0.00 , altitude = 0.00;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;
    private Context activity;
    private long race =0L;
    private long markDelta =0L;
    public DecimalFormat df = DECIMAL_FORMAT;
    private final String ASKER = "SendUserNFCDiscovery";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    private String masterMark;


    public void setMarkDelta(long markDelta) {
        this.markDelta = markDelta;
    }

    public void setMasterMark(String masterMark1) {
        this.masterMark = masterMark1;
    }

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
        try {
            JSONObject JOAnswer = new JSONObject(result);
            if (Utilites.chkKey((String) JOAnswer.get("key"))) {
                if(JOAnswer.get(f.status.toString()).equals("TRUE")) {  // TRUE|FALSE
                    String regRecord ="\n Зарегистрировано: \n"
                            + "Время прохождения: " +JOAnswer.get(f.date.toString()) + ", \n"
                            + JOAnswer.get(f.login.toString()) +", метка:"+ JOAnswer.get(f.mark.toString()) +"\n"
                            + "координаты: [" + df.format(JOAnswer.getDouble(f.latitude.toString())) + ", "
                                + df.format(JOAnswer.getDouble(f.longitude.toString()))
                            + ", " + df.format(JOAnswer.getDouble(f.altitude.toString())) + "] \n "
                            + "Мероприятие: " + JOAnswer.get(f.race_id.toString()) + ", Старт: " + JOAnswer.get(f.start_id.toString()) + "\n\n" ;
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

        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);
            outBoundJSON.put(f.mark.toString(),mark);
            outBoundJSON.put(f.master_mark_label.toString(), masterMark);
            outBoundJSON.put(f.master_mark_delta.toString(), markDelta);
            outBoundJSON.put(f.login.toString(), MainActivity.getLogin());// повтор...
            outBoundJSON.put(f.longitude.toString(),longitude);
            outBoundJSON.put(f.altitude.toString(),altitude);
            outBoundJSON.put(f.latitude.toString(),latitude);
            outBoundJSON.put(f.key.toString(),KEY);
            outBoundJSON.put(f.race.toString(),MainActivity.getRace_id());
            outBoundJSON.put(f.start.toString(),MainActivity.getStart_id());
            outBoundJSON.put(f.exec_login.toString(),MainActivity.getLogin());
            outBoundJSON.put(f.exec_level.toString(),MainActivity.getLevel());

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

    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;

    }
}


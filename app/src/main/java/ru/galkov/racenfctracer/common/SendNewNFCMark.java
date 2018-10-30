package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class SendNewNFCMark extends AsyncTask<String, Void, String> {

    private final String ASKER = "SendNewNFCMark";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    private MainActivity.trigger t;
    private String mark;
    private String type;
    private long race = 0;
    private Double longitude = 0.00, latitude =0.00, altitude =0.00;
    private GPS GPS_system;
    private TextView NFC_ConfigurationLog;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;
    private int PositionMethod = 1;

    public SendNewNFCMark(TextView NFC_ConfigurationLog1) {
        this.NFC_ConfigurationLog = NFC_ConfigurationLog1;
    }

    public void setMarkPositionMethod(int method) {
        this.PositionMethod = method;
    }

    public void setMark(String mark1) {
        this.mark = mark1;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public void setGPS_System(GPS GPS_System1) {
        if (PositionMethod== 1) {
            // ручная установка долготы и широты. высота=0. ручная установка
            this.GPS_system = null;
            this.altitude = 0.0;
        }
        else if (PositionMethod== 2) {
            // автоматические координаты.
            this.GPS_system = GPS_System1;
            this.latitude = GPS_system.getLatitude();
            this.longitude = GPS_system.getLongitude();
            this.altitude = GPS_system.getAltitude();
        }
        else if (PositionMethod== 3) {
            // нет координат.
            this.GPS_system = null;
            this.latitude = 0.0;
            this.longitude = 0.0;
            this.altitude = 0.0;
        }

    }

    public void setRace(long race1) {
        this.race = race1;
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
// обработка ответа сервера о сохранении новой метки
/*

    {
        "asker":"SendNewNFCMark",
        "mark":"79100",
        "key":"galkovvladimirandreevich",
        "status":"TRUE"
    }

*/
        try {
                JSONObject JOAnswer = new JSONObject(result);
                if (Utilites.chkKey((String) JOAnswer.get("key"))) {
                    if(JOAnswer.get(f.status.toString()).equals(t.TRUE.toString())) {  // TRUE|FALSE
                        wrire("Зарегистрирована метка: " + JOAnswer.get("mark") +"\n");
                    }
                    else
                        wrire(JOAnswer.get(f.error.toString()).toString());
                }

            }
        catch (JSONException e) {	e.printStackTrace();}
    }


    private void wrire(String str) {
        if (method == method.Set)  NFC_ConfigurationLog.setText(str);
        else NFC_ConfigurationLog.append(str);
    }

    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;

    }
/*
    public void setType(String type1) {
        this.type = type1;
    }
*/
    private  void makeOutBoundJSON(){

        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);
            outBoundJSON.put(f.mark.toString(),this.mark);
            outBoundJSON.put(f.key.toString(),KEY);
//            outBoundJSON.put(f.mark_type.toString(),type);
            outBoundJSON.put(f.longitude.toString(), this.longitude);
            outBoundJSON.put(f.altitude.toString(), this.altitude);
            outBoundJSON.put(f.latitude.toString(), this.latitude);
            outBoundJSON.put(f.exec_login.toString(),MainActivity.getLogin());
            outBoundJSON.put(f.exec_level.toString(),MainActivity.getLevel());
            outBoundJSON.put("race", race);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

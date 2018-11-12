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
    private String mark;
    private String type;
    private long race = 0;
    private Double longitude = 0.00, latitude =0.00, altitude =0.00;
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

    public void setGPS_System() {
        if (PositionMethod== 1) {
            // ручная установка долготы и широты. высота=0. ручная установка
            this.altitude = 0.0;
        }
        else if (PositionMethod== 2) {
            // автоматические координаты.
            this.latitude = MainActivity.getLatitude();
            this.longitude = MainActivity.getLongitude();
            this.altitude = MainActivity.getAltitude();
        }
        else if (PositionMethod== 3) {
            // нет координат.
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
                    if(JOAnswer.get(MainActivity.fieldsJSON.status.toString()).equals(MainActivity.trigger.TRUE.toString())) {  // TRUE|FALSE
                        wrire("Зарегистрирована метка: " + JOAnswer.get("mark") +"\n");
                    }
                    else
                        wrire(JOAnswer.get(MainActivity.fieldsJSON.error.toString()).toString());
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
            outBoundJSON.put(MainActivity.fieldsJSON.asker.toString(),ASKER);
            outBoundJSON.put(MainActivity.fieldsJSON.mark.toString(),this.mark);
            outBoundJSON.put(MainActivity.fieldsJSON.key.toString(),KEY);
            outBoundJSON.put(MainActivity.fieldsJSON.longitude.toString(), this.longitude);
            outBoundJSON.put(MainActivity.fieldsJSON.altitude.toString(), this.altitude);
            outBoundJSON.put(MainActivity.fieldsJSON.latitude.toString(), this.latitude);
            outBoundJSON.put(MainActivity.fieldsJSON.exec_login.toString(),MainActivity.getLogin());
            outBoundJSON.put(MainActivity.fieldsJSON.exec_level.toString(),MainActivity.getLevel());
            outBoundJSON.put("race", race);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

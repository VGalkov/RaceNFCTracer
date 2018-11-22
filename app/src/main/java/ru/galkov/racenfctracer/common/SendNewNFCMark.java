package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.MainActivity.fieldsJSON;
import ru.galkov.racenfctracer.MainActivity.trigger;
import ru.galkov.racenfctracer.MainActivity.writeMethod;

import static ru.galkov.racenfctracer.MainActivity.KEY;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;
import static ru.galkov.racenfctracer.common.Utilites.chkKey;

public class SendNewNFCMark extends AsyncTask<String, Void, String> {

    private final String ASKER = "SendNewNFCMark";
    private JSONObject outBoundJSON;
    private String mark;
    private long race = 0;
    private Double longitude = 0.00, latitude =0.00, altitude =0.00;
    private TextView NFC_ConfigurationLog;
    private writeMethod method = writeMethod.Set;
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
                if (chkKey((String) JOAnswer.get(fieldsJSON.key.toString()))) {
                    if(JOAnswer.getString(fieldsJSON.status.toString()).equals(trigger.TRUE.toString())) {
                        write("Зарегистрирована метка: " + JOAnswer.getString(fieldsJSON.mark.toString()) +"\n");
                    }
                    else
                        write(JOAnswer.get(fieldsJSON.error.toString()).toString());
                }

            }
        catch (JSONException e) {	e.printStackTrace();}
    }


    private void write(String str) {
        if (method == writeMethod.Set)  NFC_ConfigurationLog.setText(str);
        else NFC_ConfigurationLog.append(str);
    }

    public void setMethod(writeMethod method1) {
        method = method1;

    }

    private  void makeOutBoundJSON(){

        try {
            outBoundJSON = new JSONObject()
                    .put(fieldsJSON.asker.toString(),ASKER)
                    .put(fieldsJSON.mark.toString(),this.mark)
                    .put(fieldsJSON.key.toString(),KEY)
                    .put(fieldsJSON.longitude.toString(), this.longitude)
                    .put(fieldsJSON.altitude.toString(), this.altitude)
                    .put(fieldsJSON.latitude.toString(), this.latitude)
                    .put(fieldsJSON.exec_login.toString(),getLogin())
                    .put(fieldsJSON.exec_level.toString(),getLevel())
                    .put("race", race);

        } catch (JSONException e) {
            e.printStackTrace();
        }
             }
             }

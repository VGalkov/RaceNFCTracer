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
    private long race = 0;
    private Double longitude = 0.00, latitude =0.00, altitude =0.00;
    private GPS GPS_system;
    private TextView NFC_ConfigurationLog;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;

    public SendNewNFCMark(TextView NFC_ConfigurationLog1) {
        this.NFC_ConfigurationLog = NFC_ConfigurationLog1;
    }

    public void setMark(String mark1) {
        this.mark = mark1;
    }

    public void setGPS_System(GPS GPS_System1) {
            this.GPS_system = GPS_System1;
            this.latitude = GPS_system.getLatitude();
            this.longitude = GPS_system.getLongitude();
            this.altitude = GPS_system.getAltitude();
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
        "mark":"777777",
        "key":"galkovvladimirandreevich",
        "status":"TRUE"
    }

*/
        try {
                JSONObject JOAnswer = new JSONObject(result);
                if (Utilites.chkKey((String) JOAnswer.get("key"))) {
                    if(JOAnswer.get(f.status.toString()).equals(t.TRUE)) {  // TRUE|FALSE
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

    private  void makeOutBoundJSON(){

// {
//  "race":0,
//  "asker":"SendNewNFCMark",
//  "mark":"42421",
//  "key":"galkovvladimirandreevich"
// }
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);
            outBoundJSON.put(f.mark.toString(),this.mark);
            outBoundJSON.put(f.key.toString(),KEY);
            outBoundJSON.put(f.longitude.toString(), this.longitude);
            outBoundJSON.put(f.altitude.toString(), this.altitude);
            outBoundJSON.put(f.latitude.toString(), this.latitude);
            outBoundJSON.put("race", race);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

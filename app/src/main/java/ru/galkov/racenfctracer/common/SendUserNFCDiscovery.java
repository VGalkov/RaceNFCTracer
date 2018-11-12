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

    private String mark;
    private TextView User_Monitor;
    private Double latitude = 0.00, longitude = 0.00 , altitude = 0.00;
    private Double masterLatitude = 0.00, masterLongitude = 0.00 , masterAltitude = 0.00;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;
    private Context activity;
    private long race =0L;
    private long markDelta =0L;
    public DecimalFormat df = DECIMAL_FORMAT;
    private final String ASKER = "SendUserNFCDiscovery";
    private JSONObject outBoundJSON;
    private String masterMark;

    public void setMasterAltitude(Double masterAltitude) {
        this.masterAltitude = masterAltitude;
    }

    public void setMasterLatitude(Double masterLatitude) {
        this.masterLatitude = masterLatitude;
    }

    public void setMasterLongitude(Double masterLongitude) {
        this.masterLongitude = masterLongitude;
    }

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
                if(JOAnswer.get(MainActivity.fieldsJSON.status.toString()).equals("TRUE")) {  // TRUE|FALSE
                    String regRecord ="\n Зарегистрировано: \n"
                            + "Время прохождения: " +JOAnswer.get(MainActivity.fieldsJSON.date.toString()) + ", \n"
                            + JOAnswer.get(MainActivity.fieldsJSON.login.toString()) +", метка:"+ JOAnswer.get(MainActivity.fieldsJSON.mark.toString()) +"\n"
                            + "координаты: [" + df.format(JOAnswer.getDouble(MainActivity.fieldsJSON.latitude.toString())) + ", "
                                + df.format(JOAnswer.getDouble(MainActivity.fieldsJSON.longitude.toString()))
                            + ", " + df.format(JOAnswer.getDouble(MainActivity.fieldsJSON.altitude.toString())) + "] \n "
                            + "Мероприятие: " + JOAnswer.get(MainActivity.fieldsJSON.race_id.toString()) + ", Старт: " + JOAnswer.get(MainActivity.fieldsJSON.start_id.toString()) + "\n\n" ;
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
            outBoundJSON.put(MainActivity.fieldsJSON.asker.toString(),ASKER);
            outBoundJSON.put(MainActivity.fieldsJSON.mark.toString(),mark);
            outBoundJSON.put(MainActivity.fieldsJSON.master_mark_label.toString(), masterMark);
            outBoundJSON.put(MainActivity.fieldsJSON.master_mark_delta.toString(), markDelta);
            outBoundJSON.put(MainActivity.fieldsJSON.login.toString(), MainActivity.getLogin());// повтор...
            outBoundJSON.put(MainActivity.fieldsJSON.mark_master_longitude.toString(),longitude);
            outBoundJSON.put(MainActivity.fieldsJSON.mark_master_altitude.toString(),altitude);
            outBoundJSON.put(MainActivity.fieldsJSON.mark_master_latitude.toString(),latitude);
            outBoundJSON.put(MainActivity.fieldsJSON.longitude.toString(),longitude);
            outBoundJSON.put(MainActivity.fieldsJSON.altitude.toString(),altitude);
            outBoundJSON.put(MainActivity.fieldsJSON.latitude.toString(),latitude);
            outBoundJSON.put(MainActivity.fieldsJSON.key.toString(),KEY);
            outBoundJSON.put(MainActivity.fieldsJSON.race.toString(),MainActivity.getRace_id());
            outBoundJSON.put(MainActivity.fieldsJSON.start.toString(),MainActivity.getStart_id());
            outBoundJSON.put(MainActivity.fieldsJSON.exec_login.toString(),MainActivity.getLogin());
            outBoundJSON.put(MainActivity.fieldsJSON.exec_level.toString(),MainActivity.getLevel());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



// SETTERS - GETTERS ===========================
    public  void setContext(Context c1) {
        activity = c1;
    }

    public void setGPS_System() {
        this.altitude = MainActivity.getAltitude();
        this.longitude = MainActivity.getLongitude();
        this.latitude = MainActivity.getLatitude();
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


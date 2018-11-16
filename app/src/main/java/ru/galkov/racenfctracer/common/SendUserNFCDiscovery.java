package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import org.json.*;
import ru.galkov.racenfctracer.MainActivity;
import static ru.galkov.racenfctracer.MainActivity.DECIMAL_FORMAT;
import static ru.galkov.racenfctracer.MainActivity.KEY;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;
import static ru.galkov.racenfctracer.MainActivity.getRace_id;
import static ru.galkov.racenfctracer.MainActivity.getStart_id;
import static ru.galkov.racenfctracer.MainActivity.writeMethod;
import static ru.galkov.racenfctracer.common.Utilites.chkKey;
import static ru.galkov.racenfctracer.common.Utilites.messager;
import ru.galkov.racenfctracer.MainActivity.fieldsJSON;
import ru.galkov.racenfctracer.MainActivity.trigger;

public class SendUserNFCDiscovery extends AsyncTask<String, Void, String> {

    private final String ASKER = "SendUserNFCDiscovery";
    private String mark, masterMark;
    private TextView User_Monitor;
    private Double latitude = 0.00, longitude = 0.00 , altitude = 0.00;
    private Double masterLatitude = 0.00, masterLongitude = 0.00 , masterAltitude = 0.00;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;
    private Context activity;
    private long race =0L;
    private long markDelta =0L;
    private JSONObject outBoundJSON;

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
            if (chkKey((String) JOAnswer.get(fieldsJSON.key.toString()))) {
                if(JOAnswer.getString(fieldsJSON.status.toString()).equals(trigger.TRUE)) {  // TRUE|FALSE
                    String regRecord ="\n Зарегистрировано: \n"
                            + "Время прохождения: " +JOAnswer.get(fieldsJSON.date.toString()) + ", \n"
                            + JOAnswer.get(fieldsJSON.login.toString()) +", метка:"+ JOAnswer.get(fieldsJSON.mark.toString()) +"\n"
                            + "координаты: [" + DECIMAL_FORMAT.format(JOAnswer.getDouble(MainActivity.fieldsJSON.latitude.toString())) + ", "
                                + DECIMAL_FORMAT.format(JOAnswer.getDouble(fieldsJSON.longitude.toString()))
                            + ", " + DECIMAL_FORMAT.format(JOAnswer.getDouble(fieldsJSON.altitude.toString())) + "] \n "
                            + "Мероприятие: " + JOAnswer.get(fieldsJSON.race_id.toString()) + ", Старт: " + JOAnswer.get(fieldsJSON.start_id.toString()) + "\n\n" ;
                    if (method == writeMethod.Append)
                        User_Monitor.append(regRecord);
                    else User_Monitor.setText(regRecord);
                }
                else {
                    messager(activity, JOAnswer.getString("Error"));
                }

            }else {
                    messager(activity, JOAnswer.getString("Error"));
                }
        }
        catch (JSONException e) {	e.printStackTrace();}
    }

    // =============================================================


    private  void makeOutBoundJSON(){

        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(fieldsJSON.asker.toString(),ASKER);
            outBoundJSON.put(fieldsJSON.mark.toString(),mark);
            outBoundJSON.put(fieldsJSON.master_mark_label.toString(), masterMark);
            outBoundJSON.put(fieldsJSON.master_mark_delta.toString(), markDelta);
            outBoundJSON.put(fieldsJSON.login.toString(), getLogin());
            outBoundJSON.put(fieldsJSON.mark_master_longitude.toString(),longitude);
            outBoundJSON.put(fieldsJSON.mark_master_altitude.toString(),altitude);
            outBoundJSON.put(fieldsJSON.mark_master_latitude.toString(),latitude);
            outBoundJSON.put(fieldsJSON.longitude.toString(),longitude);
            outBoundJSON.put(fieldsJSON.altitude.toString(),altitude);
            outBoundJSON.put(fieldsJSON.latitude.toString(),latitude);
            outBoundJSON.put(fieldsJSON.key.toString(),KEY);
            outBoundJSON.put(fieldsJSON.race.toString(),getRace_id());
            outBoundJSON.put(fieldsJSON.start.toString(),getStart_id());
            outBoundJSON.put(fieldsJSON.exec_login.toString(),getLogin());
            outBoundJSON.put(fieldsJSON.exec_level.toString(),getLevel());

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


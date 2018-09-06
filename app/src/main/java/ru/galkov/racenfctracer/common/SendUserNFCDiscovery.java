package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.ActivityUserManager;
import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class SendUserNFCDiscovery extends AsyncTask<String, Void, String> {

    private String SERVER_URL = MainActivity.SERVER_URL;
    private GPS GPS_System;
    private String user;
    private String mark;
    private ActivityUserManager.ActivityUserManagereController AUMC;

    public SendUserNFCDiscovery(ActivityUserManager.ActivityUserManagereController AUMC1) {
        AUMC = AUMC1;
    }

    public void setGPS_System(GPS GPS_System1) {
        this.GPS_System = GPS_System1;
    }

    public void setMark(String mark1) {
        this.mark = mark1;
    }

    public void setUser(String user1) {
        this.user = user1;
    }

    @Override
    protected String doInBackground(String... strings) {
        // отправляем на сервер уведомление о считывании участником NFC  метки

        JSONObject SendThis = new JSONObject();
        try {
            SendThis.put("Mark",mark);
            SendThis.put("user",user);
            SendThis.put("key",KEY);
            SendThis.put("geoLatitude", GPS_System.gettLatitude());
            SendThis.put("geoLongitude", GPS_System.getLongitude());
            SendThis.put("geoAltitude", GPS_System.getAltitude());
        } catch (JSONException e) {	e.printStackTrace();}

        return Utilites.getUserHaveReadNFCJSON_ZAGLUSHKA(SendThis);
    }

    @Override
    protected void onPostExecute(String result) {
// обработка ответа сервера на сохранение считывания метки участником.
        try {
            JSONObject JOAnswer = new JSONObject(result);
            if (Utilites.chkKey((String) JOAnswer.get("key"))) {
                if(JOAnswer.get("Status").equals("TRUE")) {  // TRUE|FALSE
// пишем, что ок. и помещаем регистрационные данные в монитор в админа c датой с сервера.
                    String regRecord ="Зарегистрировано: " + JOAnswer.get("Date") + ", " +JOAnswer.get("user") +", "+ JOAnswer.get("Mark");
                    AUMC.User_Monitor.append(regRecord);
                }
                else {
                    AUMC.User_Monitor.append(JOAnswer.get("Error").toString());
                }

            }
        }
        catch (JSONException e) {	e.printStackTrace();}
    }
}


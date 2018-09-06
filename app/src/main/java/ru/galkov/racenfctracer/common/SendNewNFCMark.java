package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.adminLib.ActivityNFCMarksRedactor;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class SendNewNFCMark extends AsyncTask<String, Void, String> {

    private String SERVER_URL = MainActivity.SERVER_URL;
    private String login;
    private String mark;
    private String admin;
    private ActivityNFCMarksRedactor.ActivityNFCMarksRedactorController ANFCMRC;

    public SendNewNFCMark(ActivityNFCMarksRedactor.ActivityNFCMarksRedactorController ANFCMRC1) {
        ANFCMRC = ANFCMRC1;
    }

    public void setLogin(String login1) {
        this.login = login1;
    }


    public void setMark(String mark1) {
        this.mark = mark1;
    }

    // должно неявно устанавливаться из имени заеганого
    public void setAdmin(String admin1) {
        this.admin = admin1;
    }

    @Override
    protected String doInBackground(String... strings) {
        // отправляем серверу новую NFC метку и получаем ответ

        JSONObject SendThis = new JSONObject();
        try {
            SendThis.put("Mark",mark);
            SendThis.put("admin",admin);
            SendThis.put("key",KEY);
        } catch (JSONException e) {	e.printStackTrace();}

        return Utilites.getNewNFCMarkResultJSON_ZAGLUSHKA(SendThis);
    }

    @Override
    protected void onPostExecute(String result) {
// обработка ответа сервера о сохранении новой метки
        try {
            JSONObject JOAnswer = new JSONObject(result);
            if (Utilites.chkKey((String) JOAnswer.get("key"))) {
                if(JOAnswer.get("Status").equals("TRUE")) {  // TRUE|FALSE
// пишем, что ок. и помещаем регистрационные данные в монитор в админа
                    String regRecord ="Зарегистрировано: " + JOAnswer.get("Mark");
                    ANFCMRC.NFC_ConfigurationLog.append(regRecord);
                }
                else {
                    ANFCMRC.NFC_ConfigurationLog.append(JOAnswer.get("Error").toString());
                }

            }
        }
        catch (JSONException e) {	e.printStackTrace();}
    }
}

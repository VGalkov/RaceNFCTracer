package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.adminLib.ActivityNFCMarksRedactor;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskMarksList extends AsyncTask<String, Void, String> {



    private final String SERVER_URL = MainActivity.SERVER_URL + "/ActivityUserManager/";
    private ActivityNFCMarksRedactor.ActivityNFCMarksRedactorController ANFCMRC;
    private String admin;

    public void setAdmin(String admin1) {
        this.admin = admin1;
    }

    public AskMarksList(ActivityNFCMarksRedactor.ActivityNFCMarksRedactorController ANFCMRC1) {
        ANFCMRC = ANFCMRC1;

    }


    @Override
    protected String doInBackground(String... strings) {
// список NFC меток на сервере.

        JSONObject SendThis = new JSONObject();
        try {
            SendThis.put("Marklist","TRUE");
            SendThis.put("admin",admin);
            SendThis.put("key",KEY);
        } catch (JSONException e) {	e.printStackTrace();}

        return Utilites.getNFC_MarksListJSON_ZAGLUSHKA(SendThis.toString());
    }

    @Override
    protected void onPostExecute(String result) {
// отображение меток
        ANFCMRC.NFC_ConfigurationLog.setText(result);
    }



}

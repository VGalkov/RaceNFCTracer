package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskMarksList extends AsyncTask<String, Void, String> {



    private final String SERVER_URL = MainActivity.SERVER_URL + "/ActivityUserManager/";
//    private ActivityNFCMarksRedactor.ActivityNFCMarksRedactorController ANFCMRC;
    private String admin;
    private TextView Ekran;

    public void setAdmin(String admin1) {
        this.admin = admin1;
    }

    public AskMarksList(TextView Ekran1) {
        this.Ekran = Ekran1;

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
        // почему-то включение этого вызывает ошибку.
      //  Ekran.setText("вот");
    }

}

package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskMarksList extends AsyncTask<String, Void, String> {



    private final String SERVER_URL = MainActivity.SERVER_URL + "/ActivityUserManager/";
//    private ActivityNFCMarksRedactor.ActivityNFCMarksRedactorController ANFCMRC;
    private String admin;
    private TextView Ekran;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;
    private Context activity;

    public void setAdmin(String admin1) {
        this.admin = admin1;
    }

    public AskMarksList(TextView Ekran1) {
        this.Ekran = Ekran1;

    }

    public  void setContext(Context c1) {
        activity = c1;
    }

    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;
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
        String str = "\n";
        try {
            JSONArray arr = new JSONArray(result);
            for(int i = 0 ; i< arr.length() ; i++) {
                JSONObject obj = arr.getJSONObject(i);
                str = str + "Метка(зарегистрирована на сервере):" + obj.getString("mark") + "\n";
            }
        } catch (JSONException e) {	e.printStackTrace();}

        if (method == MainActivity.writeMethod.Append) Ekran.append(str);
           else Ekran.setText(str);

    }

}

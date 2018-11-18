package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity.fieldsJSON;
import ru.galkov.racenfctracer.MainActivity.writeMethod;

import static ru.galkov.racenfctracer.MainActivity.DECIMAL_FORMAT;
import static ru.galkov.racenfctracer.MainActivity.KEY;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;
import static ru.galkov.racenfctracer.common.Utilites.chkKey;

public class AskMarksList extends AsyncTask<String, Void, String> {


    private TextView Ekran;
    private writeMethod method = writeMethod.Set;
    private final String ASKER = "AskMarksList";
    private JSONObject outBoundJSON;

    public AskMarksList(TextView Ekran1) {
        this.Ekran = Ekran1;
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
        StringBuffer response = new StringBuffer();

        try {
            JSONObject JOAnswer = new JSONObject(result);
            String serverKEY = JOAnswer.getString(fieldsJSON.key.toString());
            if (chkKey(serverKEY)) {
                JSONArray arr = JOAnswer.getJSONArray(fieldsJSON.rows.toString());

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    response.append("Метка(сервер):").append(obj.getString(fieldsJSON.label.toString())).
                            append("(").
                            append(DECIMAL_FORMAT.format(obj.getDouble(fieldsJSON.altitude.toString()))).append(",").
                            append(DECIMAL_FORMAT.format(obj.getDouble(fieldsJSON.latitude.toString()))).append(",").
                            append(DECIMAL_FORMAT.format(obj.getDouble(fieldsJSON.longitude.toString()))).
                            append(")\n");
                }
            } else {
                response = new StringBuffer("Ошибка ключа или версии клиента!");
            }
            } catch(JSONException e){       e.printStackTrace();      }

        if (method == writeMethod.Append) Ekran.append(response.toString());
           else Ekran.setText(response.toString());

    }


    // ==================================================================================

    private  void makeOutBoundJSON(){
        // {"asker":"AskMarksList", "key":"galkovvladimirandreevich"}

        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(fieldsJSON.asker.toString(),ASKER);
            outBoundJSON.put(fieldsJSON.key.toString(),KEY);
            outBoundJSON.put(fieldsJSON.exec_login.toString(), getLogin());
            outBoundJSON.put(fieldsJSON.exec_level.toString(), getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setMethod(writeMethod method1) {
        method = method1;
    }
}

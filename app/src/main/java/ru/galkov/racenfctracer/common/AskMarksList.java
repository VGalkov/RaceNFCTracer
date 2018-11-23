package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;
import org.json.*;
import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.MainActivity.writeMethod;
import static ru.galkov.racenfctracer.MainActivity.DECIMAL_FORMAT;
import static ru.galkov.racenfctracer.MainActivity.KEY;
import static ru.galkov.racenfctracer.common.Utilites.chkKey;

import ru.galkov.racenfctracer.MainActivity.fieldsJSON;

public class AskMarksList extends AsyncTask<String, Void, String> {


    private TextView Ekran;
    private writeMethod method = writeMethod.Set;
    private final String ASKER = "AskMarksList";
    private JSONObject outBoundJSON;

    public AskMarksList(TextView Ekran1) {
        this.Ekran = Ekran1;
    }

    private void Close() {
        // защита от утечки памяти.
        Ekran = null;

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
                    response.append("Метка(сервер):" + obj.getString(fieldsJSON.label.toString())
                            + "(" + DECIMAL_FORMAT.format(obj.getDouble(fieldsJSON.altitude.toString()))
                            + ";" + DECIMAL_FORMAT.format(obj.getDouble(fieldsJSON.latitude.toString()))
                            + ";" + DECIMAL_FORMAT.format(obj.getDouble(fieldsJSON.longitude.toString())) + ")" + "\n");
                }
            } else {
                response = new StringBuffer("Ошибка ключа или версии клиента!");
            }
            } catch(JSONException e){       e.printStackTrace();      }

        if (method == writeMethod.Append) Ekran.append(response.toString());
           else Ekran.setText(response.toString());
        Close();

    }


    // ==================================================================================

    private  void makeOutBoundJSON(){
        // {"asker":"AskMarksList", "key":"galkovvladimirandreevich"}

        try {
            outBoundJSON = new JSONObject()
                    .put(fieldsJSON.asker.toString(),ASKER)
                    .put(fieldsJSON.key.toString(),KEY)
                    .put(fieldsJSON.exec_login.toString(),MainActivity.getLogin())
                    .put(fieldsJSON.exec_level.toString(),MainActivity.getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;
    }
}

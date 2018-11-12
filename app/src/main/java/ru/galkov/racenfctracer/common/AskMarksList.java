package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import ru.galkov.racenfctracer.MainActivity;
import static ru.galkov.racenfctracer.MainActivity.DECIMAL_FORMAT;
import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskMarksList extends AsyncTask<String, Void, String> {


    private TextView Ekran;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;
    private final String ASKER = "AskMarksList";
    private JSONObject outBoundJSON;
    private DecimalFormat df = DECIMAL_FORMAT;

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
            String serverKEY = JOAnswer.getString(MainActivity.fieldsJSON.key.toString());
            if (Utilites.chkKey(serverKEY)) {
                JSONArray arr = JOAnswer.getJSONArray(MainActivity.fieldsJSON.rows.toString());

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    response.append("Метка(сервер):" + obj.getString(MainActivity.fieldsJSON.label.toString())
                            + "(" + df.format(obj.getDouble(MainActivity.fieldsJSON.altitude.toString()))
                            + ";" + df.format(obj.getDouble(MainActivity.fieldsJSON.latitude.toString()))
                            + ";" + df.format(obj.getDouble(MainActivity.fieldsJSON.longitude.toString())) + ")" + "\n");
                }
            } else {
                response = new StringBuffer("Ошибка ключа или версии клиента!");
                // Utilites.messager(context,"сбой протокола шифрования или всего запроса!");
            }
            } catch(JSONException e){       e.printStackTrace();      }

        if (method == MainActivity.writeMethod.Append) Ekran.append(response.toString());
           else Ekran.setText(response.toString());

    }


    // ==================================================================================

    private  void makeOutBoundJSON(){
        // {"asker":"AskMarksList", "key":"galkovvladimirandreevich"}

        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(MainActivity.fieldsJSON.asker.toString(),ASKER);
            outBoundJSON.put(MainActivity.fieldsJSON.key.toString(),KEY);
            outBoundJSON.put(MainActivity.fieldsJSON.exec_login.toString(),MainActivity.getLogin());
            outBoundJSON.put(MainActivity.fieldsJSON.exec_level.toString(),MainActivity.getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(outBoundJSON);
    }

    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;

    }
}

package ru.galkov.racenfctracer.common;

import android.content.Context;
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
    private Context activity;

    private final String ASKER = "AskMarksList";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    public DecimalFormat df = DECIMAL_FORMAT;



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
 /*
 {
    "asker":"AskMarksList",
    "rows":
        [
            {"Id":0,"mark":"?????? ?????"},
            {"Id":1,"mark":"??????"}
        ],
    "key":"galkovvladimirandreevich"}
 */
        StringBuffer response = new StringBuffer();
        // TODO проверка ключа
        try {
            JSONObject JOAnswer = new JSONObject(result);
            String serverKEY = JOAnswer.getString(f.key.toString());
            JSONArray arr = JOAnswer.getJSONArray(f.rows.toString());

            for(int i = 0 ; i< arr.length() ; i++) {
                JSONObject obj = arr.getJSONObject(i);
                response.append("Метка(сервер):" + obj.getString(f.label.toString())
                        + "("+df.format(obj.getDouble(f.altitude.toString()))
                        +","+df.format(obj.getDouble(f.latitude.toString()))
                        +","+df.format(obj.getDouble(f.longitude.toString()))+")" + "\n");
            }
        } catch (JSONException e) {	e.printStackTrace();}

        if (method == MainActivity.writeMethod.Append) Ekran.append(response.toString());
           else Ekran.setText(response.toString());

    }


    // ==================================================================================

    private  void makeOutBoundJSON(){
        // {"asker":"AskMarksList", "key":"galkovvladimirandreevich"}

        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);
            outBoundJSON.put(f.key.toString(),KEY);
            outBoundJSON.put(f.exec_login.toString(),MainActivity.getLogin());
            outBoundJSON.put(f.exec_level.toString(),MainActivity.getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public  void setContext(Context c1) {
        activity = c1;

    }

    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;

    }
}

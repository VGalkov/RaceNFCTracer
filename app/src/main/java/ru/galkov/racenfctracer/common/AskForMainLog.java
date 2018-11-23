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

public class AskForMainLog extends AsyncTask<String, Void, String> {

    private TextView ResultEkran;
    private writeMethod method = writeMethod.Set;
    private final String ASKER = "AskForMainLog";
    private String caller = "Unknown";
    private JSONObject outBoundJSON;

    public void setMethod(writeMethod method1) {
        method = method1;
    }

    public AskForMainLog(TextView ResultEkran1, String caller1) {
        this.ResultEkran = ResultEkran1;
        setCaller(caller1);
    }

    private void Close() {
        // защита от утечки памяти.
        ResultEkran = null;

    }


    private void setCaller(String caller) {
        this.caller = caller;
    }

    private  void makeOutBoundJSON(){
        // {"asker":"AskForMainLog","key":"galkovvladimirandreevich"}
        try {
            outBoundJSON = new JSONObject()
                    .put(fieldsJSON.asker.toString(),ASKER)
                    .put(fieldsJSON.key.toString(),KEY)
                    .put(fieldsJSON.caller.toString(),caller)
                    .put(fieldsJSON.exec_login.toString(),getLogin())
                    .put(fieldsJSON.exec_level.toString(),getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                StringBuffer sb = new StringBuffer("\n");
                if (chkKey(JOAnswer.getString(fieldsJSON.key.toString()))) {
                    JSONArray arr = JOAnswer.getJSONArray(fieldsJSON.rows.toString());
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        sb.append(obj.getString(fieldsJSON.login.toString())).append(" прошёл метку -> \n").
                                append(obj.getString(fieldsJSON.mark_label.toString())).
                                append("\n время: ").append(obj.getString(fieldsJSON.date.toString())).
                                append("\n на точке: ").append(DECIMAL_FORMAT.format(obj.getDouble(fieldsJSON.latitude.toString()))).
                                append(", ").append(DECIMAL_FORMAT.format(obj.getDouble(fieldsJSON.longitude.toString()))).
                                append(", ").append(DECIMAL_FORMAT.format(obj.getDouble(fieldsJSON.altitude.toString()))).
                                append(" \n\n");
                    }
                } else sb = new StringBuffer("Ошибка ключа или версии клиента!");

            if (method == writeMethod.Append)  ResultEkran.append(sb.toString());
            else                               ResultEkran.setText(sb.toString());
        } catch (JSONException e) {	e.printStackTrace();}
        Close();
    }
}


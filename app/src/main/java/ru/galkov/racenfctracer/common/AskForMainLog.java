package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import org.json.*;
import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.MainActivity.writeMethod;
import static ru.galkov.racenfctracer.MainActivity.DECIMAL_FORMAT;
import static ru.galkov.racenfctracer.MainActivity.KEY;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;
import static ru.galkov.racenfctracer.common.Utilites.chkKey;
import ru.galkov.racenfctracer.MainActivity.fieldsJSON;

public class AskForMainLog extends AsyncTask<String, Void, String> {

    private TextView ResultEkran;
    private Context activity;
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

    public void setActivity(Context context1) {
        activity = context1;
    }

    private void setCaller(String caller) {
        this.caller = caller;
    }

    private  void makeOutBoundJSON(){
        // {"asker":"AskForMainLog","key":"galkovvladimirandreevich"}
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(fieldsJSON.asker.toString(),ASKER);
            outBoundJSON.put(fieldsJSON.key.toString(),KEY);
            outBoundJSON.put(fieldsJSON.caller.toString(),caller);
            outBoundJSON.put(fieldsJSON.exec_login.toString(),getLogin());
            outBoundJSON.put(fieldsJSON.exec_level.toString(),getLevel());

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
                String str = "\n";
                if (chkKey(JOAnswer.getString(fieldsJSON.key.toString()))) {
                    JSONArray arr = JOAnswer.getJSONArray(fieldsJSON.rows.toString());
                    //TODO переписать в StringBuffer
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        str = str + obj.getString(fieldsJSON.login.toString()) + " прошёл метку -> \n"
                                + obj.getString(fieldsJSON.mark_label.toString()) + "\n"
                                + "время: " + obj.getString(fieldsJSON.date.toString()) + "\n"
                                + "на точке: " + DECIMAL_FORMAT.format(obj.getDouble(fieldsJSON.latitude.toString()))
                                + ", " + DECIMAL_FORMAT.format(obj.getDouble(fieldsJSON.longitude.toString()))
                                + ", " + DECIMAL_FORMAT.format(obj.getDouble(fieldsJSON.altitude.toString())) + " \n\n";
                    }
                } else str = "Ошибка ключа или версии клиента!";

            if (method == writeMethod.Append)  ResultEkran.append(str);
            else                                            ResultEkran.setText(str);
        } catch (JSONException e) {	e.printStackTrace();}
    }
}


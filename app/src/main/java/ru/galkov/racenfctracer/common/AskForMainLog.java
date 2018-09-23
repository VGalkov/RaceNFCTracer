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

public class AskForMainLog extends AsyncTask<String, Void, String> {

    private String URL_Extention;
    private TextView ResultEkran;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;
    // private SimpleDateFormat formatForDate = MainActivity.formatForDate;
    private Context activity;

    private final String ASKER = "AskForMainLog";
    private String caller = "Unknown";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    public DecimalFormat df = DECIMAL_FORMAT;
    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;
    }

    public AskForMainLog(TextView ResultEkran1) {
        this.ResultEkran = ResultEkran1;

    }

    public AskForMainLog(TextView ResultEkran1, String caller1) {
        this.ResultEkran = ResultEkran1;
        setCaller(caller1);
    }


    public void setActivity(Context context1) {
        activity = context1;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    private  void makeOutBoundJSON(){
        // {"asker":"AskForMainLog","key":"galkovvladimirandreevich"}
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);
            outBoundJSON.put(f.key.toString(),KEY);
            outBoundJSON.put(f.caller.toString(),caller);
            outBoundJSON.put(f.exec_login.toString(),MainActivity.getLogin());
            outBoundJSON.put(f.exec_level.toString(),MainActivity.getLevel());

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

// =====================================================================

        String str = "\n";
        System.out.print(result);
        try {
                JSONObject JOAnswer = new JSONObject(result);
                String serverKEY = JOAnswer.getString("key");
                if (Utilites.chkKey(serverKEY)) {
                    JSONArray arr = JOAnswer.getJSONArray("rows");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        str = str + obj.getString("login") + " прошёл метку -> \n"
                                + obj.getString("mark_label") + "\n"
                                + "время: " + obj.getString("date") + "\n"
                                + "на точке: " + df.format(obj.getDouble("latitude"))
                                + ", " + df.format(obj.getDouble("longitude"))
                                + ", " + df.format(obj.getDouble("altitude")) + " \n\n";
                    }
                } else str = "Ошибка ключа или версии клиента!";

            if (method == MainActivity.writeMethod.Append)  ResultEkran.append(str);
            else                                            ResultEkran.setText(str);
        } catch (JSONException e) {	e.printStackTrace();}
    }
}


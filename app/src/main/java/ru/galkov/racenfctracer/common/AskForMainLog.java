package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskForMainLog extends AsyncTask<String, Void, String> {

    private String URL_Extention;
    private TextView ResultEkran;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;
    // private SimpleDateFormat formatForDate = MainActivity.formatForDate;
    private Context activity;

    private final String ASKER = "AskForMainLog";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;

    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;
    }

    public AskForMainLog(TextView ResultEkran1) {
        this.ResultEkran = ResultEkran1;
    }

    public void setActivity(Context context1) {
        activity = context1;
    }

    private  void makeOutBoundJSON(){
        // {"asker":"AskForMainLog","key":"galkovvladimirandreevich"}
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
                // TODO проверка ключа
                String serverKEY = JOAnswer.getString("key");
                JSONArray arr = JOAnswer.getJSONArray("rows");
                for(int i = 0 ; i< arr.length() ; i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    str = str + obj.getString("login") + " прошёл метку -> \n"
                            + obj.getString("mark_label") + "\n"
                            + "время: " + obj.getString("date") + "\n"
                            + "на точке: "+obj.getString("latitude")+", "+obj.getString("longitude")+", "+obj.getString("altitude")+" \n\n";
                }

            if (method == MainActivity.writeMethod.Append)  ResultEkran.append(str);
            else                                            ResultEkran.setText(str);
        } catch (JSONException e) {	e.printStackTrace();}
    }
}


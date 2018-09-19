package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskResultsTable extends AsyncTask<String, Void, String> {


    private TextView userLogger;
    private final String ASKER = "AskResultsTable";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;
    private MainActivity.fileType fileType;

// TODO полностью переписать смысл.

    public AskResultsTable(TextView userLogger1,MainActivity.fileType fileType2 ) {
        this.userLogger = userLogger1;
        this.fileType = fileType2;
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
// TODO переписать.

        String str = "\n";
        try {
            JSONObject JOAnswer = new JSONObject(result);
            // TODO проверка ключа
            String serverKEY = JOAnswer.getString(f.key.toString());
            JSONArray arr = JOAnswer.getJSONArray(f.rows.toString());
            for(int i = 0 ; i< arr.length() ; i++) {
                JSONObject obj = arr.getJSONObject(i);
                str = str + obj.getString("login") + " прошёл метку -> \n" + obj.getString("mark") + "\n время: " + obj.getString("date") + "\n" +"на точке: "+obj.getString("latitude")+", "+obj.getString("longitude")+", "+obj.getString("altitude")+" \n\n";
            }

            if (method == MainActivity.writeMethod.Append) {
                userLogger.append(str);
            }
            else {
                userLogger.setText(str);
            }
        } catch (JSONException e) {	e.printStackTrace();}

    }



    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;

    }

    private  void makeOutBoundJSON(){
        //     {"asker":"AskResultsTable","key":"galkovvladimirandreevich"}
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



}

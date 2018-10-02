package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskMasterMark extends AsyncTask<String, Void, String> {

    private TextView master_mark;
    private final String ASKER = "AskMasterMark";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;

    public AskMasterMark(TextView master_mark1)     {
        master_mark = master_mark1;
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
            String serverKEY = JOAnswer.getString(f.key.toString());
            if (Utilites.chkKey(serverKEY)) {
                    MainActivity.setmASTER_MARK(JOAnswer.getString(f.mark_label.toString()));
                    master_mark.setText("Эталонная метка загружена: : " + MainActivity.getmASTER_MARK());
                }
            } catch (JSONException e) {	e.printStackTrace();}
        }



    void makeOutBoundJSON() {
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);
            outBoundJSON.put(f.key.toString(),KEY);
            outBoundJSON.put(f.exec_login.toString(),MainActivity.getLogin());
            outBoundJSON.put(f.exec_level.toString(),MainActivity.getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
            outBoundJSON = Utilites.ErrorJSON();
        }
    }

}

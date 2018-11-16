package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;
import org.json.*;
import ru.galkov.racenfctracer.MainActivity;
import static ru.galkov.racenfctracer.MainActivity.KEY;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;
import static ru.galkov.racenfctracer.MainActivity.getmASTER_MARK;
import static ru.galkov.racenfctracer.MainActivity.setmASTER_MARK;
import static ru.galkov.racenfctracer.common.Utilites.ErrorJSON;
import static ru.galkov.racenfctracer.common.Utilites.chkKey;

import ru.galkov.racenfctracer.MainActivity.fieldsJSON;

public class AskMasterMark extends AsyncTask<String, Void, String> {

    private TextView master_mark;
    private final String ASKER = "AskMasterMark";
    private JSONObject outBoundJSON;

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
        try {
            JSONObject JOAnswer = new JSONObject(result);
            if (chkKey(JOAnswer.getString(fieldsJSON.key.toString()))) {
                    setmASTER_MARK(JOAnswer.getString(fieldsJSON.mark_label.toString()));
                    String str = "Эталонная метка загружена: : " + getmASTER_MARK();
                    master_mark.setText(str);
                }
            } catch (JSONException e) {	e.printStackTrace();}
        }



    void makeOutBoundJSON() {
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(fieldsJSON.asker.toString(),ASKER);
            outBoundJSON.put(fieldsJSON.key.toString(),KEY);
            outBoundJSON.put(fieldsJSON.exec_login.toString(),getLogin());
            outBoundJSON.put(fieldsJSON.exec_level.toString(),getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
            outBoundJSON = ErrorJSON();
        }
    }

}

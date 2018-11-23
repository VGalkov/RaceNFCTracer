package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity.fieldsJSON;

import static ru.galkov.racenfctracer.MainActivity.KEY;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;
import static ru.galkov.racenfctracer.common.Utilites.chkKey;

public class AskUserTable extends AsyncTask<String, Void, String> {

    private Context activityContext;
    public Spinner spinnerUsers;
    private final String ASKER = "AskUserTable";
    private JSONObject outBoundJSON;

    private void Close() {
        // защита от утечки памяти.
        spinnerUsers = null;
        activityContext = null;
    }

    public void setActivityContext(Context activityContext1){
        activityContext = activityContext1;
    }

    public AskUserTable(Spinner spinnerUsers1) {
        spinnerUsers = spinnerUsers1;
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
                if (chkKey((String) JOAnswer.get(fieldsJSON.key.toString()))) {
                    JSONArray arr = JOAnswer.getJSONArray(fieldsJSON.usersArr.toString());
                    String[] userList = new String[arr.length()];

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj1 = arr.getJSONObject(i);
                        userList[i] = obj1.get(fieldsJSON.login.toString()) + "(" + obj1.get(fieldsJSON.level.toString()) + ")";
                    }


                    ArrayAdapter<String> adapterUsers = new ArrayAdapter<String>(activityContext, android.R.layout.simple_spinner_item, userList);
                    spinnerUsers.setAdapter(adapterUsers);
                }
        } catch (JSONException e) {	e.printStackTrace();}
        Close();
    }

    private  void makeOutBoundJSON(){
//         {"asker":"AskUserTable", "key":"galkovvladimirandreevich"}
        try {
            outBoundJSON = new JSONObject()
                    .put(fieldsJSON.asker.toString(),ASKER)
                    .put(fieldsJSON.key.toString(),KEY)
                    .put(fieldsJSON.exec_login.toString(), getLogin())
                    .put(fieldsJSON.exec_level.toString(), getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

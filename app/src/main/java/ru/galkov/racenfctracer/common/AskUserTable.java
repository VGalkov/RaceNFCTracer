package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskUserTable extends AsyncTask<String, Void, String> {

    private Context activityContext;
    public Spinner spinnerUsers;
    private final String ASKER = "AskUserTable";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;

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

/*
        {"usersArr":
            [
                {"password":"2222222","level":"User","Id":8,"login":"+79272000001"},
                {"password":"1111111","level":"Admin","Id":9,"login":"+79272006026"},
                {"password":"dggsshshgss","level":"Guest","Id":11,"login":"+79424557585"},
                {"password":"cfccccccc","level":"Guest","Id":22,"login":"888888888888"},
            ],
            "asker":"AskUserTable",
            "key":"galkovvladimirandreevich"}
 */
        try {
                JSONObject JOAnswer = new JSONObject(result);
                String serverKEY = JOAnswer.getString(f.key.toString());
                JSONArray arr = JOAnswer.getJSONArray(f.usersArr.toString());
                String[] userList = new String[arr.length()];

                for(int i = 0 ; i< arr.length() ; i++) {
                    JSONObject obj1 = arr.getJSONObject(i);
//                    userList[i] = obj1.get(f.login.toString()) + "("+ obj1.get(f.level.toString() +")");
                    userList[i] = obj1.get(f.login.toString()) + "("+ obj1.get(f.level.toString()) +")";
                }


                ArrayAdapter<String> adapterUsers = new ArrayAdapter<String>(activityContext,  android.R.layout.simple_spinner_item, userList);
                spinnerUsers.setAdapter(adapterUsers);

        } catch (JSONException e) {	e.printStackTrace();}
    }

    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;
    }

    private  void makeOutBoundJSON(){
//         {"asker":"AskUserTable", "key":"galkovvladimirandreevich"}
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);
            outBoundJSON.put(f.key.toString(),KEY);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

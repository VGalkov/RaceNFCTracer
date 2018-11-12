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

        try {
                JSONObject JOAnswer = new JSONObject(result);
                String serverKEY = JOAnswer.getString(MainActivity.fieldsJSON.key.toString());
                JSONArray arr = JOAnswer.getJSONArray(MainActivity.fieldsJSON.usersArr.toString());
                String[] userList = new String[arr.length()];

                for(int i = 0 ; i< arr.length() ; i++) {
                    JSONObject obj1 = arr.getJSONObject(i);
//                    userList[i] = obj1.get(f.login.toString()) + "("+ obj1.get(f.level.toString() +")");
                    userList[i] = obj1.get(MainActivity.fieldsJSON.login.toString()) + "("+ obj1.get(MainActivity.fieldsJSON.level.toString()) +")";
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
            outBoundJSON.put(MainActivity.fieldsJSON.asker.toString(),ASKER);
            outBoundJSON.put(MainActivity.fieldsJSON.key.toString(),KEY);
            outBoundJSON.put(MainActivity.fieldsJSON.exec_login.toString(),MainActivity.getLogin());
            outBoundJSON.put(MainActivity.fieldsJSON.exec_level.toString(),MainActivity.getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

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
//        {"usersArr":[{"level":"Admin","login":"+79000000000"},{"level":"Guest","login":"490000000000"}],"asker":"AskUserTable","key":"galkovvladimirandreevich"}
        // TODO rewrite
        try {
                JSONObject obj = new  JSONObject(result);
                JSONArray arr = obj.getJSONArray(f.usersArr.toString());

                String[] userList = new String[arr.length()];

                for(int i = 0 ; i< arr.length() ; i++) {
                    JSONObject obj1 = arr.getJSONObject(i);
                    userList[i] = obj1.get(f.login.toString()) + "("+ obj1.get(f.level.toString() +")");
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

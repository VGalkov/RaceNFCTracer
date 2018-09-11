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

    private  void makeOutBoundJSON(){
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);

            outBoundJSON.put(f.key.toString(),KEY);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
//  список полученных пользователей кидаем в выпадающий спсиок

        try {
            JSONArray arr = new JSONArray(result);
            String[] userList = new String[arr.length()];

            for(int i = 0 ; i< arr.length() ; i++) {
                JSONObject obj = arr.getJSONObject(i);
                userList[i] = obj.get("user") + "("+ obj.get("level") +")";
            }

            ArrayAdapter<String> adapterUsers = new ArrayAdapter<String>(activityContext,  android.R.layout.simple_spinner_item, userList);
            spinnerUsers.setAdapter(adapterUsers);

        } catch (JSONException e) {	e.printStackTrace();}
    }

}

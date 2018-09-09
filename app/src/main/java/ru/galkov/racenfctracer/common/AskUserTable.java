package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

public class AskUserTable extends AsyncTask<String, Void, String> {

    private final String SERVER_URL = MainActivity.SERVER_URL + "/ActivityGuestManager/";
    private Context activityContext;
    public Spinner spinnerUsers;

    public void setActivityContext(Context activityContext1){
        activityContext = activityContext1;
    }

    public AskUserTable(Spinner spinnerUsers1) {
        spinnerUsers = spinnerUsers1;
    }


    @Override
    protected String doInBackground(String... strings) {
// запрос полной таблицы пользователей для пользовательского интерфейса раздачи уровня доступа


        return Utilites.getUserListJSON_ZAGLUSHKA("");
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

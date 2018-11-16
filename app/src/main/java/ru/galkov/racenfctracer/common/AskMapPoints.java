package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.mapview.MapView;
import org.json.*;
import static ru.galkov.racenfctracer.MainActivity.KEY;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;
import static ru.galkov.racenfctracer.common.Utilites.chkKey;

import ru.galkov.racenfctracer.MainActivity.fieldsJSON;

public class AskMapPoints extends AsyncTask<String, Void, String> {

    private Context activityContext;
    private MapView mapView;
    private final String ASKER = "AskMapPoints";
    private JSONObject outBoundJSON;

    public void setMapView(MapView mapview1) {
        mapView = mapview1;
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

    /*
    type = mark,user,admin,guest
    {
        "rows":[
        {"longitude":0.0,"altitude":0.0,"latitude":0.0,"type":"","label":"1"},
        {"longitude":0.0,"altitude":0.0,"latitude":0.0,"type":"","label":"1"},
        "asker":"AskStartSructure",
        "key":"galkovvladimirandreevich",
        "exec_login":"",
        "exec_level":""
    } */


    @Override
    protected void onPostExecute(String result) {

        try {
            JSONObject JOAnswer = new JSONObject(result);

            JSONArray arr = JOAnswer.getJSONArray(fieldsJSON.rows.toString());
            if (chkKey(JOAnswer.getString(fieldsJSON.key.toString()))) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Double latitude = obj.getDouble(fieldsJSON.latitude.toString());
                Double longitude = obj.getDouble(fieldsJSON.longitude.toString());
                Point point = new Point(latitude,longitude);
                mapView.getMap().getMapObjects().addPlacemark(point);
            }}
        } catch (JSONException e) {	e.printStackTrace();}
    }

    private  void makeOutBoundJSON(){
//         {"asker":"AskUserTable", "key":"galkovvladimirandreevich"}
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(fieldsJSON.asker.toString(),ASKER);
            outBoundJSON.put(fieldsJSON.key.toString(),KEY);
            outBoundJSON.put(fieldsJSON.exec_login.toString(),getLogin());
            outBoundJSON.put(fieldsJSON.exec_level.toString(),getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

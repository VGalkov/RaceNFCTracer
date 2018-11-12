package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;

import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;
import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskMapPoints extends AsyncTask<String, Void, String> {
// https://tech.yandex.ru/maps/?lang=ru-RU
    private Context activityContext;
    private final String ASKER = "AskMapPoints";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;
    private MapView mapView;

/*
    public AskMapPoints() {

    }
*/
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
            String serverKEY = JOAnswer.getString(f.key.toString());
            JSONArray arr = JOAnswer.getJSONArray(f.rows.toString());

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Double latitude = obj.getDouble(f.latitude.toString());
                Double longitude = obj.getDouble(f.longitude.toString());
                Point point = new Point(latitude,longitude);
                mapView.getMap().getMapObjects().addPlacemark(point);
            }
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
            outBoundJSON.put(f.exec_login.toString(),MainActivity.getLogin());
            outBoundJSON.put(f.exec_level.toString(),MainActivity.getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

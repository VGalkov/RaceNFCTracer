package ru.galkov.racenfctracer.common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class Utilites {

    public static boolean chkKey(String key1) {
        if (key1.equals(KEY)) 	{ return true; }
        else 					{ return false; }
    }


    public static String ErrorJSON() {
        return "{\"Error\" : \"ошибка в построении JSON!\"}";
    }


    public static String getUserLogin_ZAGLUSHKA() {
        // json with result тут заглушка
        // view - {level":"Admin","login":"TRUE|FALSE","key":"sgfsdfg"}

            JSONObject loginAnswer = new JSONObject();
            try {
                loginAnswer.put("login","TRUE");
                loginAnswer.put("level", "Admin");
                loginAnswer.put("key",KEY);
            } catch (JSONException e) {	e.printStackTrace();}


            return loginAnswer.toString();
    }

    public static String getMainLogJSON_ZAGLUSHKA() {

        JSONArray arr = new JSONArray();

        // ЗАГЛУШКА!!!!!!!!!!!!!!
        // это делалка JSON-а как с сервера*/
        Date Dt2 = new Date();
        try {
            for(int i = 0 ; i< 10 ; i++) {
                JSONObject obj = new JSONObject();

                obj.put("Id", i);
                obj.put("user", "+79272006026");
                obj.put("key", "sgfsdfg");
                obj.put("Mark", "1234567890");
                obj.put("gpsX", "50.21341243");
                obj.put("gpsY", "50.21341243");
                obj.put("gpsAlt", "50.21341243");
                obj.put("Date", Dt2);

                arr.put(obj);
            }

        }
        catch (JSONException e) {	e.printStackTrace();}

        return arr.toString();
    }
}

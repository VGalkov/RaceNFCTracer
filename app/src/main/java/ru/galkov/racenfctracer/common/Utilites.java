package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.widget.Toast;

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
//кот запросов в JSON_Examples1.java на гитхабе и в BackendController в серверном коде.

    public static String getNFC_MarksListJSON_ZAGLUSHKA() {
        // возвращает список NFC меток с сервера.

        return  getUserLogin_ZAGLUSHKA();
    }


    public static String getUsersResultsJSON_ZAGLUSHKA() {
        // возвращает результаты участников, интерпратация запроса к main_log

        return  getUserLogin_ZAGLUSHKA();
    }


    public static String getUserListJSON_ZAGLUSHKA() {
        // возвращает список пользователей и их прав с сервера

        return  getUserLogin_ZAGLUSHKA();
    }


    public static String getUserHaveReadNFCJSON_ZAGLUSHKA() {
        // возвращает ответ сервера на считывание метки пользователем

        return  "";
    }

    public static String getNewNFCMarkResultJSON_ZAGLUSHKA() {
        // возвращает ответ сервера на сохранение новой NFC метки там

        return  "";
    }

    public static String getLoginLevelChangingJSON_ZAGLUSHKA() {
        // возвращает ответ сервера на смену уровня доступа логина.

        return  "";
    }



    public static String ErrorJSON() {
        return "{\"Error\" : \"ошибка в построении JSON!\"}";
    }

    public static void messager(Context cont1, String str2) {
        Toast.makeText(cont1, str2, Toast.LENGTH_LONG).show();
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

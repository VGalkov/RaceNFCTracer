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

    public static String getNFC_MarksListJSON_ZAGLUSHKA(String str1) {
        // возвращает список NFC меток с сервера.
        JSONArray arr = new JSONArray();

        Date Dt2 = new Date();
        try {
            for(int i = 0 ; i< 10 ; i++) {
                JSONObject obj = new JSONObject();

                obj.put("key", "sgfsdfg");
                obj.put("Mark", "1234567890");
                arr.put(obj);
            }
        }
        catch (JSONException e) {	e.printStackTrace();}

        return arr.toString();
    }



    public static String getServerTimeJSON_ZAGLUSHKA(String obj1) {
        // возвращает результаты участников, интерпратация запроса к main_log
        Date Dt2 = new Date();
        // ЗАГЛУШКА!!!!!!!!!!!!!!
        JSONObject obj = new JSONObject();
        try {
                obj.put("Status", "TRUE");
                obj.put("date", Dt2);
                obj.put("key", KEY);
        }
        catch (JSONException e) {	e.printStackTrace();}

        return  obj.toString();
    }

    public static String getUsersResultsJSON_ZAGLUSHKA() {
        // возвращает результаты участников, интерпратация запроса к main_log

        return  getMainLogJSON_ZAGLUSHKA();
    }


    public static String getUserListJSON_ZAGLUSHKA() {
        // возвращает список пользователей и их прав с сервера
        JSONArray arr = new JSONArray();

        // ЗАГЛУШКА!!!!!!!!!!!!!!
        try {
            for(int i = 0 ; i< 10 ; i++) {
                JSONObject obj = new JSONObject();
                obj.put("user", "+79272006026");
                obj.put("key", KEY);
                obj.put("level", "Guest");
                arr.put(obj);
            }

        }
        catch (JSONException e) {	e.printStackTrace();}

        return arr.toString();
    }


    public static String getUserHaveReadNFCJSON_ZAGLUSHKA(JSONObject obj1) {
        // возвращает ответ сервера на считывание метки пользователем
        JSONObject obj = new JSONObject();
        try{
            Date Dt2 = new Date();
            obj.put("user", "+79272006026");
            obj.put("key", "sgfsdfg");
            obj.put("Mark", "1234567890");
            obj.put("gpsX", "50.21341243");
            obj.put("gpsY", "50.21341243");
            obj.put("gpsAlt", "50.21341243");
            obj.put("Date", Dt2); // время серверное!
            obj.put("Status", "TRUE"); // TRUE|FALSE
        }
        catch (JSONException e) {	e.printStackTrace();}

        return  obj.toString();
    }

    public static String getNewNFCMarkResultJSON_ZAGLUSHKA(JSONObject obj) {
        // Mark, key, Admin
        // возвращает ответ сервера на сохранение новой NFC метки там
        JSONObject Answer = new JSONObject();
        try {
            Answer.put("Mark","????");
            Answer.put("Status", "TRUE"); // TRUE|FALSE
            Answer.put("key",KEY);
        } catch (JSONException e) {	e.printStackTrace();}


        return Answer.toString();
    }

    public static String getLoginLevelChangingJSON_ZAGLUSHKA(JSONObject obj) {
        // возвращает ответ сервера на смену уровня доступа логина. obj - это запрос на сервер
        // {login, user, level, key}
        JSONObject Answer = new JSONObject();
        try {
            Answer.put("login","TRUE");    // TRUE|FALSE
            Answer.put("key",KEY);
            Answer.put("Msg","описываем ошибку");
        } catch (JSONException e) {	e.printStackTrace();}


        return Answer.toString();
    }



    public static String ErrorJSON() {
        return "{\"Error\" : \"ошибка в построении JSON!\"}";
    }

    public static void messager(Context cont1, String str2) {
        Toast.makeText(cont1, str2, Toast.LENGTH_LONG).show();
    }

    public static String getUserLogin_ZAGLUSHKA(String toServer) {
        // json with result тут заглушка
        // view - {level":"Admin","login":"TRUE|FALSE","key":"sgfsdfg"}
        JSONObject loginAnswer = new JSONObject();

        try {
                JSONObject ServerJSON = new JSONObject(toServer);



                loginAnswer.put("login","TRUE");
//                loginAnswer.put("level", "Admin");
                loginAnswer.put("level", ServerJSON.get("level")); // это для дебага
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

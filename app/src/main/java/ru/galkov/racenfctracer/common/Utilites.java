package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static ru.galkov.racenfctracer.MainActivity.KEY;
// import static ru.galkov.racenfctracer.MainActivity.formatForDateNow;


public class Utilites {

   public static SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");

    public static boolean chkKey(String key1) {
        if (key1.equals(KEY)) 	{ return true; }
        else 					{ return false; }
    }

    public static String getNFC_MarksListJSON_ZAGLUSHKA(String str1) {
        // возвращает список NFC меток с сервера.
        JSONArray arr = new JSONArray();

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
                obj.put("date", formatForDateNow.format(Dt2));
                obj.put("key", KEY);
        }
        catch (JSONException e) {	e.printStackTrace();}

        return  obj.toString();
    }

    public static String getUsersResultsJSON_ZAGLUSHKA() {
        // возвращает результаты участников, интерпратация запроса к main_log

        return  getMainLogJSON_ZAGLUSHKA();
    }


    public static String getUserListJSON_ZAGLUSHKA(String json) {
        // возвращает список пользователей и их прав с сервера
        JSONArray arr = new JSONArray();


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


    public static String getUserHaveReadNFCJSON_ZAGLUSHKA(String MessageIN1) {
        // возвращает ответ сервера на считывание метки пользователем
        Date Dt2 = new Date();
        JSONObject obj = new JSONObject();
        try {
                JSONObject MessageIN = new JSONObject(MessageIN1);
                obj.put("date", formatForDateNow.format(Dt2)); // время серверное!
                obj.put("Status", "TRUE"); // TRUE|FALSE
                obj.put("Error", "Нет ошибок");
                obj.put("user", "+79272006026");
                obj.put("key", KEY);

                obj.put("geoLatitude", MessageIN.get("geoLatitude").toString());
                obj.put("geoLongitude", MessageIN.get("geoLongitude").toString());
                obj.put("geoAltitude", MessageIN.get("geoAltitude").toString());

                obj.put("mark", MessageIN.get("mark").toString());
        }
        catch (JSONException e) {	e.printStackTrace();}

        return  obj.toString();
    }

    public static String getNewNFCMarkResultJSON_ZAGLUSHKA(String obj1) {
        // Mark, key, Admin
        // возвращает ответ сервера на сохранение новой NFC метки там

        JSONObject Answer = new JSONObject();
        try {
            JSONObject obj = new JSONObject(obj1);
            Answer.put("Mark",obj.get("Mark")); // данны из запроса.
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



    public static String getONEUserLogin_ZAGLUSHKA(String toServer) {

        // в ServerJSON.get("login") сидит логин и уровень в скобках login(level)
        JSONObject loginAnswer = new JSONObject();
        try {
            JSONObject ServerJSON = new JSONObject(toServer);

            loginAnswer.put("login",ServerJSON.get("login"));
            loginAnswer.put("level", ServerJSON.get("level")); // это для дебага
            loginAnswer.put("key",KEY);
        } catch (JSONException e) {	e.printStackTrace();}


        return loginAnswer.toString();
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
                obj.put("mark", "1234567890");
                obj.put("gpsX", "50.2134");
                obj.put("gpsY", "50.2134");
                obj.put("gpsZ", "50.2143");
                obj.put("date", formatForDateNow.format(Dt2));

                arr.put(obj);
            }

        }
        catch (JSONException e) {	e.printStackTrace();}

        return arr.toString();
    }



}

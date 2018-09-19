package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;
// import static ru.galkov.racenfctracer.MainActivity.formatForDateNow;


public class Utilites {

    private MainActivity.fieldsJSON f;

    public static boolean chkKey(String key1) {
        if (key1.equals(KEY)) 	{ return true; }
        else 					{ return false; }
    }

    public static JSONObject ErrorJSON() {
        JSONObject errorJSON = new JSONObject();
        try {
            errorJSON.put("error","ошибка в построении JSON!" );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return errorJSON;
    }

    public static void messager(Context cont1, String str2) {
        Toast.makeText(cont1, str2, Toast.LENGTH_LONG).show();
    }




}

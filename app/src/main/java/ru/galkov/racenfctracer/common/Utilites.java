package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.widget.Toast;
import org.json.*;
import static ru.galkov.racenfctracer.MainActivity.KEY;


public class Utilites {


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

    public static String replace(String str, int index, char replace){
        if(str==null){
            return str;
        }else if(index<0 || index>=str.length()){
            return str;
        }
        char[] chars = str.toCharArray();
        chars[index] = replace;
        return String.valueOf(chars);
    }


}

package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.DECIMAL_FORMAT;
import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskRaceConfig  extends AsyncTask<String, Void, String> {

    private final String ASKER = "AskRaceConfig";
    private JSONObject outBoundJSON;
    private TableLayout tableLayout;
    private Context context;
    private TextView ekran;

    public AskRaceConfig(TableLayout tableLayout1, Context context2, TextView start_config3) {
        this.tableLayout =  tableLayout1;
        this.context = context2;
        this.ekran = start_config3;
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
     {
"start_time":"2018-10-14 03:00:00.0",
"usersArr":
		[
	{	"altitude":84,
		"registred_race_id":0,
		"level":"User",
		"latitude":53.20004181470722,
		"Id":97,
		"master_mark_label":"8487",
		"login":"+22222222222",
		"registred_start_id":0,
		"longitude":50.144251165911555
	}
		],
"stop_time":"2018-10-14 11:00:00.0",
"start_id":36,
"race_id":2,
"asker":"AskRaceConfig",
"counter":3,
"key":"galkovvladimirandreevich"
}

     */


    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject JOAnswer = new JSONObject(result);
            String serverKEY = JOAnswer.getString(MainActivity.fieldsJSON.key.toString());
            if (Utilites.chkKey(serverKEY)) {
                String str =    " Суммарно, конфигурация соревнования: \n Номер соревнования:" +
                                    JOAnswer.getString(MainActivity.fieldsJSON.race_id.toString()) +
                                "; номер старта в рамках соревнования " +
                                JOAnswer.getString(MainActivity.fieldsJSON.start_id.toString()) +
                                "; \n Диапазон времени старта(начало/конец) -  \n"+
                                JOAnswer.getString(MainActivity.fieldsJSON.start_time.toString()) + " - " +
                                JOAnswer.getString(MainActivity.fieldsJSON.stop_time.toString())  +
                                "\n \n На старт зарегистрировалось " +
                                JOAnswer.getString(MainActivity.fieldsJSON.counter.toString()) +
                                "участников. Ниже следует их полный список и состояние их регистрации.\n\n";

                ekran.setText(str);

                TableRow tableRow = new TableRow(context);
                tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                drawSell(tableRow, "Логин", 0, Color.GRAY);
                drawSell(tableRow, "Координаты", 1, Color.GRAY);
                drawSell(tableRow, "Мастерметка", 2, Color.GRAY);
                drawSell(tableRow, "Заезд", 3, Color.GRAY);

                tableLayout.addView(tableRow, 0);

                JSONArray arr = JOAnswer.getJSONArray(MainActivity.fieldsJSON.usersArr.toString());
                for(int i = 0 ; i< arr.length() ; i++) {
                    JSONObject obj1 = arr.getJSONObject(i);
                    TempUser usr = new TempUser(obj1);  // может и не надо это, а просто из jsonа сразу фигарить.

                    tableRow = new TableRow(context);
                    tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    drawSell(tableRow, usr.getLogin(), 0,Color.WHITE);
                    drawSell(tableRow, "(" + DECIMAL_FORMAT.format(usr.getLatitude()) + ", " + DECIMAL_FORMAT.format(usr.getLongitude())+", " + DECIMAL_FORMAT.format(usr.getAltitude()) + "); ", 1,Color.WHITE);
                    drawSell(tableRow, usr.getMaster_mark_label(), 2,Color.WHITE);
                    drawSell(tableRow, usr.getRegistred_race_id()+" / "+usr.getRegistred_start_id(), 3,Color.WHITE);

                    tableLayout.addView(tableRow, i+1);
                }


            }


            // http://developer.alexanderklimov.ru/android/layout/tablelayout.php

        } catch (JSONException e) {	e.printStackTrace();}
    }


    void drawSell(TableRow tableRow1, String str2, int id3, int color4) {

        TextView cell0 = new TextView(context);
        cell0.setText(str2);
        tableRow1.setBackgroundColor(color4);
        tableRow1.addView(cell0,id3);
    }

class TempUser {

        private String login;
        private String level;
        private String master_mark_label;
        private long registred_start_id;
        private long registred_race_id;
        private Double latitude;
        private Double altitude;
        private Double longitude;


    TempUser (JSONObject obj) {
        try {
            this.altitude = obj.getDouble(MainActivity.fieldsJSON.altitude.toString());
            this.latitude = obj.getDouble(MainActivity.fieldsJSON.latitude.toString());
            this.longitude = obj.getDouble(MainActivity.fieldsJSON.longitude.toString());
            this.registred_start_id = obj.getLong(MainActivity.fieldsJSON.registred_start_id.toString());
            this.registred_race_id = obj.getLong(MainActivity.fieldsJSON.registred_race_id.toString());
            this.master_mark_label = obj.getString(MainActivity.fieldsJSON.master_mark_label.toString());
            this.level = obj.getString(MainActivity.fieldsJSON.level.toString());
            this.login = obj.getString(MainActivity.fieldsJSON.login.toString());
        }
        catch (JSONException e) {	e.printStackTrace();}

    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMaster_mark_label() {
        return master_mark_label;
    }

    public void setMaster_mark_label(String master_mark_label) {
        this.master_mark_label = master_mark_label;
    }

    public long getRegistred_start_id() {
        return registred_start_id;
    }

    public void setRegistred_start_id(long registred_start_id) {
        this.registred_start_id = registred_start_id;
    }

    public long getRegistred_race_id() {
        return registred_race_id;
    }

    public void setRegistred_race_id(long registred_race_id) {
        this.registred_race_id = registred_race_id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}



    void makeOutBoundJSON() {
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(MainActivity.fieldsJSON.asker.toString(),ASKER);
            outBoundJSON.put(MainActivity.fieldsJSON.key.toString(),KEY);
            outBoundJSON.put(MainActivity.fieldsJSON.exec_login.toString(),MainActivity.getLogin());
            outBoundJSON.put(MainActivity.fieldsJSON.exec_level.toString(),MainActivity.getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
            outBoundJSON = Utilites.ErrorJSON();
        }
    }

}
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
                                "\n \nНа старт зарегистрировалось " +
                                JOAnswer.getString(MainActivity.fieldsJSON.counter.toString()) +
                                "участников. Ниже следует их полный список и состояние их регистрации.\n\n";

                ekran.setText(str);

                TableRow tableRow = new TableRow(context);

                TextView cell0 = new TextView(context);
                cell0.setText("Логин");
                tableRow.setBackgroundColor(Color.GRAY);
                tableRow.addView(cell0,0);


                TextView cell1 = new TextView(context);
                cell1.setText("Координаты");
                tableRow.setBackgroundColor(Color.GRAY);
                tableRow.addView(cell1,1);

                TextView cell2 = new TextView(context);
                cell2.setText("Мастерметка");
                tableRow.setBackgroundColor(Color.GRAY);
                tableRow.addView(cell2,2);

                TextView cell3 = new TextView(context);
                cell3.setText("Заезд");
                tableRow.setBackgroundColor(Color.GRAY);
                tableRow.addView(cell3,3);

                tableLayout.addView(tableRow, 0);

                JSONArray arr = JOAnswer.getJSONArray(MainActivity.fieldsJSON.usersArr.toString());
                for(int i = 1 ; i< arr.length()-1 ; i++) {
                    JSONObject obj1 = arr.getJSONObject(i);
                    TempUser usr = new TempUser(obj1);  // может и не надо это, а просто из jsonа сразу фигарить.
                    tableRow = new TableRow(context);
                    tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    cell0 = new TextView(context);
                    cell0.setText(usr.getLogin());
                    tableRow.addView(cell0,0);

                    cell1 = new TextView(context);
                    str = "(" + usr.getLatitude()+", "+usr.getLongitude()+", "+usr.getAltitude() + "); ";
                    cell1.setText(str);
                    tableRow.addView(cell1,1);

                    cell2 = new TextView(context);
                    cell2.setText(usr.getMaster_mark_label());
                    tableRow.addView(cell2,2);

                    cell3 = new TextView(context);
                    str = usr.getRegistred_race_id()+" / "+usr.getRegistred_start_id();
                    cell3.setText(str);
                    tableRow.addView(cell3,3);

                    tableLayout.addView(tableRow, i);
                }


            }


            // http://developer.alexanderklimov.ru/android/layout/tablelayout.php

        } catch (JSONException e) {	e.printStackTrace();}
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
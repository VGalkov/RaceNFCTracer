package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.R;

import static ru.galkov.racenfctracer.MainActivity.KEY;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.view.ViewGroup.LayoutParams;

public class AskRaceConfig  extends AsyncTask<String, Void, String> {

    private final String ASKER = "AskRaceConfig";
    private JSONObject outBoundJSON;
    private TableLayout tableLayout;
    private Context context;

    public AskRaceConfig(TableLayout tableLayout1, Context context2) {
        this.tableLayout =  tableLayout1;
        this.context = context2;
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


    // http://developer.alexanderklimov.ru/android/layout/tablelayout.php
    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject JOAnswer = new JSONObject(result);
            String serverKEY = JOAnswer.getString(MainActivity.fieldsJSON.key.toString());
            if (Utilites.chkKey(serverKEY)) {
                int counter = JOAnswer.getInt(MainActivity.fieldsJSON.key.toString());
                JSONArray arr = JOAnswer.getJSONArray(MainActivity.fieldsJSON.usersArr.toString());
                //TODO вытащить общие данные объявленного старта
                int race_id = JOAnswer.getInt(MainActivity.fieldsJSON.race_id.toString());
                int start_id = JOAnswer.getInt(MainActivity.fieldsJSON.start_id.toString());
                String start_time = JOAnswer.getString(MainActivity.fieldsJSON.start_time.toString());
                String stop_time = JOAnswer.getString(MainActivity.fieldsJSON.stop_time.toString());

                //TODO укоротить запись.
                for(int i = 0 ; i< arr.length() ; i++) {
                    JSONObject obj1 = arr.getJSONObject(i);
                    UserTemplate usr =  new UserTemplate(obj1);
                    TableRow tableRow = new TableRow(context);
                    tableRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    //TODO совать в таблицу данные о регистрации каждого участника.
//                    tableRow.setBackgroundResource(R.drawable.shelf);
//                    tableRow.addView(суда конструкцию из textview.setText(usr[i]), i);  - колонки
                    tableLayout.addView(tableRow, i); // добавка строк.
                }

            }
     } catch (JSONException e) {	e.printStackTrace();}
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


    class UserTemplate {
        private int Id;
        private String login;
        private String level;
        private Double latitude;
        private Double altitude;
        private Double longitude;
        private String master_mark_label;
        private long registred_start_id;
        private long registred_race_id;

        UserTemplate(JSONObject obj1) {
            try{
                this.setLogin(obj1.getString(MainActivity.fieldsJSON.login.toString()));
                this.setAltitude(obj1.getDouble(MainActivity.fieldsJSON.altitude.toString()));
                this.setLatitude(obj1.getDouble(MainActivity.fieldsJSON.latitude.toString()));
                this.setLongitude(obj1.getDouble(MainActivity.fieldsJSON.longitude.toString()));
                this.setLevel(obj1.getString(MainActivity.fieldsJSON.level.toString()));
                this.setMaster_mark_label(obj1.getString(MainActivity.fieldsJSON.master_mark_label.toString()));
                this.setRegistred_race_id(obj1.getInt(MainActivity.fieldsJSON.registred_race_id.toString()));
                this.setRegistred_start_id(obj1.getInt(MainActivity.fieldsJSON.registred_start_id.toString()));
            } catch (JSONException e) {	e.printStackTrace();}
        }


            /*

                obj.put("Id", Row.getId());
                obj.put(fieldsJSON.login.toString(), Row.getLogin());
                obj.put(fieldsJSON.level.toString(), Row.getLevel());
                obj.put(fieldsJSON.latitude.toString(), Row.getLatitude());
                obj.put(fieldsJSON.altitude.toString(), Row.getAltitude());
                obj.put(fieldsJSON.longitude.toString(), Row.getLongtitude());
                obj.put(fieldsJSON.master_mark_label.toString(), Row.getMaster_mark_label());
                obj.put(fieldsJSON.registred_start_id.toString() , Row.getRegistred_start_id());
                obj.put(fieldsJSON.registred_race_id.toString() , Row.getRegistred_race_id());
                arr.put(obj);
		}
        outBoundJSON.put(fieldsJSON.usersArr.toString(), arr);

    */



        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public int getId() {
            return Id;
        }

        public void setId(int id) {
            Id = id;
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

        public Double getLatitude() {
            return latitude;
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
    }
}

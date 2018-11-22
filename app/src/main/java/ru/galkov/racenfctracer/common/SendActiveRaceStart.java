package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity.fieldsJSON;

import static ru.galkov.racenfctracer.MainActivity.KEY;
import static ru.galkov.racenfctracer.MainActivity.formatForDate;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;
import static ru.galkov.racenfctracer.MainActivity.getStartDate;
import static ru.galkov.racenfctracer.MainActivity.getStopDate;
import static ru.galkov.racenfctracer.common.Utilites.chkKey;

public class SendActiveRaceStart extends AsyncTask<String, Void, String> {

    private final String ASKER = "SendActiveRaceStart";
    private JSONObject outBoundJSON;
    private TextView ekran;
    private long race_id =0L;
    private long start_id =0L;


    public void setRace_id(long race_id) {
        this.race_id = race_id;
    }

    public void setStart_id(long start_id) {
        this.start_id = start_id;
    }

    public  SendActiveRaceStart(TextView ekran1) {
        this.ekran = ekran1;
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
    "start_id":9,
    "exec_login":"+79272006026","race_id":4,"asker":"SendActiveRaceStart","exec_level":"Admin","key":"galkovvladimirandreevich"}

*/

    @Override
    protected void onPostExecute(String result) {
        try {
                JSONObject JOAnswer = new JSONObject(result);
                if (chkKey((String) JOAnswer.get(fieldsJSON.key.toString()))) {

                   setRace_id(JOAnswer.getLong(fieldsJSON.race_id.toString()));
                   setStart_id(JOAnswer.getLong(fieldsJSON.start_id.toString()));
                   //TODO прописать тут registred_race_id registred_start_id для пользователя.
                   String str = "Соревнование: " + JOAnswer.getString(fieldsJSON.race_id.toString()) +
                        "\n Заезд: " + JOAnswer.getString(fieldsJSON.start_id.toString());
                   ekran.setText(str);
                }
        }
        catch (JSONException e) {	e.printStackTrace();}
    }


    void makeOutBoundJSON() {
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(fieldsJSON.asker.toString(),ASKER)
                    .put(fieldsJSON.race_id.toString(),race_id)
                    .put(fieldsJSON.start_id.toString(),start_id)
                    .put(fieldsJSON.key.toString(),KEY)
                    .put(fieldsJSON.exec_login.toString(), getLogin())
                    .put(fieldsJSON.exec_level.toString(), getLevel())
                    .put(fieldsJSON.start_time.toString(),formatForDate.format(getStartDate()))
                    .put(fieldsJSON.stop_time.toString(),formatForDate.format(getStopDate()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
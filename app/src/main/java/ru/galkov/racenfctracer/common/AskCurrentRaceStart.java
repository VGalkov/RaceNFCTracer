package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskCurrentRaceStart extends AsyncTask<String, Void, String> {

    private final String ASKER = "AskCurrentRaceStart";
    private JSONObject outBoundJSON;
    private TextView ekran;
    private TextView showStop;
    private TextView showStart;



    // fields

    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;

    public AskCurrentRaceStart(TextView ekran1, TextView showStop2, TextView showStart3) {
        this.ekran = ekran1;
        this.showStart = showStart3;
        this.showStop = showStop2;
    }


    public void setMethod(MainActivity.writeMethod method) {
        this.method = method;
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
// получаем номер заезда и должны его прописать в сеттеры майн активити и инфу в этот активти.
// {race_id, race_label, start_id, start_label}
        String str = "\n";

        try {
            JSONObject JOAnswer = new JSONObject(result);
            String serverKEY = JOAnswer.getString(MainActivity.fieldsJSON.key.toString());
            // true, если определён админом.
            String status = JOAnswer.getString(MainActivity.fieldsJSON.status.toString());

            if (serverKEY.equals(KEY)) {
                if (status.equals(MainActivity.trigger.TRUE.toString())) {
                    MainActivity.setRace_id(JOAnswer.getLong(MainActivity.fieldsJSON.race_id.toString()));
                    MainActivity.setStart_id(JOAnswer.getLong(MainActivity.fieldsJSON.start_id.toString()));
// TODO !!
                    MainActivity.setStartDate(MainActivity.formatForDate.parse(JOAnswer.getString(MainActivity.fieldsJSON.start_time.toString())));
                    MainActivity.setStopDate(MainActivity.formatForDate.parse(JOAnswer.getString(MainActivity.fieldsJSON.stop_time.toString())));
                    str = "Соревнование: " + MainActivity.getRace_id() +
                            "\n Заезд: " + MainActivity.getStart_id();
                    showStop.setText(JOAnswer.getString(MainActivity.fieldsJSON.start_time.toString()));
                    showStart.setText(JOAnswer.getString(MainActivity.fieldsJSON.stop_time.toString()));
                }
                else {
                    str = "заезд не создан админом!";
                    // сброс соревнования!!!
                    MainActivity.setRace_id(JOAnswer.getLong(MainActivity.fieldsJSON.race_id.toString()));
                    MainActivity.setStart_id(JOAnswer.getLong(MainActivity.fieldsJSON.start_id.toString()));
                }

                if (method == MainActivity.writeMethod.Append)     ekran.append(str);
                else    ekran.setText(str);
            }

        } catch (JSONException | ParseException e) {	e.printStackTrace();}
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
        }
    }

}


package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;
import org.json.*;
import java.text.ParseException;
import ru.galkov.racenfctracer.MainActivity;
import static ru.galkov.racenfctracer.MainActivity.KEY;
import static ru.galkov.racenfctracer.MainActivity.formatForDate;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;
import static ru.galkov.racenfctracer.MainActivity.writeMethod;
import static ru.galkov.racenfctracer.MainActivity.setRace_id;
import static ru.galkov.racenfctracer.MainActivity.setStartDate;
import static ru.galkov.racenfctracer.MainActivity.setStart_id;
import static ru.galkov.racenfctracer.MainActivity.setStopDate;
import static ru.galkov.racenfctracer.common.Utilites.chkKey;

import ru.galkov.racenfctracer.MainActivity.fieldsJSON;
import ru.galkov.racenfctracer.MainActivity.trigger;

public class AskCurrentRaceStart extends AsyncTask<String, Void, String> {

    private final String ASKER = "AskCurrentRaceStart";
    private JSONObject outBoundJSON;
    private TextView ekran, showStop, showStart;

    private writeMethod method = writeMethod.Set;

    public AskCurrentRaceStart(TextView ekran1, TextView showStop2, TextView showStart3) {
        this.ekran = ekran1;
        this.showStart = showStart3;
        this.showStop = showStop2;
    }

    public void setMethod(writeMethod method) {
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
        try {

            JSONObject JOAnswer = new JSONObject(result);
            String status = JOAnswer.getString(fieldsJSON.status.toString());

            if (chkKey(JOAnswer.getString(fieldsJSON.key.toString()))) {
                String str;
                if (status.equals(trigger.TRUE.toString())) {
                    setRace_id(JOAnswer.getLong(fieldsJSON.race_id.toString()));
                    setStart_id(JOAnswer.getLong(fieldsJSON.start_id.toString()));
                    setStartDate(formatForDate.parse(JOAnswer.getString(fieldsJSON.start_time.toString())));
                    setStopDate(formatForDate.parse(JOAnswer.getString(fieldsJSON.stop_time.toString())));
                    str = "Соревнование: " + MainActivity.getRace_id() + "\n Заезд: " + MainActivity.getStart_id();
                    showStop.setText(JOAnswer.getString(fieldsJSON.start_time.toString()));
                    showStart.setText(JOAnswer.getString(fieldsJSON.stop_time.toString()));
                }
                else {
                    str = "заезд не создан админом!";
                    setRace_id(JOAnswer.getLong(fieldsJSON.race_id.toString()));
                    setStart_id(JOAnswer.getLong(fieldsJSON.start_id.toString()));
                }

                if (method == writeMethod.Append)     ekran.append(str);
                else    ekran.setText(str);
            }

        } catch (JSONException | ParseException e) {	e.printStackTrace();}
    }


    void makeOutBoundJSON() {
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(fieldsJSON.asker.toString(),ASKER);
            outBoundJSON.put(fieldsJSON.key.toString(),KEY);
            outBoundJSON.put(fieldsJSON.exec_login.toString(),getLogin());
            outBoundJSON.put(fieldsJSON.exec_level.toString(),getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}


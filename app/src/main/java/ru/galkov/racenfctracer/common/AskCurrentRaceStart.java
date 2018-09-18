package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskCurrentRaceStart extends AsyncTask<String, Void, String> {

    private final String ASKER = "AskCurrentRaceStart";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    private MainActivity.trigger trigger;
    private TextView ekran;


    // fields

    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;

    public AskCurrentRaceStart(TextView ekran1) {
        this.ekran = ekran1;
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
            String serverKEY = JOAnswer.getString(f.key.toString());
            // true, если определён админом.
            String status = JOAnswer.getString(f.status.toString());

            if (serverKEY.equals(KEY)) {
                if (status.equals(trigger.TRUE.toString())) {
                    MainActivity.setRace_id(JOAnswer.getLong(f.race_id.toString()));
                    MainActivity.setStart_id(JOAnswer.getLong(f.start_id.toString()));
                    str = "Соревнование: " + JOAnswer.getString(f.race_name.toString()) + "(" + JOAnswer.getString(f.race_id.toString()) + ")" +
                            "\n Заезд: " + JOAnswer.getString(f.start_label.toString()) + "(" + JOAnswer.getString(f.start_id.toString()) + ")";
                }
                else str = "заезд не создан админом!";

                if (method == MainActivity.writeMethod.Append)     ekran.append(str);
                else    ekran.setText(str);

            }

        } catch (JSONException e) {	e.printStackTrace();}
    }


    void makeOutBoundJSON() {
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);
            outBoundJSON.put(f.key.toString(),KEY);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

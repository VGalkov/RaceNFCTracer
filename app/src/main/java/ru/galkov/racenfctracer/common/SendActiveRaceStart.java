package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class SendActiveRaceStart extends AsyncTask<String, Void, String> {
    // прописывает активный рейс и старт.

    private final String ASKER = "SendActiveRaceStart";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    private MainActivity.trigger trigger;
    private MainActivity.writeMethod method;
    private TextView ekran;

    // fields
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
        // TODO выпадающие списки рейсов и стартов связанные.
        try {
                JSONObject JOAnswer = new JSONObject(result);
                String serverKEY = JOAnswer.getString(f.key.toString());
                // устанавливаем номер гонки и номер старта глобально.
                MainActivity.setRace_id(JOAnswer.getLong(f.race_id.toString()));
                MainActivity.setStart_id(JOAnswer.getLong(f.start_id.toString()));

                String str = "Соревнование: " + JOAnswer.getString(f.race_name.toString()) + "(" + JOAnswer.getString(f.race_id.toString()) + ")" +
                    "\n Заезд: " + JOAnswer.getString(f.start_label.toString()) + "(" + JOAnswer.getString(f.start_id.toString()) + ")";
                ekran.setText(str);
        }
        catch (JSONException e) {	e.printStackTrace();}
    }


    void makeOutBoundJSON() {
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);
            outBoundJSON.put(f.race_id.toString(),race_id);
            outBoundJSON.put(f.start_id.toString(),start_id);
            outBoundJSON.put(f.key.toString(),KEY);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
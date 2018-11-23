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
import ru.galkov.racenfctracer.MainActivity.fieldsJSON;

import static ru.galkov.racenfctracer.MainActivity.KEY;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;
import static ru.galkov.racenfctracer.common.Utilites.ErrorJSON;
import static ru.galkov.racenfctracer.common.Utilites.chkKey;

public class AskRaceConfig  extends AsyncTask<String, Void, String> {

    private final String ASKER = "AskRaceConfig";
    private JSONObject outBoundJSON;
    private TableLayout tableLayout;
    private Context context;
    private TextView ekran;

    public AskRaceConfig(TableLayout tableLayout1, Context context2, TextView start_config3) {
        this.tableLayout = tableLayout1;
        this.context = context2;
        this.ekran = start_config3;
    }

    private void Close() {
        // защита от утечки памяти.
        ekran = null;
        context = null;
        tableLayout = null;

    }

    @Override
    protected void onPreExecute() {
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
            if (chkKey(JOAnswer.getString(fieldsJSON.key.toString()))) {
                String str = " Суммарно, конфигурация соревнования: \n Номер соревнования: " +
                        JOAnswer.getString(fieldsJSON.race_id.toString()) +
                        " номер старта в рамках соревнования " +
                        JOAnswer.getString(fieldsJSON.start_id.toString()) +
                        " \n Диапазон времени старта(начало/конец) -  \n" +
                        JOAnswer.getString(fieldsJSON.start_time.toString()) + " - " +
                        JOAnswer.getString(fieldsJSON.stop_time.toString()) +
                        "\n \n На старт зарегистрировалось " +
                        JOAnswer.getString(fieldsJSON.counter.toString()) +
                        " участников. Ниже следует их полный список и состояние их регистрации.\n\n";

                ekran.setText(str);
                tableLayout.addView(drawHeader(), 0);

                JSONArray arr = JOAnswer.getJSONArray(fieldsJSON.usersArr.toString());
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj1 = arr.getJSONObject(i);
                    TempUser usr = new TempUser(obj1);
                    tableLayout.addView(drawRow(usr, JOAnswer), i + 1);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Close();
    }

    private TableRow drawRow(TempUser usr1, JSONObject JOAnswer2) {
        TableRow tableRow = new TableRow(context);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        try {

            drawCell(tableRow, usr1.getLogin(), 0, Color.WHITE);

            if ((usr1.getRegistred_race_id() == 0) || (usr1.getRegistred_start_id() == 0)) {
                drawCell(tableRow, "не зарегистрировался", 1, Color.RED);
            } else if ((usr1.getRegistred_race_id() != JOAnswer2.getLong(MainActivity.fieldsJSON.race_id.toString())) ||
                    (usr1.getRegistred_start_id() != JOAnswer2.getLong(MainActivity.fieldsJSON.start_id.toString()))) {
                drawCell(tableRow, "не тот старт", 1, Color.RED);
            } else {
                drawCell(tableRow, usr1.getRegistred_race_id() + " / " + usr1.getRegistred_start_id(), 1, Color.GREEN);
            }

            if (usr1.getMaster_mark_label().equals("")) {
                drawCell(tableRow, "нет мастерметки", 2, Color.RED);
            } else {
                drawCell(tableRow, usr1.getMaster_mark_label(), 2, Color.GREEN);
            }

            if ((usr1.getLatitude() == 0) || (usr1.getLongitude() == 0)) {
                drawCell(tableRow, "не поступают", 3, Color.RED);
            } else {
                drawCell(tableRow, "поступают", 3, Color.GREEN);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tableRow;
    }

    private TableRow drawHeader () {
        TableRow tableRow = new TableRow(context);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        drawCell(tableRow, "Логин", 0, Color.LTGRAY);
        drawCell(tableRow, "Заезд", 1, Color.LTGRAY);
        drawCell(tableRow, "Мастерметка", 2, Color.LTGRAY);
        drawCell(tableRow, "Координаты", 3, Color.LTGRAY);

        return tableRow;
    }


    private void drawCell(TableRow tableRow1, String str2, int id3, int color4) {

        TextView cell = new TextView(context);
        cell.setText(str2);
        cell.setBackgroundColor(color4);
        tableRow1.setBackgroundColor(Color.WHITE);
        tableRow1.addView(cell,id3);
    }


    private void makeOutBoundJSON() {
        try {
            outBoundJSON = new JSONObject()
                    .put(fieldsJSON.asker.toString(),ASKER)
                    .put(fieldsJSON.key.toString(),KEY)
                    .put(fieldsJSON.exec_login.toString(),getLogin())
                    .put(fieldsJSON.exec_level.toString(),getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
            outBoundJSON = ErrorJSON();
        }
    }

}
package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

public class AskForMainLog extends AsyncTask<String, Void, String> {

    private String URL_Extention;
    private TextView ResultEkran;


    public AskForMainLog(TextView ResultEkran1) {
        this.ResultEkran = ResultEkran1;
    }

    public  void setURL_Extention(String str) {
        URL_Extention = str;
    }

    @Override
    protected String doInBackground(String... strings) {
        return Utilites.getMainLogJSON_ZAGLUSHKA();
    }

    @Override
    protected void onPostExecute(String result) {

        if (result == null) {

        }
        else {
            // очеловечить вывод. сейчас это гольный son массив
            ResultEkran.setText(result);
        }

    }
}

package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;

import ru.galkov.racenfctracer.MainActivity;

public class SendUserNFCDiscovery extends AsyncTask<String, Void, String> {

    private String SERVER_URL = MainActivity.SERVER_URL;

    @Override
    protected String doInBackground(String... strings) {
        // отправляем на сервер уведомление о считывании участником NFC  метки
        return Utilites.getUserHaveReadNFCJSON_ZAGLUSHKA();

    }

    @Override
    protected void onPostExecute(String result) {
// обработка ответа сервера на сохранение считывания метки участником.
    }
}

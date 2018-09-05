package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import ru.galkov.racenfctracer.MainActivity;

public class SendNewNFCMark extends AsyncTask<String, Void, String> {

    private String SERVER_URL = MainActivity.SERVER_URL;

    @Override
    protected String doInBackground(String... strings) {
        // отправляем серверу новую NFC метку и получаем ответ
        return Utilites.getNewNFCMarkResultJSON_ZAGLUSHKA();
    }

    @Override
    protected void onPostExecute(String result) {
// обработка ответа сервера о сохранении новой метки
    }
}

package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;
import ru.galkov.racenfctracer.MainActivity;

public class SendNewLoginLevel extends AsyncTask<String, Void, String> {

    private TextView login;
    private TextView password;
    private MainActivity.registrationLevel level;
    private String SERVER_URL = MainActivity.SERVER_URL;


    public void setLogin(TextView login1) {
        this.login = login1;

    }

    public void setLevel(MainActivity.registrationLevel level1) {
        this.level = level1;

    }

    @Override
    protected String doInBackground(String... strings) {
        // пишем запрос серверу на смену логина тут и обрабатываем его ответ
        return Utilites.getLoginLevelChangingJSON_ZAGLUSHKA();

    }

    @Override
    protected void onPostExecute(String result) {
     // обработка ответа о смене админом уровня доступа логину.
    }


}

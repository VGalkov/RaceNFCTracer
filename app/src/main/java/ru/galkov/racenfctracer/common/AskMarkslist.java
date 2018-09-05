package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;

import ru.galkov.racenfctracer.MainActivity;
import ru.galkov.racenfctracer.adminLib.ActivityNFCMarksRedactor;

public class AskMarkslist extends AsyncTask<String, Void, String> {



    private final String SERVER_URL = MainActivity.SERVER_URL + "/ActivityUserManager/";
    private ActivityNFCMarksRedactor.ActivityNFCMarksRedactorController ANFCMRC;


    public AskMarkslist (ActivityNFCMarksRedactor.ActivityNFCMarksRedactorController ANFCMRC1) {
        ANFCMRC = ANFCMRC1;

    }

    @Override
    protected String doInBackground(String... strings) {
// список NFC меток на сервере.
        return Utilites.getNFC_MarksListJSON_ZAGLUSHKA();
    }

    @Override
    protected void onPostExecute(String result) {
// отображение меток
        ANFCMRC.NFC_ConfigurationLog.setText(result);
    }



}

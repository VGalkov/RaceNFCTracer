package ru.galkov.racenfctracer.common;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class SendNewNFCMark extends AsyncTask<String, Void, String> {

    private final String ASKER = "SendNewNFCMark";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    private String login;
    private String mark;
    private String admin;
    private TextView NFC_ConfigurationLog;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;

    public SendNewNFCMark(TextView NFC_ConfigurationLog1) {
        this.NFC_ConfigurationLog = NFC_ConfigurationLog1;
    }

    public void setMark(String mark1) {
        this.mark = mark1;
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
// обработка ответа сервера о сохранении новой метки
        // TODO rewrite!
        //=>
        //        {"asker":"SendNewNFCMark","mark":"09201724307","key":"galkovvladimirandreevich","status":"TRUE"}
        try {
            JSONObject JOAnswer = new JSONObject(result);

            if (Utilites.chkKey((String) JOAnswer.get("key"))) {
                if(JOAnswer.get("Status").equals("TRUE")) {  // TRUE|FALSE
                    NFC_ConfigurationLog.append("Зарегистрирована метка: " + JOAnswer.get("Mark") +"\n");
                }
                else {
                    NFC_ConfigurationLog.append(JOAnswer.get("Error").toString());
                }

            }
        }
        catch (JSONException e) {	e.printStackTrace();}
    }

    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;

    }

    private  void makeOutBoundJSON(){

//        {"asker":"SendNewNFCMark","mark" ="09201724307","key":"galkovvladimirandreevich"}
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);
            outBoundJSON.put(f.mark.toString(),this.mark);
            outBoundJSON.put(f.key.toString(),KEY);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import ru.galkov.racenfctracer.MainActivity;

import static ru.galkov.racenfctracer.MainActivity.KEY;

public class AskResultsTable extends AsyncTask<String, Void, String> {


    private TextView userLogger;
    private final String ASKER = "AskResultsTable";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;
    private MainActivity.fileType fileType;
    private Context context;

    public AskResultsTable(TextView userLogger1,MainActivity.fileType fileType2, Context context3) {
        this.userLogger = userLogger1;
        this.fileType = fileType2;
        this.context = context3;
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

        // если в JSON преобразуется - в ответе не файл :)
        try {
                JSONObject JOAnswer = new JSONObject(result);
                String res;
                String serverKEY = JOAnswer.getString(f.key.toString());
                if (Utilites.chkKey(serverKEY)) {
                    res = "Файл не выслан!" + JOAnswer.getString(f.resultsFileLink.toString()) + "/" + JOAnswer.getString(f.resultsFileDir.toString());
                }
                else        { res = "Ошибка ключа или версии клиента!"; }

                if (method == MainActivity.writeMethod.Append) userLogger.append(res);
                else userLogger.setText(res);

            } catch (JSONException e) {
            try {
                    String filePath = context.getExternalCacheDir().toString() + "/"+fileType+".csv"; // папка приложения на SD
                    // String filePath =  Environment.getExternalStorageDirectory().toString() + "/"+fileType+".csv"; //корень SD
                    //String filePath = context.getCacheDir().toString() + "/"+fileType+".csv";
                    //String filePath = context.getFilesDir().getPath().toString() + "/"+fileType+".csv"; // туда не попасть
                    File file = new File(filePath);
                    if(file.exists()) file.delete();
                    file.createNewFile();
                    PrintWriter out = new PrintWriter(file.getAbsoluteFile());
                    result = result.replace("||", "\r\n");
                    out.println(result);
                    out.close();
                    if (method == MainActivity.writeMethod.Append)    userLogger.append("Файл  сохранён в:" + filePath);
                    else        userLogger.setText("Файл  сохранён в:" + filePath);

            }catch (IOException ee) {
                ee.printStackTrace();
            }


        }

    }





    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;

    }

    private  void makeOutBoundJSON(){
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);
            outBoundJSON.put(f.key.toString(),KEY);
            outBoundJSON.put(f.fileType.toString(),fileType);
            outBoundJSON.put(f.exec_login.toString(),MainActivity.getLogin());
            outBoundJSON.put(f.exec_level.toString(),MainActivity.getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}

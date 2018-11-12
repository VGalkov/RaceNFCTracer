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

            try {
                    String filePath = context.getExternalCacheDir().toString() + "/"+fileType+".csv"; // папка приложения на SD
                    File file = new File(filePath);
                    if(file.exists()) file.delete();
                    file.createNewFile();
                    PrintWriter out = new PrintWriter(file.getAbsoluteFile());
                    result = result.replace("||", "\r\n");
                    out.println(result);
                    out.close();
                    if (method == MainActivity.writeMethod.Append)    userLogger.append("Файл  сохранён в:" + filePath);
                    else        {
                        String str = "Файл  сохранён в:" + filePath;
                        userLogger.setText(str);
                    }

            }catch (IOException ee) {
                ee.printStackTrace();
            }


        //}

    }





    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;

    }

    private  void makeOutBoundJSON(){
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(MainActivity.fieldsJSON.asker.toString(),ASKER);
            outBoundJSON.put(MainActivity.fieldsJSON.key.toString(),KEY);
            outBoundJSON.put(MainActivity.fieldsJSON.fileType.toString(),fileType);
            outBoundJSON.put(MainActivity.fieldsJSON.exec_login.toString(),MainActivity.getLogin());
            outBoundJSON.put(MainActivity.fieldsJSON.exec_level.toString(),MainActivity.getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}

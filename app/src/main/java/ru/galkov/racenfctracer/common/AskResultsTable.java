package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import ru.galkov.racenfctracer.MainActivity.fieldsJSON;
import ru.galkov.racenfctracer.MainActivity.fileType;
import ru.galkov.racenfctracer.MainActivity.writeMethod;

import static ru.galkov.racenfctracer.MainActivity.KEY;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;

public class AskResultsTable extends AsyncTask<String, Void, String> {


    private TextView userLogger;
    private Context context;
    private final String ASKER = "AskResultsTable";
    private JSONObject outBoundJSON;
    private writeMethod method = writeMethod.Set;
    private fileType fileType;

    private void Close() {
        // защита от утечки памяти.
        context = null;
        userLogger = null;
    }

    public AskResultsTable(TextView userLogger1, fileType fileType2, Context context3) {
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
                    if (method == writeMethod.Append)    userLogger.append("Файл  сохранён в:" + filePath);
                    else        {
                        String str = "Файл  сохранён в:" + filePath;
                        userLogger.setText(str);
                    }

            }catch (IOException ee) {
                ee.printStackTrace();
            }
            Close();
    }



    public void setMethod(writeMethod method1) {
        method = method1;

    }

    private  void makeOutBoundJSON(){
        try {
            outBoundJSON = new JSONObject()
                    .put(fieldsJSON.asker.toString(),ASKER)
                    .put(fieldsJSON.key.toString(),KEY)
                    .put(fieldsJSON.fileType.toString(),fileType)
                    .put(fieldsJSON.exec_login.toString(), getLogin())
                    .put(fieldsJSON.exec_level.toString(), getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}

package ru.galkov.racenfctracer.common;

import android.graphics.*;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.*;
import org.json.*;
import java.io.*;
import java.net.*;
import static ru.galkov.racenfctracer.MainActivity.KEY;
import static ru.galkov.racenfctracer.MainActivity.SERVER_URL;
import static ru.galkov.racenfctracer.MainActivity.getLevel;
import static ru.galkov.racenfctracer.MainActivity.getLogin;
import ru.galkov.racenfctracer.MainActivity.img_types;
import ru.galkov.racenfctracer.MainActivity.fieldsJSON;

public class  AskResultsImgTable  extends AsyncTask<String, Void, String> {


    private final String ASKER = "AskResultsImgTable";
    private JSONObject outBoundJSON;
    private ImageView image;
    private String IMGType = img_types.ALL.toString();

    private void Close() {
        // защита от утечки памяти.
        image = null;
    }

    public void setIMGType(String IMGType1) {
        this.IMGType = IMGType1;
    }

    public String getIMGType() {
        return IMGType;
    }

    public void setImage(ImageView image1) {
        this.image = image1;
    }

    @Override
    protected void onPreExecute(){
        makeOutBoundJSON();
    }

    // TODO переписать этот говнокод, когда это будет поддерживать приём  Bitmap
    // сейчас для передачи Bitmap делаем конверт в String
    @Override
    protected String doInBackground(String... strings) {

        String temp ="";
        try {
            URL link = new URL(SERVER_URL + "/"+ ASKER + "/" + outBoundJSON);
            InputStream st = link.openConnection().getInputStream();
            Bitmap bM = BitmapFactory.decodeStream(st);
            ByteArrayOutputStream baos=new  ByteArrayOutputStream();
            bM.compress(Bitmap.CompressFormat.PNG,100, baos);
            byte [] b=baos.toByteArray();
            temp=Base64.encodeToString(b, Base64.DEFAULT);
            st.close();
        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Close();
        return temp;
    }

    @Override
    protected void onPostExecute(String result) {

        byte [] encodeByte=Base64.decode(result,Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        image.setImageBitmap(bitmap);
    }


    private  void makeOutBoundJSON(){
        try {
            outBoundJSON = new JSONObject()
                    .put(fieldsJSON.asker.toString(),ASKER)
                    .put(fieldsJSON.key.toString(),KEY)
                    .put(fieldsJSON.exec_login.toString(),getLogin())
                    .put(fieldsJSON.exec_level.toString(),getLevel())
                    .put(fieldsJSON.IMGType.toString(),getIMGType());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

package ru.galkov.racenfctracer.common;

        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.os.AsyncTask;
        import android.util.Base64;
        import android.widget.ImageView;
        import android.widget.TextView;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.ByteArrayOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.net.MalformedURLException;
        import java.net.ProtocolException;
        import java.net.URL;

        import ru.galkov.racenfctracer.MainActivity;

        import static ru.galkov.racenfctracer.MainActivity.KEY;
        import static ru.galkov.racenfctracer.MainActivity.SERVER_URL;

public class  AskResultsImgTable  extends AsyncTask<String, Void, String> {


    private TextView userLogger;
    private final String ASKER = "AskResultsImgTable";
    private JSONObject outBoundJSON;
    private MainActivity.fieldsJSON f;
    private MainActivity.writeMethod method = MainActivity.writeMethod.Set;
    private ImageView image;


    public void setImage(ImageView image1) {
        this.image = image1;
    }
/*
    public AskResultsImgTable() {
    }
*/
    @Override
    protected void onPreExecute(){
        makeOutBoundJSON();
    }

    // TODO переписать этот говнокод, когда это будет поддерживать приём  Bitmap
    // сейчас для передачи Bitmap делаем конверт в String
    @Override
    protected String doInBackground(String... strings) {
        Bitmap bM;
        String temp ="";
        try {
            URL link = new URL(SERVER_URL + "/"+ ASKER + "/" + outBoundJSON);
            InputStream st = link.openConnection().getInputStream();
            bM = BitmapFactory.decodeStream(st);
            ByteArrayOutputStream baos=new  ByteArrayOutputStream();
            bM.compress(Bitmap.CompressFormat.PNG,100, baos);
            byte [] b=baos.toByteArray();
            temp=Base64.encodeToString(b, Base64.DEFAULT);
            st.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return temp;
    }
// https://ru.stackoverflow.com/questions/426386/%D0%97%D0%B0%D0%B3%D1%80%D1%83%D0%B7%D0%BA%D0%B0-%D0%BA%D0%B0%D1%80%D1%82%D0%B8%D0%BD%D0%BE%D0%BA-%D0%B8%D0%B7-%D1%81%D0%B5%D1%82%D0%B8-%D0%B2-%D0%BF%D1%80%D0%B8%D0%BB%D0%BE%D0%B6%D0%B5%D0%BD%D0%B8%D0%B5-android
    @Override
    protected void onPostExecute(String result) {
        Bitmap bitmap = null;
        byte [] encodeByte=Base64.decode(result,Base64.DEFAULT);
        bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        image.setImageBitmap(bitmap);
    }




    public void setMethod(MainActivity.writeMethod method1) {
        method = method1;

    }

    private  void makeOutBoundJSON(){
        try {
            outBoundJSON = new JSONObject();
            outBoundJSON.put(f.asker.toString(),ASKER);
            outBoundJSON.put(f.key.toString(),KEY);
            outBoundJSON.put(f.exec_login.toString(),MainActivity.getLogin());
            outBoundJSON.put(f.exec_level.toString(),MainActivity.getLevel());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}

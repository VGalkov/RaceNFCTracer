package ru.galkov.racenfctracer.common;

import org.json.JSONObject;

import java.io.*;
import java.net.*;
import ru.galkov.racenfctracer.MainActivity;

public class HttpProcessor {

    public final String SERVER_URL = MainActivity.SERVER_URL;
    private String json, ASKER;

    HttpProcessor() {    }

    public String execute() {
        StringBuffer response = new StringBuffer();
        try {
            URL link = new URL(SERVER_URL + "/"+ ASKER + "/" + json);
            HttpURLConnection connection = (HttpURLConnection) link.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0" );
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            connection.setRequestProperty("Content-Type", "application/json");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            while ((inputLine = bufferedReader.readLine()) != null)  response.append(inputLine);
            bufferedReader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    public void setASKER(String ASKER) {
        this.ASKER = ASKER;
    }

    public void setJson(JSONObject json) {
        this.json = json.toString();
    }
}

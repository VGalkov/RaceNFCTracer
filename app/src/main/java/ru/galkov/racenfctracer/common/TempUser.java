package ru.galkov.racenfctracer.common;

import org.json.*;
import ru.galkov.racenfctracer.MainActivity.fieldsJSON;

class TempUser {

    private String login, level, master_mark_label;
    private long registred_start_id, registred_race_id;
    private Double latitude, altitude, longitude;


    TempUser (JSONObject obj) {
        try {
            this.altitude = obj.getDouble(fieldsJSON.altitude.toString());
            this.latitude = obj.getDouble(fieldsJSON.latitude.toString());
            this.longitude = obj.getDouble(fieldsJSON.longitude.toString());
            this.registred_start_id = obj.getLong(fieldsJSON.registred_start_id.toString());
            this.registred_race_id = obj.getLong(fieldsJSON.registred_race_id.toString());
            this.master_mark_label = obj.getString(fieldsJSON.master_mark_label.toString());
            this.level = obj.getString(fieldsJSON.level.toString());
            this.login = obj.getString(fieldsJSON.login.toString());
        }
        catch (JSONException e) {	e.printStackTrace();}

    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMaster_mark_label() {
        return master_mark_label;
    }

    public void setMaster_mark_label(String master_mark_label) {
        this.master_mark_label = master_mark_label;
    }

    public long getRegistred_start_id() {
        return registred_start_id;
    }

    public void setRegistred_start_id(long registred_start_id) {
        this.registred_start_id = registred_start_id;
    }

    public long getRegistred_race_id() {
        return registred_race_id;
    }

    public void setRegistred_race_id(long registred_race_id) {
        this.registred_race_id = registred_race_id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
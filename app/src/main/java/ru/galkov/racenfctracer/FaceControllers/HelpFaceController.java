package ru.galkov.racenfctracer.FaceControllers;

import android.widget.TextView;

public class HelpFaceController extends ActivityFaceController {

    private TextView ekran;
    private String helpTopic;


    public HelpFaceController() {
    }


    public void setEkran(TextView ekran) {
        this.ekran = ekran;
    }

    public void setHelpTopic(String helpTopic) {
        this.helpTopic = helpTopic;
    }

    @Override
    protected void initViewObjects() {

    }

    @Override
    protected void addListeners() {
    }

    @Override
    protected void setDefaultFace() {

    }

    @Override
    public void start() {
        ekran.setText(helpTopic);
    }

    @Override
    protected void stop() {

    }

    @Deprecated
    public void show() {
        ekran.setText(helpTopic);
    }



}
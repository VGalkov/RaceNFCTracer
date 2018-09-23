package ru.galkov.racenfctracer.FaceControllers;

public abstract class ActivityFaceController {

//        private Timer ServerTimer;

        public ActivityFaceController() {
            initViewObjects();
            setDefaultFace();
            addListeners();
        }

        protected abstract void initViewObjects();

        protected abstract void addListeners();

        protected abstract void setDefaultFace();

/*
        protected abstract void start();

        void stop() {
            ServerTimer.cancel();
        }
        protected abstract void startTimeSync(TextView ekran1);
        */
    }


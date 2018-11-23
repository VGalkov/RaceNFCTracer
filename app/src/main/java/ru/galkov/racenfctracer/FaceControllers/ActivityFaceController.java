package ru.galkov.racenfctracer.FaceControllers;

public abstract class ActivityFaceController {


        public ActivityFaceController() {
            initViewObjects();
            setDefaultFace();
            addListeners();
        }

        protected abstract void initViewObjects();

        protected abstract void addListeners();

        protected abstract void setDefaultFace();

        public abstract void start();

        public abstract void stop();

        public abstract boolean isStarted();

        public void restart() {
            stop();
            start();
        }
    }


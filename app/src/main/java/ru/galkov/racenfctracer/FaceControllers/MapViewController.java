package ru.galkov.racenfctracer.FaceControllers;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;

import ru.galkov.racenfctracer.MainActivity;

public class MapViewController extends ActivityFaceController {
    private MapView mapview;
// https://tech.yandex.ru/maps/doc/mapkit/3.x/concepts/android/mapkit/ref/com/yandex/mapkit/map/CameraPosition-docpage/

    public MapViewController(MapView mapview1) {
        mapview = mapview1;
        mapview.getMap().move(
                new CameraPosition(MainActivity.TARGET_LOCATION, MainActivity.DEFAULT_ZOOM, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        initViewObjects();

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

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStarted() {
        return false;
    }
}

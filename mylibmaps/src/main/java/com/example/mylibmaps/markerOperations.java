package com.example.mylibmaps;

/**
 * Created by user on 2017/06/20.
 */

import android.os.Handler;
import android.os.SystemClock;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.Projection;

import android.util.Log;
import android.view.animation.Interpolator;
import android.graphics.Point;
import android.view.animation.LinearInterpolator;
import java.util.*;

public class markerOperations {
//    private boolean runFinish = true;

    public void animateMarker(final GoogleMap mMap,
                              final Marker marker,
                              final LatLng toPosition,
                              final boolean hideMarker) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;
        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
//                    runFinish = false;
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

/*    public boolean isAlive(){
        return runFinish;
    }*/
}

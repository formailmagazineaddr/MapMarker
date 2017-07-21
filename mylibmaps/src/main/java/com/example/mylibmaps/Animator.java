package com.example.mylibmaps;

/**
 * Created by user on 2017/06/20.
 */

import android.os.Handler;
import android.os.SystemClock;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import java.util.*;

/*
public class Animator implements Runnable {
    private  Handler mHandler = new Handler();
    private  Interpolator interpolator = new LinearInterpolator();
    private  long start = SystemClock.uptimeMillis();
    private  Marker trackingMarker;
    private  LatLng endLatLng = null;
    private  LatLng beginLatLng = null;
    private int currentIndex = 0;
    private List<Marker> markers;
    private GoogleMap googleMap;
    private float tilt = 90;

    private static final int ANIMATE_SPEEED = 1500;
    private static final int ANIMATE_SPEEED_TURN = 1000;
    private static final int BEARING_OFFSET = 20;

    public Animator(){
//        mHandler.post(this);
    }

    public void initialize(List<Marker> markers, GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.markers = markers;
        reset();
        highLightMarker(0);
        // We first need to put the camera in the correct position for the first run (we need 2 markers for this).....
        LatLng markerPos = markers.get(0).getPosition();
        LatLng secondPos = markers.get(1).getPosition();

        setupCameraPositionForMovement(markerPos, secondPos);
    }

    private void setupCameraPositionForMovement(LatLng markerPos,
                                                LatLng secondPos) {

        float bearing = bearingBetweenLatLngs(markerPos,secondPos);

        trackingMarker = googleMap.addMarker(new MarkerOptions().position(markerPos)
                .title("title")
                .snippet("snippet"));

        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(markerPos)
                        .bearing(bearing + BEARING_OFFSET)
                        .tilt(90)
                        .zoom(googleMap.getCameraPosition().zoom >=16 ? googleMap.getCameraPosition().zoom : 16)
                        .build();

        googleMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(cameraPosition),
                ANIMATE_SPEEED_TURN,
                new GoogleMap.CancelableCallback() {

                    @Override
                    public void onFinish() {
                        System.out.println("finished camera");
//                        this.reset();
                        Handler handler = new Handler();
//                        handler.post(animator);
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("cancelling camera");
                    }
                }
        );
    }

    private void highLightMarker(int index) {
        highLightMarker(markers.get(index));
    }

    */
/**
     * Highlight the marker by marker.
     *//*

    private void highLightMarker(Marker marker) {
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        marker.showInfoWindow();
    }

    public void reset() {
        resetMarkers();
        start = SystemClock.uptimeMillis();
        currentIndex = 0;
        endLatLng = getEndLatLng();
        beginLatLng = getBeginLatLng();

    }

    private void resetMarkers() {
        for (Marker marker : this.markers) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
    }

    private LatLng getEndLatLng() {
        return markers.get(currentIndex+1).getPosition();
    }

    private LatLng getBeginLatLng() {
        return markers.get(currentIndex).getPosition();
    }

    @Override
    public void run(){
        long elapsed = SystemClock.uptimeMillis() - start;
        double t = interpolator.getInterpolation((float)elapsed/ANIMATE_SPEEED);
        double lat = t * endLatLng.latitude + (1-t) * beginLatLng.latitude;
        double lng = t * endLatLng.longitude + (1-t) * beginLatLng.longitude;
        LatLng newPosition = new LatLng(lat, lng);

        trackingMarker.setPosition(newPosition);

        // It's not possible to move the marker + center it through a cameraposition update while another camerapostioning was already happening.
        //navigateToPoint(newPosition,tilt,bearing,currentZoom,false);
        //navigateToPoint(newPosition,false);

        if (t< 1) {
            mHandler.postDelayed(this, 16);
        } else {

            System.out.println("Move to next marker.... current = " + currentIndex + " and size = " + markers.size());
            // imagine 5 elements -  0|1|2|3|4 currentindex must be smaller than 4
            if (currentIndex<markers.size()-2) {

                currentIndex++;

                endLatLng = getEndLatLng();
                beginLatLng = getBeginLatLng();


                start = SystemClock.uptimeMillis();

                LatLng begin = getBeginLatLng();
                LatLng end = getEndLatLng();

                float bearingL = bearingBetweenLatLngs(begin, end);

                highLightMarker(currentIndex);

                CameraPosition cameraPosition =
                        new CameraPosition.Builder()
                                .target(end) // changed this...
                                .bearing(bearingL  + BEARING_OFFSET)
                                .tilt(tilt)
                                .zoom(googleMap.getCameraPosition().zoom)
                                .build();


                googleMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(cameraPosition),
                        ANIMATE_SPEEED_TURN,
                        null
                );

                start = SystemClock.uptimeMillis();
                mHandler.postDelayed(this, 16);

            } else {
                currentIndex++;
                highLightMarker(currentIndex);
                stopAnimation();
            }

        }

    }

    public void stopAnimation() {
        this.stop();
    }

    public void stop() {
        trackingMarker.remove();
        mHandler.removeCallbacks(this);

    }

    private float bearingBetweenLatLngs(LatLng begin,LatLng end) {
        Location beginL= convertLatLngToLocation(begin);
        Location endL= convertLatLngToLocation(end);

        return beginL.bearingTo(endL);
    }

    private Location convertLatLngToLocation(LatLng latLng) {
        Location loc = new Location("someLoc");
        loc.setLatitude(latLng.latitude);
        loc.setLongitude(latLng.longitude);
        return loc;
    }
}
*/

public class Animator{
    private  Handler mHandler = new Handler();
    private  Interpolator interpolator = new LinearInterpolator();
    private  long start = SystemClock.uptimeMillis();
    private  Marker trackingMarker;
    private  LatLng endLatLng = null;
    private  LatLng beginLatLng = null;
    private int currentIndex = 0;
    private List<Marker> markers;
    private GoogleMap googleMap;
    private float tilt = 90;

/*
    private static final int ANIMATE_SPEEED = 1500;
    private static final int ANIMATE_SPEEED_TURN = 1000;
*/
private static final int ANIMATE_SPEEED = 15000;
    private static final int ANIMATE_SPEEED_TURN = 10000;
    private static final int BEARING_OFFSET = 20;

    public Animator(){
//        mHandler.post(this);
    }

    public void initialize(List<Marker> markers, GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.markers = markers;
        reset();
        highLightMarker(0);
        // We first need to put the camera in the correct position for the first run (we need 2 markers for this).....
        LatLng markerPos = markers.get(0).getPosition();
        LatLng secondPos = markers.get(1).getPosition();

        setupCameraPositionForMovement(markerPos, secondPos);
    }

    private void setupCameraPositionForMovement(LatLng markerPos,
                                                LatLng secondPos) {

        float bearing = bearingBetweenLatLngs(markerPos,secondPos);

        trackingMarker = googleMap.addMarker(new MarkerOptions().position(markerPos)
                .title("title")
                .snippet("snippet"));

        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(markerPos)
                        .bearing(bearing + BEARING_OFFSET)
                        .tilt(90)
                        .zoom(googleMap.getCameraPosition().zoom >=16 ? googleMap.getCameraPosition().zoom : 16)
                        .build();

        googleMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(cameraPosition),
                ANIMATE_SPEEED_TURN,
                new GoogleMap.CancelableCallback() {

                    @Override
                    public void onFinish() {
                        System.out.println("finished camera");
//                        this.reset();
                        Handler handler = new Handler();
//                        handler.post(animator);
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("cancelling camera");
                    }
                }
        );
    }

    private void highLightMarker(int index) {
        highLightMarker(markers.get(index));
    }

    /**
     * Highlight the marker by marker.
     */
    private void highLightMarker(Marker marker) {
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        marker.showInfoWindow();
    }

    public void reset() {
        resetMarkers();
        start = SystemClock.uptimeMillis();
        currentIndex = 0;
        endLatLng = getEndLatLng();
        beginLatLng = getBeginLatLng();

    }

    private void resetMarkers() {
        for (Marker marker : this.markers) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
    }

    private LatLng getEndLatLng() {
        return markers.get(currentIndex+1).getPosition();
    }

    private LatLng getBeginLatLng() {
        return markers.get(currentIndex).getPosition();
    }

    public void animateMarker(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                double t = interpolator.getInterpolation((float) elapsed / ANIMATE_SPEEED);
                double lat = t * endLatLng.latitude + (1 - t) * beginLatLng.latitude;
                double lng = t * endLatLng.longitude + (1 - t) * beginLatLng.longitude;
                LatLng newPosition = new LatLng(lat, lng);

                trackingMarker.setPosition(newPosition);

                // It's not possible to move the marker + center it through a cameraposition update while another camerapostioning was already happening.
                //navigateToPoint(newPosition,tilt,bearing,currentZoom,false);
                //navigateToPoint(newPosition,false);

                if (t < 1) {
                    mHandler.postDelayed(this, 16);
                } else {

                    System.out.println("Move to next marker.... current = " + currentIndex + " and size = " + markers.size());
                    // imagine 5 elements -  0|1|2|3|4 currentindex must be smaller than 4
                    if (currentIndex < markers.size() - 2) {

                        currentIndex++;

                        endLatLng = getEndLatLng();
                        beginLatLng = getBeginLatLng();


                        start = SystemClock.uptimeMillis();

                        LatLng begin = getBeginLatLng();
                        LatLng end = getEndLatLng();

                        float bearingL = bearingBetweenLatLngs(begin, end);

                        highLightMarker(currentIndex);

                        CameraPosition cameraPosition =
                                new CameraPosition.Builder()
                                        .target(end) // changed this...
                                        .bearing(bearingL + BEARING_OFFSET)
                                        .tilt(tilt)
                                        .zoom(googleMap.getCameraPosition().zoom)
                                        .build();


                        googleMap.animateCamera(
                                CameraUpdateFactory.newCameraPosition(cameraPosition),
                                ANIMATE_SPEEED_TURN,
                                null
                        );

                        start = SystemClock.uptimeMillis();
                        mHandler.postDelayed(this, 16);

                    } else {
                        currentIndex++;
                        highLightMarker(currentIndex);
                        stopAnimation();
                    }

                }

            }
        });
    }

    public void stopAnimation() {
        this.stop();
    }

    public void stop() {
        trackingMarker.remove();
//        mHandler.removeCallbacks(this);

    }

    private float bearingBetweenLatLngs(LatLng begin,LatLng end) {
        Location beginL= convertLatLngToLocation(begin);
        Location endL= convertLatLngToLocation(end);

        return beginL.bearingTo(endL);
    }

    private Location convertLatLngToLocation(LatLng latLng) {
        Location loc = new Location("someLoc");
        loc.setLatitude(latLng.latitude);
        loc.setLongitude(latLng.longitude);
        return loc;
    }
}

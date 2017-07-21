package com.example.user.mapmarker;

import android.Manifest;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;    //what?
import android.support.v4.app.ActivityCompat;    //what?
import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import android.location.Location;
import android.view.View;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.Context;
import android.content.IntentSender;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.Marker;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import com.example.mylibmaps.*;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
//import com.example.mylibmaps.markerOperations;
import com.example.mylibmaps.Animator;

import java.util.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener{

    private GoogleMap mMap;

    private float zoomLevel = 10.0F;
    private double latitude;
    private double longitude;
    private String markerId;
    private Marker marker;
    private Marker theUserMarker;
    private markerOperations markerOps;
    private Animator animator;
    Thread AnimatorThread;

    protected static final String TAG = "debugTag";
    private final static String LOCATION_KEY = "location-key";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;   //10秒間隔で位置情報を更新。実際には多少頻度が多くなるかもしれない。
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;    //最速の更新間隔。この値より頻繁に更新されることはない。
    private final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    private final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 10;

    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Boolean mRequestingLocationUpdates;
    private Button startLocationUpdateBtn, stopLocationUpdateBtn, Animatebtn;
    private Map<String, LatLng> mapLatLng;
    private Map<String, Marker> mapMarker;
    private List<LatLng> listLatLng;
    private List<Marker> listMarker;
    private PolylineOptions polylineOptions;
    private Polyline polyline;
    private int color = Color.RED;
    private int width = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        startLocationUpdateBtn = (Button)findViewById(R.id.start_updates_button);
        stopLocationUpdateBtn = (Button)findViewById(R.id.stop_updates_button);
        mRequestingLocationUpdates = false;
        updateValuesFromBundle(savedInstanceState);
        buildGoogleApiClient();
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "updateValuesFromBundle");
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
                setButtonsEnabledState();
            }

            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
//                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
//            updateUI();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "buildGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void setButtonsEnabledState() {
        if (mRequestingLocationUpdates) {
            startLocationUpdateBtn.setEnabled(false);
            stopLocationUpdateBtn.setEnabled(true);
        } else {
            startLocationUpdateBtn.setEnabled(true);
            stopLocationUpdateBtn.setEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mCurrentLocation == null){return;}   //getLastLocation() is a method to get a known location. No known location then return null. If you want to get the latest place use requestLocationUpdates().
            LatLng theUserLatLng = new LatLng( mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude() );
            theUserMarker = mMap.addMarker(new MarkerOptions()
                            .position(theUserLatLng)
                            .title("You"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(theUserLatLng, zoomLevel));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged");
        LatLng theUserLatLng = new LatLng( location.getLatitude(), location.getLongitude() );
        replaceMarkerOnMap(mMap, theUserMarker, theUserLatLng);
/*
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
*/
        Toast.makeText(this, getResources().getString(R.string.location_updated_message), Toast.LENGTH_SHORT).show();
    }

    private void replaceMarkerOnMap(GoogleMap mMap, Marker marker, LatLng latLng) {
        marker.setPosition(latLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady");
        mMap = googleMap;
        mapLatLng = new LinkedHashMap<String, LatLng>();
        mapMarker = new LinkedHashMap<String, Marker>();

        polylineOptions = new PolylineOptions().color(color).width(width);
        polyline = mMap.addPolyline(polylineOptions);
        markerOps = new markerOperations();

        animator = new Animator();
//        AnimatorThread = new Thread(animator);

        setListeners();
    }

    private void setListeners() {
        // when the user clicked on map
        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng clickLocation) {
                marker = mMap.addMarker(new MarkerOptions()
                        .position(clickLocation)
                        .title(markerId)
                        .snippet("Lat,Lng="+clickLocation.latitude+","+clickLocation.longitude));
                markerId = marker.getId();
                Toast.makeText(getApplicationContext(), markerId, Toast.LENGTH_LONG).show();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clickLocation, mMap.getCameraPosition().zoom));

                mapLatLng.put(markerId,clickLocation);
                mapMarker.put(markerId,marker);
                listLatLng = new ArrayList<LatLng>(mapLatLng.values());
                listMarker = new ArrayList<Marker>(mapMarker.values());

                polyline.setPoints(listLatLng);
            }
        });

        // when the user long clicked on map
        mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng longClickLocation) {
                Log.i(TAG, "start markerOps.animateMarker()");

//                markerOps.animateMarker(mMap, theUserMarker, longClickLocation, false);

                animator.initialize(listMarker, mMap);
                animator.animateMarker();
//                AnimatorThread.start();
            }
        });

        // when the user clicked maker
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker clickedMarker) {
                markerId = clickedMarker.getId();
                clickedMarker.remove();
                mapLatLng.remove(markerId);
                mapMarker.remove(markerId);
                listLatLng = new ArrayList<LatLng>(mapLatLng.values());
                listMarker = new ArrayList<Marker>(mapMarker.values());
                polyline.setPoints(listLatLng);

                return true;
            }
        });
    }

    //-------------------------------------
    // called when pushed 'start updates' button
    //-------------------------------------
    public void startUpdatesButtonHandler(View view) {
//        clearUI();
        if (!isPlayServicesAvailable(this)) return;
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
        } else {
            return;
        }

        if (Build.VERSION.SDK_INT < 23) {
            setButtonsEnabledState();
            startLocationUpdates();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setButtonsEnabledState();
            startLocationUpdates();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                showRationaleDialog();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    private void showRationaleDialog() {
        new AlertDialog.Builder(this)
                .setPositiveButton("許可する", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MapsActivity.this,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }
                })
                .setNegativeButton("しない", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MapsActivity.this, "位置情報パーミッションが許可されませんでした。", Toast.LENGTH_SHORT).show();
                        mRequestingLocationUpdates = false;
                    }
                })
                .setCancelable(false)
                .setMessage("このアプリは位置情報の利用を許可する必要があります。")
                .show();
    }

    private void startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates");

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        // 現在位置の取得の前に位置情報の設定が有効になっているか確認する
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // 設定が有効になっているので現在位置を取得する
                        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MapsActivity.this);
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // 設定が有効になっていないのでダイアログを表示する
                        try {
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    //-------------------------------------
    // called when pushed 'stop updates' button
    //-------------------------------------
    public void stopUpdatesButtonHandler(View view) {
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
            setButtonsEnabledState();
            stopLocationUpdates();
        }
    }

    protected void stopLocationUpdates() {
        Log.i(TAG, "stopLocationUpdates");
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public static boolean isPlayServicesAvailable(Context context) {
        // Google Play Service APKが有効かどうかチェックする
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog((Activity) context, resultCode, 2).show();
            return false;
        }
        return true;
    }

}

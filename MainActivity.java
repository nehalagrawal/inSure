package com.example.starterproject;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;
//import android.view.View;
//import android.content.Intent;

//import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
//import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    private LocationDisplay mLocationDisplay;

    //private String Source, Destination;
    //float radius;

    private void setupMap() {
        if (mMapView != null) {
            Basemap.Type basemapType = Basemap.Type.STREETS_VECTOR;
            double latitude = 34.09042;
            double longitude = -118.71511;
            int levelOfDetail = 11;
            ArcGISMap map = new ArcGISMap(basemapType, latitude, longitude, levelOfDetail);
            mMapView.setMap(map);
        }
    }

    private void setupLocationDisplay() {
        mLocationDisplay = mMapView.getLocationDisplay();
        mLocationDisplay.addDataSourceStatusChangedListener(dataSourceStatusChangedEvent -> {

            // If LocationDisplay started OK or no error is reported, then continue.
            if (dataSourceStatusChangedEvent.isStarted() || dataSourceStatusChangedEvent.getError() == null) {
                return;
            }

            int requestPermissionsCode = 2;
            String[] requestPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

            // If an error is found, handle the failure to start.
            // Check permissions to see if failure may be due to lack of permissions.
            if (!(ContextCompat.checkSelfPermission(MainActivity.this, requestPermissions[0]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(MainActivity.this, requestPermissions[1]) == PackageManager.PERMISSION_GRANTED)) {

                // If permissions are not already granted, request permission from the user.
                ActivityCompat.requestPermissions(MainActivity.this, requestPermissions, requestPermissionsCode);
            } else {

                // Report other unknown failure types to the user - for example, location services may not
                // be enabled on the device.
                String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
                        .getSource().getLocationDataSource().getError().getMessage());
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
        mLocationDisplay.startAsync();
        final Point[] currentLocation = new Point[1];
        mLocationDisplay.addLocationChangedListener(new LocationDisplay.LocationChangedListener() {
            @Override public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                currentLocation[0] = mLocationDisplay.getMapLocation();
                Point wgsPoint=(com.esri.arcgisruntime.geometry.Point)GeometryEngine.project(currentLocation[0], SpatialReferences.getWgs84());
                handleMapPoint(wgsPoint);
                // only update preferredSearchLocation if device has moved
                //if (!currentLocation[0].equals(mLocationDisplay.getMapLocation(), 100) || mPreferredSearchProximity == null) {
                //    mPreferredSearchProximity = mLocationDisplay.getMapLocation();
                //}
            }
        });

    }

    public void handleMapPoint(Point mapPoint) {
        Log.i("Point moved", "Location x: " + String.valueOf(mapPoint.getX()) + " y: " + String.valueOf(mapPoint.getY()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // Location permission was granted. This would have been triggered in response to failing to start the
            // LocationDisplay, so try starting this again.
            mLocationDisplay.startAsync();
        } else {

            // If permission was denied, show toast to inform user what was chosen. If LocationDisplay is started again,
            // request permission UX will be shown again, option should be shown to allow never showing the UX again.
            // Alternative would be to disable functionality so request is not shown again.
            Toast.makeText(MainActivity.this, getResources().getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.mapView);
        setupMap();
       // Source = getIntent().getExtras().getString("source");
        //Destination = getIntent().getExtras().getString("destination");
        setupLocationDisplay();
    }

    @Override
    protected void onPause() {
        if (mMapView != null) {
            mMapView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (mMapView != null) {
            mMapView.dispose();
        }
        super.onDestroy();
    }



}

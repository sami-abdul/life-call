package com.technuclear.lifecall.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.technuclear.lifecall.R;

import android.net.Uri;
import android.view.MenuItem;

public class TrackPatientActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double latitude;
    double longitude;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    public static final String CO_ORDINATES_KEY = "coOrdinatesKey";
    public static final String LATITUDE_KEY = "latitudeKey";
    public static final String LONGITUDE_KEY = "longitudeKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_patient);

        sharedPref = getSharedPreferences(CO_ORDINATES_KEY, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        mMap = googleMap;

        Intent intent = getIntent();
        String url = intent.getStringExtra(MainActivity.URL);
        if (url != null) {
            Uri uri = intent.getData();
            String[] arr = url.split(",");
            latitude = Double.parseDouble(arr[1]);
            longitude = Double.parseDouble(arr[2]);
            editor.putFloat(LATITUDE_KEY, (float) latitude);
            editor.putFloat(LONGITUDE_KEY, (float) longitude);
            editor.commit();
        } else {
            latitude = (double) sharedPref.getFloat(LATITUDE_KEY, 24.878145f);
            longitude = (double) sharedPref.getFloat(LONGITUDE_KEY, 67.172261f);
        }

        LatLng karachi = new LatLng(latitude, longitude);

        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(karachi).title("Marker in Karachi"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(karachi, 14.0f));
    }
}

package com.example.sondestrabelsi.openhomes;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText resultTxt ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        resultTxt = (EditText) findViewById(R.id.TVresult);
        LatLng charguia = new LatLng(36.851767,10.21114829999999);
        mMap.addMarker(new MarkerOptions().position(charguia).title("ENICarthage"));
        float zoomLevel = 16.0f;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(charguia, zoomLevel));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET,
            }, 10);
            return;
        }

        mMap.setMyLocationEnabled(true);
    }

    public void onSpeak(View view){
        if (view.getId()==R.id.BVsearch)
        {

            promptSpeechInput();
        }
    }
    public void promptSpeechInput(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something");
        try {
            startActivityForResult(i, 100);
        }
        catch (ActivityNotFoundException e){
            Toast.makeText(MapsActivity.this,"Sorry your device doesn't support speech language",Toast.LENGTH_LONG).show();
        }
    }
    public void onActivityResult(int request_code, int result_code , Intent i)
    {
        super.onActivityResult(request_code,result_code,i);
        switch (request_code){
            case 100: if(result_code==RESULT_OK && i!= null) {
                ArrayList<String> result = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                resultTxt.setText(result.get(0));
            }
            break;
        }

    }
    public void onSearch(View view){
        EditText location_tf = (EditText) findViewById(R.id.TVresult);
        String loc = location_tf.getText().toString();
        List<Address> adresslist= null;
        if ( loc!= null || !loc.equals(""))
        {
           Geocoder geocoder = new Geocoder(this);
            try {
                adresslist = geocoder.getFromLocationName(loc,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address =adresslist.get(0);
            LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        }
    }
}



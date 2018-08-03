package com.elkhamitech.hajjhackproject.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.elkhamitech.hajjhackproject.R;
import com.elkhamitech.hajjhackproject.model.MyMarker;
import com.elkhamitech.hajjhackproject.model.Operator;
import com.elkhamitech.hajjhackproject.view.adapters.PopupAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.Inflater;

import static com.elkhamitech.hajjhackproject.R.layout.map_popup;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private MyMarker myMarker;
    private String topic;
    private String topic2;
    private MqttAndroidClient client;
    private Circle mCircle;
    String myTopic = "Zone1/SubZone1/UnitID2/Medic/Req";
    String pTopic = "Zone1/SubZone1/UnitID1/Medic/Resp";
    Marker markerName;
    boolean xyz = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("SOS Box");

        myMarker = new MyMarker();
        loadFromIOT();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(xyz){
            markerName.remove();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

            }
        }

    }

    private void loadFromIOT() {


        topic = myTopic;
        topic2 = pTopic;

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(getApplicationContext(),"tcp://broker.mqttdashboard.com:1883",clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("LOOk", "onSuccess");
//                    Toast.makeText(getApplicationContext(), "Connected",
//                            Toast.LENGTH_LONG).show();
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("LOOK", "onFailure");
//                    Toast.makeText(getApplicationContext(), "Failed",
//                            Toast.LENGTH_LONG).show();
                }

            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                //HERE DO WHAT YOU WANT
//                Toast.makeText(MainActivity.this, (new String(message.getPayload())), Toast.LENGTH_SHORT).show();

                String str = (new String(message.getPayload()));
                String[] splited = str.split(",");
//                Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();

//                for(int i = 0; i < splited.length; i++){
//                    Toast.makeText(MainActivity.this, splited[i].toString(), Toast.LENGTH_SHORT).show();
//                }
                myMarker.setLat(splited[0]);
                myMarker.setLon(splited[1]);
                myMarker.setRFid(splited[2]);
                myMarker.setUnitId(splited[3]);

//                Toast.makeText(MainActivity.this, myMarker.getLat()+myMarker.getLon(), Toast.LENGTH_SHORT).show();

                centerMapLocation(Double.valueOf(myMarker.getLat()),Double.valueOf(myMarker.getLon()),myMarker.getUnitId(),BitmapDescriptorFactory.HUE_RED);


                LatLng currentLocation = new LatLng(Double.valueOf(myMarker.getLat()),Double.valueOf(myMarker.getLon()));
                markerName = mMap.addMarker(new MarkerOptions().position(currentLocation).title(myMarker.getUnitId()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 19));

                mMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

//                        marker.showInfoWindow();
                        marker.hideInfoWindow();

                    }
                });

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }

    public void setSubscription(){
        try {
            client.subscribe(topic, 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        try {
          client.subscribe(topic2, 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void checkPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            mMap.setMyLocationEnabled(true);

        }
    }


    private void drawMarkerWithCircle(LatLng position){
        double radiusInMeters = 1.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mCircle = mMap.addCircle(circleOptions);

//        MarkerOptions markerOptions = new MarkerOptions().position(position);
//        mMarker = mMap.addMarker(markerOptions);
    }


    //Overload takes location
    public void centerMapLocation(Location location, String title, float color){

        double lat = location.getLatitude();
        double lon = location.getLongitude();

        LatLng currentLocation = new LatLng(lat, lon);

        if(title != "Your current location"){
            mMap.addMarker(new MarkerOptions().position(currentLocation).title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker(color)));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

    }



    public void centerMapLocation(double lat,double lon , String title, float color){

        LatLng currentLocation = new LatLng(lat, lon);

        if(title != "Your current location"){
            mMap.addMarker(new MarkerOptions().position(currentLocation).title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker(color)));
        }

        //BitmapDescriptorFactory.HUE_GREEN
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                double lat = location.getLatitude();
                double lon = location.getLongitude();

                LatLng currentLocation = new LatLng(lat, lon);

//              mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                float[] distance = new float[2];

                drawMarkerWithCircle(currentLocation);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

                    Location.distanceBetween(21.486254,  39.192989,
                            mCircle.getCenter().latitude, mCircle.getCenter().longitude, distance);

                    if( distance[0] > mCircle.getRadius()  ){
//                        Toast.makeText(getBaseContext(), "Outside", Toast.LENGTH_LONG).show();

                    } else {

                        String message = "Done";
                        if(xyz){
                            markerName.remove();
                        }

                        try {
                            client.publish(pTopic, message.getBytes(), 0,false);
//                            Toast.makeText(getApplicationContext(),"Sent",Toast.LENGTH_LONG).show();




                        } catch ( MqttException e) {
                            e.printStackTrace();

                        }
//                        Toast.makeText(getBaseContext(), "Inside", Toast.LENGTH_LONG).show();
                    }


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };


        viewZonePins(mMap);

        checkPermissions();
    }

    private void viewZonePins(GoogleMap mMap){

        MyMarker m1 = new MyMarker();
        m1.setLat("21.486029");
        m1.setLon("39.192002");
        m1.setUnitId("M1");
        m1.setRFid("");

        MyMarker m2 = new MyMarker();
        m2.setLat("21.486094");
        m2.setLon("39.192319");
        m2.setUnitId("M2");
        m2.setRFid("");

        MyMarker m3 = new MyMarker();
        m3.setLat("21.486144");
        m3.setLon("39.192641");
        m3.setUnitId("M3");
        m3.setRFid("");

        MyMarker m4 = new MyMarker();
        m4.setLat("21.486254");
        m4.setLon("39.192989");
        m4.setUnitId("M4");
        m4.setRFid("");


        ArrayList<MyMarker> markersArray = new ArrayList<MyMarker>();

        markersArray.add(m1);
        markersArray.add(m2);
        markersArray.add(m3);
        markersArray.add(m4);

        for(int i = 0 ; i < markersArray.size() ; i++) {

            createMarker(markersArray.get(i).getLat(), markersArray.get(i).getLon(), markersArray.get(i).getUnitId(), markersArray.get(i).getRFid());
        }

        LatLng currentLocation = new LatLng(Double.valueOf(m1.getLat()),Double.valueOf(m1.getLon()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));


    }

    protected Marker createMarker(String latitude, String longitude, String title, String snippet) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
    }
}

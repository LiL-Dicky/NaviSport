package com.example.navisport;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import classes.MyLocationListener;
import classes.Point;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;
    private Marker myLoc = null;
    private LatLng position = new LatLng(0,0);
    MyLocationListener list = new MyLocationListener();
    private LocationManager locationManager;
    private boolean updateFlag = false;

    private boolean buttonStatus = true;
    private static final int REQUEST_LOCATION = 2;
    private ArrayList<Point> listPoints = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private final static String FILE_NAME = "pos.txt";
    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAddPoint = findViewById(R.id.PointMenu);
        btnAddPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonStatus) {
                    pause();
                    buttonStatus = false;
                }
                Intent intent = new Intent(".AddPoint");
                startActivity(intent);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        if (!hasPermissions()) {
            requestPerms();
            pause();
        } else {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            list.showLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        }
    }

    protected void resume() {
        if (hasPermissions()) {
            list.setLocationManager(locationManager);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, list);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 10, list);
        }
    }

    protected void pause() {
        if(hasPermissions() && list.checkEnabled()) {
            list.pause();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if(hasPermissions()) {
            list.setMap(map);
            list.setMyLoc(myLoc);
            list.setPosition(position);
        }
    }

    private boolean hasPermissions() {
        int res;
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        for (String perms : permissions) {
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void requestPerms() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, REQUEST_LOCATION);
        }
        this.finish();
    }

    public void onButtonLocationClicked(View view) {
        if(list.checkEnabled()) {
            if (buttonStatus) {
                pause();
                buttonStatus = false;
                Toast.makeText(MainActivity.this, "Navigation is just turned off", Toast.LENGTH_SHORT).show();
            } else {
                resume();
                buttonStatus = true;
                Toast.makeText(MainActivity.this, "Navigation is just turned on", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(MainActivity.this, "You need to put on your geolocation", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    public void onButtonUpdateClicked(View view) {
        if(!updateFlag) {
            if(markers.size() != 0) {
                for (int i = 0; i < markers.size(); i++) {
                    Marker mrk = markers.get(i);
                    mrk.remove();
                }
            }
            try {
                openText();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Error: file is not readen", Toast.LENGTH_SHORT).show();
            }
            if (listPoints.get(0).getName().length() != 0) {

            } else {
                Toast.makeText(MainActivity.this, "Error: file is empty", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(MainActivity.this, "File is already readen", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveText(){

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());
        }
        catch(IOException ex) {System.out.println(ex);}
        finally{
            try{
                if(fos!=null)
                    fos.close();
            }
            catch(IOException ex){
                System.out.println(ex);
            }
        }
    }

    public void openText() throws Exception {
        FileInputStream fin = null;
        try {
            fin = openFileInput(FILE_NAME);
        } catch (FileNotFoundException e) {
            Toast.makeText(MainActivity.this, "Error: file", Toast.LENGTH_SHORT).show();
        }
        text = null;
        InputStreamReader reader = new InputStreamReader(fin);
        BufferedReader buffer = new BufferedReader(reader);
        StringBuilder str = new StringBuilder();
        while ((text = buffer.readLine()) != null) {
            str.append(text).append(" ");
        }
        String[] arr;
        String name;
        int flag;
        double lattitude;
        double longtitude;
        if (str.length() != 0) {
            arr = str.toString().split("@@@   @@@");
            for (int i = 0; i < arr.length; i++) {
                flag = Integer.parseInt(arr[i]);
                i++;
                name = arr[i];
                i++;
                lattitude = Double.parseDouble(arr[i]);
                i++;
                longtitude = Double.parseDouble(arr[i]);
                if (flag == 0) {
                    listPoints.add(new Point(0, name, lattitude, longtitude));
                }
                if (flag == 1) {
                    listPoints.add(new Point(0, name, list.getMyLocationLattitude(), list.getMyLocationLongitude()));
                }
            }
            Toast.makeText(MainActivity.this, "File readed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Error: file", Toast.LENGTH_SHORT).show();
        }
    }
}

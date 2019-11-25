package com.example.navisport;

import classes.MyLocationListener;
import classes.Point;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<Point> listPoints = new ArrayList<>();
    private final static String FILE_NAME = "pos.txt";
    private final static String FREE_SPACE = "@@@   @@@";
    private String text;
    private boolean updateFlag = false;

    private static final int REQUEST_LOCATION = 2;
    GoogleMap map;
    MyLocationListener list = new MyLocationListener();
    private LocationManager locationManager;
    private boolean buttonStatus = true;
    LatLng position;
    Marker myLoc = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAddPoint = findViewById(R.id.PointMenu);
        btnAddPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonStatus) {
                    updateFlag = false;
                    pause();
                    onPause();
                }
                Intent intent = new Intent(".AddPoint");
                startActivity(intent);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        if(!hasPermissions()) {
            requestPerms();
            pause();
            onPause();
            buttonStatus = false;
        }
        else {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            list.showLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(hasPermissions()) {
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
            map.setMapType(MAP_TYPE_SATELLITE);
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

    public void onButtonLocationClicked(View v){
        if(list.checkEnabled()) {
            if (buttonStatus) {
                pause();
                buttonStatus = false;
                Toast.makeText(MainActivity.this, "Navigation is just turned off", Toast.LENGTH_SHORT).show();
            } else {
                onResume();
                buttonStatus = true;
                Toast.makeText(MainActivity.this, "Navigation is just turned on", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "You need to put on your geolocation", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    public void onButtonUpdateClicked(View v){
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
                Toast.makeText(MainActivity.this, "Error: file is not readed", Toast.LENGTH_SHORT).show();
            }
            if (listPoints.get(0).getName().length() != 0) {
                text = null;
                if (listPoints.size() == 0) {
                    Toast.makeText(MainActivity.this, "Error: file is empty", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < listPoints.size(); i++) {
                        markers.add(map.addMarker(new MarkerOptions().position(new LatLng(listPoints.get(i).getLattitude(),
                                listPoints.get(i).getLongtitude())).title(listPoints.get(i).getName())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
                        if (text != null) {
                            text = text + FREE_SPACE + listPoints.get(i).getFlag() + FREE_SPACE + listPoints.get(i).getName()
                                    + FREE_SPACE + listPoints.get(i).getLattitude() + FREE_SPACE + listPoints.get(i).getLongtitude();
                        } else {
                            text = listPoints.get(i).getFlag() + FREE_SPACE + listPoints.get(i).getName() + FREE_SPACE
                                    + listPoints.get(i).getLattitude() + FREE_SPACE + listPoints.get(i).getLongtitude();
                        }
                    }
                    saveText();
                    updateFlag = true;
                }
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

    public void openText() throws IOException {
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
            arr = str.toString().split(FREE_SPACE);
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

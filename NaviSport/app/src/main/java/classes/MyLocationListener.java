package classes;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyLocationListener implements LocationListener {
    private double myLocationLongitude;
    private double myLocationLattitude;
    private LocationManager locationManager;
    private Marker myLoc = null;
    private LatLng position;
    private GoogleMap map;

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    public void setMyLoc(Marker myLoc) {
        this.myLoc = myLoc;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public MyLocationListener() {
        this.myLocationLongitude = 0;
        this.myLocationLattitude = 0;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public double getMyLocationLongitude() {
        return myLocationLongitude;
    }

    public double getMyLocationLattitude() {
        return myLocationLattitude;
    }

    public void pause(){
        locationManager.removeUpdates(this);
        myLoc.remove();
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocationLattitude, myLocationLongitude)));
    }

    @Override
    public void onLocationChanged(Location location) {
        showLocation(location);
        if (myLoc != null) myLoc.remove();
        position = new LatLng(myLocationLattitude, myLocationLongitude);
        myLoc = map.addMarker(new MarkerOptions().position(position).title("You here"));
        map.moveCamera(CameraUpdateFactory.newLatLng(position));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    public void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            myLocationLattitude = location.getLatitude();
            myLocationLongitude = location.getLongitude();
        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            myLocationLattitude = location.getLatitude();
            myLocationLongitude = location.getLongitude();
        }
    }

    public boolean checkEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}

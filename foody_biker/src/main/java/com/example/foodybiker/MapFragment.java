package com.example.foodybiker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MapFragment extends Fragment {

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private LocationManager manager;
    private LocationListener listener;
    private FirebaseAuth firebaseAuth;

    public MapFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate and return the layout
        final View v = inflater.inflate(R.layout.fragment_map, container,false);
        mMapView = v.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                String locationProvider = LocationManager.NETWORK_PROVIDER;
                mGoogleMap = googleMap;
                if(ActivityCompat.checkSelfPermission(v.getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        Location loc = manager.getLastKnownLocation(locationProvider);

                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                                .child("Bikers/" + user.getUid());
                        HashMap<String, Object> child = new HashMap<>();
                        child.put("location", loc);
                        database.updateChildren(child);

                        MarkerOptions marker = new MarkerOptions().position(
                                new LatLng(loc.getLatitude(), loc.getLongitude())).title("Position");
                        marker.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                        mGoogleMap.addMarker(marker);
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(loc.getLatitude(), loc.getLongitude())).zoom(12).build();
                        mGoogleMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition));
                    }else{
                        Log.d("SWSW", "No permissions inside");
                    }
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                }
            }
        });
         manager = (LocationManager) getActivity().getSystemService(getActivity().getApplicationContext().LOCATION_SERVICE);

        // Define a listener that responds to location updates
        listener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        startListen();

        //use mGoogleMaps to continue set thing on maps

        // Perform any camera updates here
        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 200) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String locationProvider = LocationManager.NETWORK_PROVIDER;
                if(ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    Location loc = manager.getLastKnownLocation(locationProvider);

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                            .child("Bikers/" + user.getUid());
                    HashMap<String, Object> child = new HashMap<>();
                    child.put("location", loc);
                    database.updateChildren(child);

                    MarkerOptions marker = new MarkerOptions().position(
                            new LatLng(loc.getLatitude(), loc.getLongitude())).title("Position");
                    marker.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                    mGoogleMap.addMarker(marker);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(loc.getLatitude(), loc.getLongitude())).zoom(12).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }else{
                    Log.d("SWSW", "No permissions inside");
                }
            } else {
                Log.d("SWSW", "No permissions outside");
            }
         }

    }

    void makeUseOfNewLocation(Location location){

        Log.d("POSITIONDD","Called changed position");
        if(mGoogleMap != null){
            // create marker
            MarkerOptions marker = new MarkerOptions().position(
                    new LatLng(location.getLatitude(), location.getLongitude())).title("Position");

            // Changing marker icon
            marker.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

            // adding marker
            mGoogleMap.addMarker(marker);
        }
    }


    public void startListen(){
        String locationProvider = LocationManager.NETWORK_PROVIDER;
// Or, use GPS location data:
// String locationProvider = LocationManager.GPS_PROVIDER;
        if(ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            manager.requestLocationUpdates(locationProvider, 0, 0, listener);
    }

    public void stopListen(){
        manager.removeUpdates(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}

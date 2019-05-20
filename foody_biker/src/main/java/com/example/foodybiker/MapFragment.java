package com.example.foodybiker;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MapFragment extends Fragment {

    private GoogleMap mGoogleMap;
    private FirebaseAuth firebaseAuth;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 6;
    Location mLastKnownLocation;
    SupportMapFragment mapFragment;
    private LocationCallback locationCallback;
    LocationRequest locationRequest;

    public MapFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        locationRequest = new LocationRequest();;

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Toast.makeText(mapFragment.getContext(), location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                            .child("Bikers/" + user.getUid());
                    HashMap<String, Object> child = new HashMap<>();
                    child.put("location", location);
                    database.updateChildren(child);
                }
            };
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mapFragment.getContext());

        mLocationPermissionGranted = ContextCompat.checkSelfPermission(mapFragment.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mGoogleMap = mMap;

                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.clear(); //clear old markers

                getDeviceLocation();
                updateLocationUI();
            }
        });

        return rootView;
    }

    private void getLocationPermission(){
        if (ContextCompat.checkSelfPermission(mapFragment.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void permissionsGranted(){
        mLocationPermissionGranted = true;
    }

    public void permissionsGrantedVis(){
        mLocationPermissionGranted = true;
        updateLocationUI();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mGoogleMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);

                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                startLocationUpdates();

                Location loc = mLastKnownLocation;
                if(mFusedLocationProviderClient.getLastLocation().isComplete())
                    loc = (Location) mFusedLocationProviderClient.getLastLocation().getResult();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                        .child("Bikers/" + user.getUid());
                HashMap<String, Object> child = new HashMap<>();
                child.put("location", loc);
                database.updateChildren(child);
                //mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude())));
                //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                //        new LatLng(mLastKnownLocation.getLatitude(),
                //                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
            } else {
                mGoogleMap.setMyLocationEnabled(false);

                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        if (mGoogleMap == null) {
            Log.d("PROVA", "inside mGoogleMap");
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.

                            mLastKnownLocation = (Location) task.getResult();
                            if(mLastKnownLocation != null) {
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                                    .child("Bikers/" + user.getUid());
                            HashMap<String, Object> child = new HashMap<>();
                            child.put("location", mLastKnownLocation);
                            database.updateChildren(child);
                        } else {
                            Log.d("LOCATION", "Current location is null. Using defaults.");
                            //Log.e("LOCATION", "Exception: %s", task.getException());
                            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void startLocationUpdates() {
        mLocationPermissionGranted = ContextCompat.checkSelfPermission(mapFragment.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setFastestInterval(10000);

        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

}
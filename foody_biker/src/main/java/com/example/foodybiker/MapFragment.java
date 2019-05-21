package com.example.foodybiker;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment {

    private GoogleMap mGoogleMap;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 6;
    Location mLastKnownLocation;
    SupportMapFragment mapFragment;
    private LocationCallback locationCallback;
    LocationRequest locationRequest;
    private ArrayList<Reservation> reservations;
    Reservation activeReservation;
    private LatLngBounds.Builder builder;

    public MapFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        locationRequest = new LocationRequest();;
        builder = new LatLngBounds.Builder();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (final Location location : locationResult.getLocations()) {
                    Toast.makeText(mapFragment.getContext(), location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    final DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                            .child("Bikers/" + user.getUid());
                    HashMap<String, Object> child = new HashMap<>();
                    child.put("location", location);
                    database.updateChildren(child);

                    if(activeReservation == null)
                        fetchRestaurant(location);
                    else {
                        fetchUserAndRestaurant(location);
                    }
                }
            };
        };
    }

    public void fetchRestaurant(Location location) {
        final Location loc = location;
        Log.d("PROVA", reservations.size() + "");
        if(!reservations.isEmpty()) {
            for (int i = 0; i < reservations.size(); i++) {
                DatabaseReference databaseRest = FirebaseDatabase.getInstance().getReference().child("restaurantsInfo");
                Query queryLatLong;
                final String name = reservations.get(i).getRestaurantName();
                queryLatLong = databaseRest.child(reservations.get(i).getRestaurantID());
                //TODO: controllare se giusto
                queryLatLong.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            ReservationDBBiker reservationDB = ds.getValue(ReservationDBBiker.class);
                            Double lat = reservationDB.getLatitude();
                            Double lon = reservationDB.getLongitude();
                            LatLng latlon = new LatLng(lat, lon);

                            Log.d("PROVA", "lat " + latlon.latitude + " long " + latlon.longitude + " name " + name);
                            Double distance = haversineDistance(loc.getLatitude(), loc.getLongitude(), lat, lon);
                            MarkerOptions mark = new MarkerOptions();
                            mark.position(latlon);
                            mark.title(name);
                            mark.snippet(String.format(Locale.getDefault(), "distance %.2f km", distance.floatValue()));
                            mark.icon(BitmapDescriptorFactory.fromResource(R.drawable.rest_icon));
                            mGoogleMap.addMarker(mark);

                            builder.include(latlon);
                            builder.include(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                            LatLngBounds bounds = builder.build();
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 150);
                            mGoogleMap.animateCamera(cu);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        } else {
            Log.d("PROVA", "empty");
        }
    }

    public void fetchUserAndRestaurant(Location location) {
        final Location loc = location;
        Log.d("PROVA", activeReservation + "");

        String address = activeReservation.getUserAddress();
        List<Address> lista = new ArrayList<>();
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            lista = geocoder.getFromLocationName(address, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LatLng latLng = new LatLng(lista.get(0).getLatitude(), lista.get(0).getLongitude());
        Log.d("PROVA", "latlon " + latLng.latitude + " " + latLng.longitude);
    }

    public double haversineDistance(double initialLat, double initialLong,
                                    double finalLat, double finalLong){
        int R = 6371; // km (Earth radius)
        double dLat = toRadians(finalLat-initialLat);
        double dLon = toRadians(finalLong-initialLong);
        initialLat = toRadians(initialLat);
        finalLat = toRadians(finalLat);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(initialLat) * Math.cos(finalLat);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    public double toRadians(double deg) {
        return deg * (Math.PI/180);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mapFragment.getContext());

        mLocationPermissionGranted = ContextCompat.checkSelfPermission(mapFragment.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        initRestFromDB();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mGoogleMap = mMap;

                getDeviceLocation();
                updateLocationUI();
            }
        });

        return rootView;
    }

    private void initRestFromDB() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        activeReservation = null;
        reservations = new ArrayList<>();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("reservations").child("Bikers");
        Query query = database.child(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d("PROVA", ds.getKey());
                    ReservationDBBiker reservationDB = ds.getValue(ReservationDBBiker.class);
                    if (reservationDB.getStatus() == null) {
                        Reservation reservation = new Reservation(reservationDB.getRestaurantName(), reservationDB.getRestaurantAddress(),
                                reservationDB.getOrderTimeBiker(), reservationDB.getUserName(), reservationDB.getUserAddress(),
                                reservationDB.getOrderTime(), reservationDB.getRestaurantID(),null, false);
                        reservation.setReservationID(ds.getKey());
                        reservations.add(reservation);
                    } else if(reservationDB.getStatus().equals("accepted")){
                        Reservation reservation = new Reservation(reservationDB.getRestaurantName(), reservationDB.getRestaurantAddress(),
                                reservationDB.getOrderTimeBiker(), reservationDB.getUserName(), reservationDB.getUserAddress(),
                                reservationDB.getOrderTime(), reservationDB.getRestaurantID(),null, true);
                        reservation.setReservationID(ds.getKey());
                        activeReservation = reservation;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

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
                                           @NonNull String[] permissions,
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
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //locationRequest.setSmallestDisplacement(30.0f);
        locationRequest.setMaxWaitTime(60000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setInterval(30000);
        //TODO: think about polling or notification on changes
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
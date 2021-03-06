package com.example.foodybiker;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    ArrayList<Reservation> reservations;
    Reservation activeReservation = null;
    private LatLngBounds.Builder builder;
    private LatLngBounds.Builder builderUserRest;
    private MainActivity father;
    private Location currLoc;
    private Geocoder geocoder;

    public void setFather(MainActivity father){
        this.father = father;
    }

    public MapFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        locationRequest = new LocationRequest();
        builder = new LatLngBounds.Builder();
        builderUserRest = new LatLngBounds.Builder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mapFragment.getContext());

        mLocationPermissionGranted = ContextCompat.checkSelfPermission(mapFragment.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        geocoder = new Geocoder(mapFragment.getContext(), Locale.getDefault());

        activeReservation = null;
        initRestFromDB();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mGoogleMap = mMap;

                Log.d("MAPPA", "getMapAsync");

                getDeviceLocation();
                updateLocationUI();

                mGoogleMap.animateCamera(CameraUpdateFactory.zoomBy(DEFAULT_ZOOM));
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (final Location location : locationResult.getLocations()) {
                    currLoc = location;
                    //Toast.makeText(father.getApplicationContext(), location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    final DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                            .child("Bikers/" + user.getUid());
                    HashMap<String, Object> child = new HashMap<>();
                    child.put("location", location);
                    database.updateChildren(child);

                    if(activeReservation == null) {
                        fetchRestaurant(location);
                    } else {
                        fetchUserAndRestaurant(location);
                    }
                }
            }
        };
    }

    private void initRestFromDB() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        reservations = new ArrayList<>();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("reservations").child("Bikers");
        Query query = database.child(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ReservationDBBiker reservationDB = ds.getValue(ReservationDBBiker.class);
                    if (reservationDB.getStatus() == null) {
                        Reservation reservation = new Reservation(reservationDB.getRestaurantName(), reservationDB.getRestaurantAddress(),
                                reservationDB.getOrderTimeBiker(), reservationDB.getUserName(), reservationDB.getUserAddress(),
                                reservationDB.getOrderTime(), reservationDB.getRestaurantID(),null, false);
                        reservation.setReservationID(ds.getKey());
                        reservations.add(reservation);
                    } else if (reservationDB.getStatus().equals("accepted")) {
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
                    loc = mFusedLocationProviderClient.getLastLocation().getResult();
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
            e.printStackTrace();
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
                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
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
            e.printStackTrace();
        }
    }

    private void startLocationUpdates() {
        mLocationPermissionGranted = ContextCompat.checkSelfPermission(mapFragment.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //locationRequest.setSmallestDisplacement(30.0f);
        locationRequest.setMaxWaitTime(60000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setInterval(20000);
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

    public void fetchRestaurant(Location location) {
        mGoogleMap.clear();

        activeReservation = null;

        builder = new LatLngBounds.Builder();
        builder.include(new LatLng(location.getLatitude(), location.getLongitude()));

        if(!reservations.isEmpty()) {
            for (int i = 0; i < reservations.size(); i++) {
                String addressRest = reservations.get(i).getRestaurantAddress();
                List<Address> lista = new ArrayList<>();

                try {
                    lista = geocoder.getFromLocationName(addressRest, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                LatLng latlngRest = new LatLng(lista.get(0).getLatitude(), lista.get(0).getLongitude());

                MarkerOptions mark = new MarkerOptions();
                mark.position(latlngRest);
                mark.title(reservations.get(i).getRestaurantName());
                mark.snippet(String.format(Locale.getDefault(), "%s %s", getActivity().getString(R.string.pick_time_text), reservations.get(i).getRestaurantPickupTime()));
                mark.icon(BitmapDescriptorFactory.fromResource(R.drawable.rest_icon));
                mGoogleMap.addMarker(mark);

                builder.include(latlngRest);
            }
            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 150);
            mGoogleMap.animateCamera(cu);
        }
    }

    public void fetchUserAndRestaurant(Location location) {
        final Location loc = location;
        List<Address> lista = new ArrayList<>();

        mGoogleMap.clear();

        String addressUser = activeReservation.getUserAddress();
        try {
            lista = geocoder.getFromLocationName(addressUser, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng latLngUser = new LatLng(lista.get(0).getLatitude(), lista.get(0).getLongitude());

        String addressRest = activeReservation.getRestaurantAddress();
        try {
            lista = geocoder.getFromLocationName(addressRest, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng latLngRestaurant = new LatLng(lista.get(0).getLatitude(), lista.get(0).getLongitude());

        MarkerOptions mark = new MarkerOptions();
        mark.position(latLngRestaurant);
        mark.title(activeReservation.getRestaurantName());
        mark.snippet(String.format(Locale.getDefault(), "%s %s", getActivity().getString(R.string.pick_time_text), activeReservation.getRestaurantPickupTime()));
        mark.icon(BitmapDescriptorFactory.fromResource(R.drawable.rest_icon));
        mGoogleMap.addMarker(mark);

        mark.position(latLngUser);
        mark.title(activeReservation.getUserName());
        mark.snippet(String.format(Locale.getDefault(), "%s %s", getActivity().getString(R.string.delivery_time_text), activeReservation.getUserDeliveryTime()));
        mark.icon(BitmapDescriptorFactory.defaultMarker());
        mGoogleMap.addMarker(mark);

        builderUserRest = new LatLngBounds.Builder();
        builderUserRest.include(new LatLng(loc.getLatitude(), loc.getLongitude()));
        builderUserRest.include(latLngRestaurant);
        builderUserRest.include(latLngUser);
        LatLngBounds bounds = builderUserRest.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 150);
        mGoogleMap.animateCamera(cu);
    }

    public void thereIsAnActiveRes(Reservation res) {
        activeReservation = res;
        if(!reservations.isEmpty())
            fetchUserAndRestaurant(currLoc);
    }

    public void noActive(Reservation res) {
        for(int i = 0; i < reservations.size(); i++) {
            if(reservations.get(i).getReservationID().equals(res.getReservationID())) {
                activeReservation = null;
                reservations.remove(i);
            }
        }
        fetchRestaurant(currLoc);
    }

    public void clearMap() {
        if(mGoogleMap != null)
            mGoogleMap.clear();
    }

    public void newReservationToDisplay(Reservation res) {
        reservations.add(res);
        fetchRestaurant(currLoc);
    }
}
package com.swakos;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.swakos.fragment.FavouritesFragment;
import com.swakos.fragment.HomeFragment;
import com.swakos.fragment.OrdersFragment;
import com.swakos.fragment.ProfileFragment;
import com.swakos.helper.HelperMethods;
import com.swakos.model.Order;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce = false;
    public static BottomNavigationView navigation;
    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;
    private static final String TAG = MainActivity.class.getSimpleName();

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;

    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private Thread thread;

    private HomeFragment homeFragment;
    private FavouritesFragment favouritesFragment;
    private OrdersFragment ordersFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = findViewById(R.id.navigation);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
        layoutParams.setBehavior(new HideBottomViewOnScrollBehavior());
        //Setting on BottomNavigationItemSelectedListener
        navigation.setItemIconTintList(null);
        navigation.setOnNavigationItemSelectedListener(listener);
        //navigation.setSelectedItemId(R.id.action_home);

        //Initialize the necessary libraries
        init();

        //Request for location permissions
        startLocationButtonClick();

        //Start Location Updates
        startLocationUpdates();

        // restore the values from saved instance state
        restoreValuesFromBundle(savedInstanceState);

        homeFragment = new HomeFragment();
        favouritesFragment = new FavouritesFragment();
        ordersFragment = new OrdersFragment();
        profileFragment = new ProfileFragment();

    }

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //Location is received
                mCurrentLocation = locationResult.getLastLocation();
                updateLocationUI(mCurrentLocation);
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Restoring values from saved instance state
     */
    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
                updateLocationUI(mCurrentLocation);
            }
            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
                updateLocationUI(mCurrentLocation);
            }
        }


    }
    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */
    private void updateLocationUI(Location mCurrentLocation) {
        if (mCurrentLocation != null) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses;
            try{
                addresses = geocoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
                HelperMethods.LOCATION = addresses.get(0).getSubLocality();
            }catch (IOException exception){
                //Toast.makeText(MainActivity.this, exception.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");
                        //Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();
                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        updateLocationUI(mCurrentLocation);
                        HelperMethods.loadFragment(homeFragment, MainActivity.this);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                //Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI(mCurrentLocation);
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check for the integer request code originally supplied to startResolutionForResult().
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.e(TAG, "User agreed to make required location settings changes.");
                    // Nothing to do. startLocationUpdates() gets called in onResume again.
                    //updateLocationUI(mCurrentLocation);
                    mRequestingLocationUpdates = true;
                    break;
                case Activity.RESULT_CANCELED:
                    Log.e(TAG, "User chose not to make required location settings changes.");
                    mRequestingLocationUpdates = false;
                    break;
            }
        }
    }

    public void stopLocationUpdates() {
        mRequestingLocationUpdates = false;
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void startLocationButtonClick() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        //mRequestingLocationUpdates = true;
                        checkPermissions();
                        //startLocationUpdates();
                        //updateLocationUI(mCurrentLocation);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Resuming location updates depending on button state and
        // allowed permissions
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
            updateLocationUI(mCurrentLocation);
        }

    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onPause() {
        //Pausing Location Updates
        if (mRequestingLocationUpdates) {
            //Pausing Location Updates
            stopLocationUpdates();
        }
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    BottomNavigationView.OnNavigationItemSelectedListener listener = item -> {
        switch (item.getItemId()){
            case R.id.action_profile:
                //HelperMethods.hideFragment(homeFragment, MainActivity.this);
                //HelperMethods.hideFragment(favouritesFragment, MainActivity.this);
                //HelperMethods.hideFragment(ordersFragment, MainActivity.this);
                //HelperMethods.showFragment(profileFragment, MainActivity.this);
                HelperMethods.loadFragment(profileFragment, MainActivity.this);
                return true;
            case R.id.action_home:
                //HelperMethods.hideFragment(profileFragment, MainActivity.this);
                //HelperMethods.hideFragment(favouritesFragment, MainActivity.this);
                //HelperMethods.hideFragment(ordersFragment, MainActivity.this);
                //HelperMethods.showFragment(homeFragment, MainActivity.this);
                HelperMethods.loadFragment(homeFragment, MainActivity.this);
                return true;
            case R.id.action_categories:
                //HelperMethods.hideFragment(homeFragment, MainActivity.this);
                //HelperMethods.hideFragment(ordersFragment, MainActivity.this);
                //HelperMethods.hideFragment(profileFragment, MainActivity.this);
                //HelperMethods.showFragment(favouritesFragment, MainActivity.this);
                HelperMethods.loadFragment(new FavouritesFragment(), MainActivity.this);
                return true;
            case R.id.action_orders:
                //HelperMethods.hideFragment(homeFragment, MainActivity.this);
                //HelperMethods.hideFragment(favouritesFragment, MainActivity.this);
                //HelperMethods.hideFragment(profileFragment, MainActivity.this);
                //HelperMethods.showFragment(ordersFragment, MainActivity.this);
                HelperMethods.loadFragment(new OrdersFragment(), MainActivity.this);
                return true;
        }
        return false;
    };

    BottomNavigationView.OnNavigationItemReselectedListener reselectedListener = new BottomNavigationView.OnNavigationItemReselectedListener() {
        @Override
        public void onNavigationItemReselected(@NonNull MenuItem item) {
            switch (item.getItemId()){

            }

        }
    };

}

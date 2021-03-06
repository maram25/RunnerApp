package com.example.runner.Home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;

import com.example.runner.Constants;
import com.example.runner.Details.DetailsMapFaragment;
import com.example.runner.Details.GetLocationModel;
import com.example.runner.History.HistoryModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.runner.History.HistoryFragment;
import com.example.runner.R;
import com.example.runner.Utils;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener ,
        SensorEventListener  , Constants {
 /*   SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();*/
 public MutableLiveData<GetLocationModel> SelectedLocation = new MutableLiveData<>();

    ConstraintLayout StartAction;
    TextView Start;
    TextView steps;
    TextView Distance;
    TextView Time;
    ImageView Stop, Resume;
    private ArrayList<HistoryModel> historyModels;
    LocationManager location_manager;

    FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    private GoogleMap mMap;
    TextView mLocationMarkerText;
    private GoogleApiClient mGoogleApiClient;
    Boolean CountSensor;
    Context mContext;
    Chronometer chronometer;
    private LatLng mCenterLatLong;
    LocationModel pickedLocation;
    LatLng mOrigin, mDestination = null, CurrentlatLng;
    Sensor sensor;
    SensorManager sensorManager;
    private double MagnitudePrevious = 0;
    private int stepCount = 0;
    private float Distances = 0;

    long lastPause;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLocationPermission(getApplicationContext(), this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
/*
        getCurrentLocation();
*/        loadData();

        Definations();
        Actions();
        buildGoogleApiClient();




    }

    private void StartCounter() {
        if (lastPause != 0){
            chronometer.setBase(chronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
        }
        else{
            chronometer.setBase(SystemClock.elapsedRealtime());
        }

        chronometer.start();
    }
    private void StartStepSensor()
    {
        SensorEventListener stepDetector = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent!= null){
                    float x_acceleration = sensorEvent.values[0];
                    float y_acceleration = sensorEvent.values[1];
                    float z_acceleration = sensorEvent.values[2];
                    double Magnitude = Math.sqrt(x_acceleration*x_acceleration + y_acceleration*y_acceleration + z_acceleration*z_acceleration);
                    double MagnitudeDelta = Magnitude- MagnitudePrevious;
                    MagnitudePrevious = Magnitude;
                    if (MagnitudeDelta > 4){
                        stepCount++;
                    }
                    steps.setText(stepCount+" step");
                }}
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }
    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(historyModels);
        editor.putString("history list", json);
        editor.apply();
    }
    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 1) {
            finish();
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private void Definations() {
        StartAction = findViewById(R.id.start_action);
        Start = findViewById(R.id.Start);
        Resume = findViewById(R.id.Resume);
        Stop = findViewById(R.id.Stop);
        steps = findViewById(R.id.steps);
        Distance = findViewById(R.id.Distance);
        chronometer = findViewById(R.id.chronometer);
       // Time = findViewById(R.id.Time);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void Actions() {
        Resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastPause = SystemClock.elapsedRealtime();
                chronometer.stop();
            }
        });
        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartAction.setVisibility(View.VISIBLE);
                Start.setVisibility(View.GONE);

                StartTimeDate();
                StartCounter();

                Log.d("StartAction", "StartAction");
                StartStepSensor();
                getDistanceRun(stepCount);


            }
        });
        Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.Distance=Distances;
                Utils.CountStep=stepCount;
                Utils.TimeTaken=chronometer.toString();
               GetLocation(new LocationModel(CurrentlatLng.latitude, CurrentlatLng.longitude));

                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
                lastPause = 0;
                stepCount=0;
                for (int i = 0; i < historyModels.size(); i++) {
                    historyModels.get(i).setDistance(Distances+"");
                    historyModels.get(i).setSteps(stepCount);
                    historyModels.get(i).setDate(Date);
                    historyModels.get(i).setTime(Utils.Time);
                }
                saveData();
               // goToFragment(new HistoryFragment());
                goToFragment(new DetailsMapFaragment());

            }
        });


    }


    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("history list", null);
        Type type = new TypeToken<ArrayList<HistoryModel>>() {}.getType();
        historyModels = gson.fromJson(json, type);

        if (historyModels == null) {
            historyModels = new ArrayList<>();
        }
    }

    private void StartTimeDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss aaa z");
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("yyyy-MM-dd");
        //  SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm");

        String dateTime = simpleDateFormat.format(calendar.getTime()).toString();
        String dateCalande = simpleDateFormatDate.format(calendar.getTime()).toString();
        String TimeCalendar = simpleDateFormatTime.format(calendar.getTime()).toString();

        Log.d("Date", dateCalande+"");
        Log.d("Time", TimeCalendar+"");
        Utils.Date=dateCalande;
        Utils.Time=TimeCalendar;
    }

    public void goToFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.main, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    protected void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);


        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    public void checkLocationPermission(Context context, FragmentActivity activity) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(context)
                        .setTitle(("Location Permission Needed"))
                        .setMessage(("This app needs the Location permission, please accept to use location functionality"))
                        .setPositiveButton(("OK"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);

                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
//                RequestPermission.setValue(true);
            }
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                    }

                } else {
                    // if not allow a permission, the application will exit
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    //  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                    finish();
                    System.exit(0);
                }
            }
        }
    }

  /*  private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder;
                    List<Address> addresses = null;
                    geocoder = new Geocoder(mContext, Locale.getDefault());
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        Utils.SrartLat = addresses.get(0).getLatitude();
                        Utils.StartLong = addresses.get(0).getLongitude();
                        Log.e(" Utils.SrartLat ", Utils.SrartLat + "");
                        Log.e(" Utils.StartLong", Utils.StartLong + "");

                        mOrigin = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());

                    } catch (NullPointerException e) {
                    } catch (IndexOutOfBoundsException e) {
                    }
                }
            }
        });
    }*/

    private void changeMap(Location location) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // check if map is created successfully or not
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;
            latLong = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(19f).build();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            mLocationMarkerText.setText("Lat : " + location.getLatitude() + "," + "Long : " + location.getLongitude());
//            LocationModel locationModel = new LocationModel(location.getLatitude(), location.getLongitude());
//            Log.e("setLoc", locationModel.getLat() + " , " + location.getLongitude());

            startIntentService(location);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            changeMap(mLastLocation);

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null)
                changeMap(location);
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLngBounds EgyptBounds = new LatLngBounds(
                new LatLng(24.0546094, 26.3211317),   // SW bounds
                new LatLng(31.191445, 34.1435243)  // NE bounds
        );
//        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(EgyptBounds, 0));
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;
                mMap.clear();
                try {
                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);
                    startIntentService(mLocation);
                    mLocationMarkerText.setText("Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude);
                    pickedLocation = new LocationModel(mLocation.getLatitude(), mLocation.getLongitude());
                    Utils.SrartLat = mLocation.getLatitude();
                    Utils.StartLong = mLocation.getLongitude();

                    Utils.EndLat = mLocation.getLatitude();
                    Utils.EndLong = mLocation.getLongitude();
                    Log.e("setLoc", mLocation.getLatitude() + " , " + mLocation.getLongitude());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
//         mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this /* FragmentActivity */,
//                        this /* OnConnectionFailedListener */)
//                .addApi(Drive.API)
//                .addScope(Drive.SCOPE_FILE)
//                .build();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent!= null){
            float x_acceleration = sensorEvent.values[0];
            float y_acceleration = sensorEvent.values[1];
            float z_acceleration = sensorEvent.values[2];
            double Magnitude = Math.sqrt(x_acceleration*x_acceleration + y_acceleration*y_acceleration + z_acceleration*z_acceleration);
            double MagnitudeDelta = Magnitude -MagnitudePrevious;
            MagnitudePrevious = Magnitude;
            if (MagnitudeDelta > 4){
                stepCount++;
            }
            steps.setText(stepCount);
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        stepCount = sharedPreferences.getInt("stepCount", 0);
    }
    @Override
    protected void onPause() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        super.onPause();
        editor.clear();
        editor.putInt("stepCount", stepCount);
        editor.putFloat("Distance", Distances);

        editor.apply();
    }
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount", stepCount);
        editor.putFloat("Distance", Distances);
        editor.apply();
    }
    public float getDistanceRun(long steps){
        float distance = (float)(steps*78)/(float)100000;
        Utils.Distance=distance;
        Distance.setText( Utils.Distance+"km");
        return distance;
    }
    public void GetLocation(LocationModel locationModel) {
        mCenterLatLong = new LatLng(30.0594838, 31.22344);
        onMapReady(mMap);
        }
}
package com.sweteam5.ladybugadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DriverActivity extends AppCompatActivity {
    private TextView locationTextView;              // (TEST) GPS location textview

    private boolean isInOperation = false;          // Whether it's in operation
    private Spinner busNumberSpinner;               // Dropdown of bus number that driver is driving
    private TextView currentLocationTextView;       // Current location of bus in text
    private TextView currentStateTextView;          // Display whether it's in operation in text
    private Button changeStatusButton;              // Change the operation state

    LocationListener locationListener;              // GPS location listener
    private LocationManager locationManager = null; // Location manager for GPS feature

    private LinearLayout startStationContainer;     // Group of starting station selection
    private LinearLayout currentLocationContainer;  // Group of current station display
    private Spinner startStationSpinner;            // Dropdown of starting station selection
    private boolean isPassedStartingPoint;          // Check if driver have passed the starting point

    private BusLocator busLocator;                  // Bus location calculator
    private StationDataManager stationDataManager;  // Station data loading instance

    private boolean[] busOperationCheckList;        // Boolean array to check if the bus is running

    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1001; // Location Permission code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        // Caching views that is drawn in activity
        locationTextView = findViewById(R.id.locationTextView);
        busNumberSpinner = findViewById(R.id.busNumberSpinner);
        currentStateTextView = findViewById(R.id.currentStateTextView);
        changeStatusButton = findViewById(R.id.changeStatusButton);
        currentLocationTextView = findViewById(R.id.currentLocationTextView);
        startStationContainer = findViewById(R.id.startStationContainer);
        currentLocationContainer = findViewById(R.id.currentLocationContainer);
        startStationSpinner = findViewById(R.id.startStationSpinner);

        // Set operation state button listener
        changeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOperation(!isInOperation);
            }
        });

        // Permission Check for fine GPS feature
        permissionCheckGps();

        // Initialize the station data information
        stationDataManager = new StationDataManager();
        stationDataManager.init(this);

        // Initialize bus location calculator
        busLocator = new BusLocator(stationDataManager);

        // Initialize starting station list dropdown
        initStartStationSpinner();

        // Initialize operation state
        setOperation(false);

        // Get the bus service status list.
        setOperationBus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // When the activity ends, stop processing is performed.
        setOperation(false);
    }

    // Get and set the bus service status list from Firebase
    private void setOperationBus() {
        FirebaseDatabase.getInstance().getReference("Operation").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    busOperationCheckList = FirebaseConverter.convertString2BoolList(task.getResult().getValue().toString());

                    updateAvailableBus();
                }
            }
        });
    }

    // Update the list of available bus numbers.
    private void updateAvailableBus() {
        // Generate a list of available bus numbers
        ArrayList<Integer> operationableBus = new ArrayList<>();
        for(int i = 0; i < busOperationCheckList.length; i++) {
            if(!busOperationCheckList[i])
                operationableBus.add(i + 1);
        }

        // Convert to Integer class array
        Integer[] busNumbers = new Integer[operationableBus.size()];
        for (int i = 0; i < operationableBus.size(); i++)
            busNumbers[i] = Integer.valueOf(operationableBus.get(i));

        // Set the available bus number to the drop-down
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, busNumbers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        busNumberSpinner.setAdapter(adapter);
    }

    // Initialize starting station dropdown
    private void initStartStationSpinner() {
        // Get station name array from station data manager
        String[] items = stationDataManager.getStationNameArray();

        // Set spinner dropdown adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startStationSpinner.setAdapter(adapter);
    }

    // Set operation state
    private void setOperation(boolean active) {
        isInOperation = active;
        setBusOperationToServer(active);
        if (isInOperation) {
            /* Swap StartStation and CurrentLocation */
            startStationContainer.setVisibility(View.GONE);
            currentLocationContainer.setVisibility(View.VISIBLE);

            /* Initialize with starting station information */
            String stationName = startStationSpinner.getSelectedItem().toString();
            currentLocationTextView.setText(stationName + "(으)로 향하는 중");
            busLocator.initStartIndex(busLocator.getIndexByName(stationName), getCurrentBusNum());
            isPassedStartingPoint = false;

            /* Change design of current state and change status button */
            currentStateTextView.setBackground(getResources().getDrawable(R.drawable.state_background_active));
            currentStateTextView.setText(getResources().getString(R.string.in_operation));
            currentStateTextView.setTextColor(Color.BLACK);
            changeStatusButton.setBackground(getResources().getDrawable(R.drawable.state_background_unactive));
            changeStatusButton.setText(getResources().getString(R.string.stop_driving));
            changeStatusButton.setTextColor(Color.WHITE);
            startGetLocation();
        } else {
            /* Swap StartStation and CurrentLocation */
            startStationContainer.setVisibility(View.VISIBLE);
            currentLocationContainer.setVisibility(View.GONE);

            /* Change design of current state and change status button */
            currentStateTextView.setBackground(getResources().getDrawable(R.drawable.state_background_unactive));
            currentStateTextView.setText(getResources().getString(R.string.stopped));
            currentStateTextView.setTextColor(Color.WHITE);
            changeStatusButton.setBackground(getResources().getDrawable(R.drawable.state_background_active));
            changeStatusButton.setText(getResources().getString(R.string.start_driving));
            changeStatusButton.setTextColor(Color.BLACK);
            stopGetLocation();
            busLocator.initStartIndex(-1, getCurrentBusNum());
        }
        // Set bus number editable/uneditable
        busNumberSpinner.setEnabled(!active);
    }

    // Get selected bus number from spinner(drop-down)
    public String getCurrentBusNum() {
        return busNumberSpinner.getSelectedItem().toString();
    }

    // Send whether the current bus number is running or not to the server
    private void setBusOperationToServer(boolean active) {
        String busNum = getCurrentBusNum();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Operation");
        db.child(busNum).setValue(active).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

    // Check GPS Permissions (Android policy)
    private void permissionCheckGps() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "버스의 위치를 공유하기 위해 위치 정보가 필요합니다.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                Toast.makeText(this, "버스의 위치를 공유하기 위해 위치 정보가 필요합니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // GPS permission acquisition callback function
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "권한이 승인되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "아직 권한이 승인되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            return;
        }
    }

    // Event handler start method that receives GPS location information
    private void startGetLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            locationTextView.setText( "위도 : " + latitude+ ", 경도 : " + longitude + ", 거리 : " + busLocator.getDistance(location)
                    + ", Index: " + busLocator.getCurrentIndex());

        }
        // Start GPS location update event listener (400ms interval)
        locationListener = gpsLocationListener;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, locationListener);

    }

    // Stop receiving GPS location
    private void stopGetLocation() {
        if(locationManager != null){
            locationManager.removeUpdates(locationListener);
        }
    }

    // GPS event call back method
    final LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            locationTextView.setText( "위도 : " + latitude+ ", 경도 : " + longitude + ", 거리 : " + busLocator.getDistance(location)
                    + ", Index: " + busLocator.getCurrentIndex());
            // Set current location of bus
            setCurrentLocation(location);
        }
    };

    // Set current location of bus with current GPS location
    private void setCurrentLocation(Location currentLocation) {
        // Check if the bus has passed the departure station
        if(!isPassedStartingPoint) {
            // If the bus approaches the departure station within the error range,
            // change the isPassedStartingPoint to true and update the current location information.
            double dist = busLocator.getDistance(currentLocation);
            if(dist < BusLocator.ERROR_RANGE) {
                currentLocationTextView.setText(busLocator.getCurrentStationName());
                isPassedStartingPoint = true;
                busLocator.setCurrentIndex(currentLocation, getCurrentBusNum());
            }
        }
        else {
            //If it is in a normal driving state(already passed starting station), update the index.
            busLocator.setCurrentIndex(currentLocation, getCurrentBusNum());

            // If the currentIndex is an even number, it displays only the name of the station,
            // and if it is an odd number, it indicates where it is moving from.
            int currentIndex = busLocator.getCurrentIndex();
            if(currentIndex % 2 == 0)
                currentLocationTextView.setText(busLocator.getCurrentStationName());
            else
                currentLocationTextView.setText(busLocator.getCurrentStationName() + " → " + busLocator.getNextStationName());
        }
    }
}
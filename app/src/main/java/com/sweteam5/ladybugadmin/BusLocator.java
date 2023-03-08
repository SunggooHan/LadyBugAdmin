package com.sweteam5.ladybugadmin;

import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BusLocator {
    // Current location index of bus
    // If the currentIndex is odd, it is between stations('between state'), and if it is even, it is in stations('in state')
    private int currentIndex = 0;

    // Station data loading instance
    private static StationDataManager stationDataManager = null;

    // Margin of error distance in meter
    public static int ERROR_RANGE = 10;

    // Firebase instance to communicate with Firebase Realtime Database
    FirebaseDatabase indexDatabase = FirebaseDatabase.getInstance();
    DatabaseReference locationRef = indexDatabase.getReference("Location");


    // Caching the stationDataManager
    public BusLocator(StationDataManager pStationDataManager) {
        if(stationDataManager == null)
            stationDataManager = pStationDataManager;
    }

    // Set starting location of bus
    public void initStartIndex(int startIndex, String busNum) {
        currentIndex = startIndex;
        updateIndex(currentIndex, busNum);
    }

    // Get distance from current GPS location to next station in meter.
    public double getDistance (Location currentLocation) {
        Station station = stationDataManager.stations[(currentIndex + 1) / 2];

        Location stationLocation = new Location(station.getName());
        stationLocation.setLatitude(station.getLatitude());
        stationLocation.setLongitude(station.getLongitude());
        return currentLocation.distanceTo(stationLocation);
    }

    // Check if current GPS location is within error range from station
    private boolean isNearStation(Location currentLocation) {
        double distance = getDistance(currentLocation);

        if(distance < ERROR_RANGE) {
            return true;
        }
        else {
            return false;
        }
    }

    // Check and set whether the current location index needs to be changed
    public void setCurrentIndex(Location currentLocation, String busNum) {
        // Check if user is near the station
        boolean isNear = isNearStation(currentLocation);

        // If it is near the next station and index is in 'between state', add 1 to the index
        if(isNear && currentIndex % 2 == 1) {
            currentIndex++;
            updateIndex(currentIndex, busNum);
        }
        // If it is not near the station and index is in 'in state', add 1 to the index
        else if(!isNear && currentIndex % 2 == 0){
            currentIndex++;
            updateIndex(currentIndex, busNum);
        }

        // If the index exceeds the range, it initializes to zero
        if(currentIndex >= (stationDataManager.stations.length - 1) * 2) {
            currentIndex = 0;
            updateIndex(currentIndex, busNum);
        }
    }

    // Get currentIndex
    public int getCurrentIndex() {
        return currentIndex;
    }

    // Find the location index of station by its name
    public int getIndexByName(String name) {
        for(int i = 0; i < stationDataManager.stations.length; i++) {
            if(stationDataManager.stations[i].getName().equals(name))
                return i * 2;
        }
        return -1;
    }

    // Get current('in state') / prior('between state') station name
    public String getCurrentStationName() {
        return stationDataManager.stations[currentIndex / 2].getName();
    }

    // Get next('between state') / current('in state') station name
    public String getNextStationName() {
        return stationDataManager.stations[(currentIndex + 1) / 2].getName();
    }

    // Send(update) bus location index data to firebase database
    public void updateIndex(int currentIndex, String busNum){
        locationRef.child("LocationIndex_" + busNum).setValue(currentIndex).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
}

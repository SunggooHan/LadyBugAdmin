package com.sweteam5.ladybugadmin;

import android.app.ActivityManager;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BusLocationActivity extends AppCompatActivity {

    private FrameLayout busLayout;      // Layout of bus will be drawn
    private BusLineView busLineView;    // Line drawing View instance
    private BusView[] busViewsList = new BusView[3]; // Array for storing drawn bus views

    private int[] busLocations;         // Index array of the current location of the buses

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_location);

        // Create bus line view instance and add to layout
        busLayout = findViewById(R.id.busDrawContainer);
        busLineView = new BusLineView(this);
        FrameLayout lineLayout = findViewById(R.id.busLineContainer);
        lineLayout.addView(busLineView);

        // Get Bus location from Firebase Realtime Database
        getBusLocationFromServer();
    }

    // Draw bus image, add to layout, and return the instance
    private void drawBus(int locIndex, int busNum)
    {
        BusView busView = new BusView(this, busLineView);

        busViewsList[busNum] = busView;
        busLayout.addView(busViewsList[busNum]);
        busViewsList[busNum].updateLocation(locIndex);
    }

    // Delete bus image from bus container layout
    private void deleteBus(int busNum) {
        busViewsList[busNum].stopAnimation();
        busLayout.removeView(busViewsList[busNum]);
        busViewsList[busNum] = null;
    }

    // The location index of the bus is retrieved from the firebase in real time.
    private void getBusLocationFromServer() {
        // The data in the "Location" path of the firebase is called back when there is a change.
        FirebaseDatabase.getInstance().getReference("Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // The string data read from the server is converted into an int-type array
                // to update the location of all buses.
                String message = snapshot.getValue().toString();
                busLocations = FirebaseConverter.convertMsg2BusLocation(message);
                setBusLocations();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // Update the location of the bus according to the conditions and redraw it.
    private void setBusLocations() {
        for(int i = 0; i < busViewsList.length; i++) {
            // If the bus is switched to a stop(not driving), delete the bus.
            if(busViewsList[i] != null && busLocations[i] == -1)
                deleteBus(i);
            // If the bus is detected as newly created, add a bus image.
            else if(busViewsList[i] == null && busLocations[i] != -1)
                drawBus(busLocations[i], i);
            // If the bus is running, it will update the location.
            else if(busViewsList[i] != null && busLocations[i] != -1)
                busViewsList[i].updateLocation(busLocations[i]);

        }
    }


}
package com.sweteam5.ladybugadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AdminMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        /* Entering the notification management Menu */
        TextView noticeMngTextView = findViewById(R.id.noticeMngTextview);
        noticeMngTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NoticeMngActivity.class);
                startActivity(intent);
            }
        });

        /* Entering the bus location Menu */
        TextView busLocationTextView = findViewById(R.id.busLocationTextview);
        busLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BusLocationActivity.class);
                startActivity(intent);
            }
        });


        /* Entering the manage information Menu */
        TextView mngInfoTextView = findViewById(R.id.mngInfoTextview);
        mngInfoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MngInfoActivity.class);
                startActivity(intent);
            }
        });
    }
}
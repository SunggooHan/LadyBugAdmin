package com.sweteam5.ladybugadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class LoginActivity extends AppCompatActivity {

    // Firebase instance for admin code to communicate with Firebase Realtime Database
    private DatabaseReference databaseReference_admin;
    // Firebase instance for driver code to communicate with Firebase Realtime Database
    private DatabaseReference databaseReference_driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get instances from Firebase by path
        databaseReference_admin = FirebaseDatabase.getInstance().getReference("admin");
        databaseReference_driver = FirebaseDatabase.getInstance().getReference("driver");

        // Set views that is drawn in activity
        EditText checkId = findViewById(R.id.checkId);
        Button loginButton = findViewById(R.id.loginButton);
        RadioButton radAdminMode = findViewById(R.id.radAdmin);
        RadioButton radDriverMode = findViewById(R.id.radDriver);

        // Set login button listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If login type is admin, get admin code and compare with user's input
                if (radAdminMode.isChecked()) {
                    databaseReference_admin.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                            while (child.hasNext()) {
                                // If the code is valid, start the admin menu activity with toast message
                                if (child.next().getKey().equals(checkId.getText().toString())) {
                                    Toast.makeText(getApplicationContext(), "로그인!", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);

                                    if (intent != null)
                                        startActivity(intent);
                                    else
                                        Toast.makeText(getApplicationContext(), "로그인 종류를 정해주세요.", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                            // If code is not valid, show the toast message.
                            Toast.makeText(getApplicationContext(), "존재하지 않는 아이디입니다.", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                }
                // If login type is driver, get driver code and compare with user's input
                else if(radDriverMode.isChecked()){
                    databaseReference_driver.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                            while (child.hasNext()) {
                                // If the code is valid, start the driver activity with toast message
                                if (child.next().getKey().equals(checkId.getText().toString())) {
                                    Toast.makeText(getApplicationContext(), "로그인!", Toast.LENGTH_LONG).show();

                                    Intent intent = null;
                                    intent = new Intent(getApplicationContext(), DriverActivity.class);

                                    if (intent != null)
                                        startActivity(intent);
                                    else
                                        Toast.makeText(getApplicationContext(), "로그인 정류를 정해주세요.", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                            // If code is not valid, show the toast message.
                            Toast.makeText(getApplicationContext(), "존재하지 않는 아이디입니다.", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                }
            }
        });
    }
}
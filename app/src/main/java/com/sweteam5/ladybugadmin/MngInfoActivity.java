package com.sweteam5.ladybugadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MngInfoActivity extends AppCompatActivity{
    private ArrayList<CodeInfo> driverInfoList; // Driver information (CodeInfo typed) ArrayList
    private ArrayList<CodeInfo> adminInfoList;  // Adu=min information (CodeInfo typed) ArrayList
    private LinearLayout driverCodeContainer;   // Container field layout that driver code can be generated.
    private LinearLayout adminCodeContainer;    // Container field layout that admin code can be generated.

    private DatabaseReference db;               // Firebase instance to communicate with Firebase Realtime Database

    public enum CodeType {ADMIN, DRIVER}        // Code type enum (Admin / Driver)

    private ArrayList<String> adminCodes;       // Admin codes that loaded from firebase
    private ArrayList<String> driverCodes;      // Driver codes that loaded from firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mng_info);

        // Set views that is drawn in activity
        adminCodeContainer = findViewById(R.id.adminCodeContainer);
        driverCodeContainer = findViewById(R.id.driverCodeContainer);

        // Initialize ArrayList
        adminInfoList = new ArrayList<CodeInfo>();
        driverInfoList = new ArrayList<CodeInfo>();
        adminCodes = new ArrayList<>();
        driverCodes = new ArrayList<>();

        // Set Add Admin button listener
        Button addAdminButton = findViewById(R.id.addAdminButton);
        addAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set code EditText to be added after create
                EditText input = new EditText(MngInfoActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);

                // Set the dialog to receive the new code
                AlertDialog.Builder dlg = new AlertDialog.Builder(MngInfoActivity.this);
                dlg.setTitle("Add admin Code");
                dlg.setView(input);
                // Set Add & Cancel button listener of dialog
                dlg.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addAdminCodeOnServer(input.getText().toString());
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { }
                });
                // Show the dialog
                AlertDialog alert = dlg.create();
                alert.show();
            }
        });

        // Set Add Driver button listener
        Button addDriverButton = findViewById(R.id.addDriverButton);
        addDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set code EditText to be added after create
                EditText input = new EditText(MngInfoActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);

                // Set the dialog to receive the new code
                // and set Add & Cancel button listener of dialog
                AlertDialog.Builder dlg = new AlertDialog.Builder(MngInfoActivity.this);
                dlg.setTitle("Add driver Code").setView(input).setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addDriverCodeOnServer(input.getText().toString());
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                // Show the dialog
                AlertDialog alert = dlg.create();
                alert.show();
            }
        });

        // Get admin codes from Firebase
        FirebaseDatabase.getInstance().getReference("admin").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                // If loading data is successful, convert loaded data to String typed codes
                if(task.isSuccessful()) {
                    String[] codes = FirebaseConverter.convertDict2CodeList(task.getResult().getValue().toString());

                    // Add String array data to admin code array list
                    for(int i = 0; i < codes.length; i++) {
                        adminCodes.add(codes[i]);
                    }

                    // Initialize Admin code UI
                    initAdminCode();
                }
            }
        });

        // Get driver codes from Firebase
        FirebaseDatabase.getInstance().getReference("driver").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                // If loading data is successful, convert loaded data to String typed codes
                if(task.isSuccessful()) {
                    System.out.println(task.getResult().getValue().toString());
                    String[] codes = FirebaseConverter.convertDict2CodeList(task.getResult().getValue().toString());

                    // Add String array data to driver code array list
                    for(int i = 0; i < codes.length; i++) {
                        driverCodes.add(codes[i]);
                    }

                    // Initialize Driver code UI
                    initDriverCode();
                }
            }
        });
    }

    // Initialize(add) admin code UI(EditText)
    private void initAdminCode() {
        for(int i = 0; i < adminCodes.size(); i++) {
            addAdminCode();
            adminInfoList.get(i).initCode(adminCodes.get(i));
        }
    }

    // Initialize(add) Driver code UI(EditText)
    private void initDriverCode() {
        for(int i = 0; i < driverCodes.size(); i++) {
            addDriverCode();
            driverInfoList.get(i).initCode(driverCodes.get(i));
        }
    }

    // Add driver code to driver code list (UI View and ArrayList)
    private void addDriverCode() {
        CodeInfo newDriverCode = new CodeInfo(getApplicationContext(), getResources().getString(R.string.driver)
                + " " + driverInfoList.size(), CodeType.DRIVER, this);
        driverInfoList.add(newDriverCode);
        driverCodeContainer.addView(newDriverCode);
    }

    // Add driver code and and initialize it
    private void addDriverCode(String code) {
        addDriverCode();
        driverCodes.add(code);
        driverInfoList.get(driverInfoList.size() - 1).initCode(code);
    }

    // Add admin code to admin code list (UI View and ArrayList)
    private void addAdminCode() {
        CodeInfo newBusCode = new CodeInfo(getApplicationContext(), getResources().getString(R.string.admin_abbrev)
                + " " + adminInfoList.size(), CodeType.ADMIN, this);
        adminInfoList.add(newBusCode);
        adminCodeContainer.addView(newBusCode);
    }

    // Add admin code and and initialize it
    private void addAdminCode(String code) {
        addAdminCode();
        adminCodes.add(code);
        adminInfoList.get(adminInfoList.size() - 1).initCode(code);
    }

    // Delete existing code from UI and Firebase Database
    public void deleteCode(MngInfoActivity.CodeType type, CodeInfo codeInfo) {
        // If the existing admin code is the last code, it prevents deletion.
        if(type == CodeType.ADMIN && adminCodes.size() <= 1) {
            Toast.makeText(this, "마지막 관리자 코드는 지울 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show the confirmation dialog for code deletion.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("코드 삭제").setMessage("로그인 코드를 삭제하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(type == CodeType.DRIVER) {
                            // Request the server to delete the driver codes
                            db = FirebaseDatabase.getInstance().getReference("driver");
                            db.child(codeInfo.getCodeOnEditText()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // If successful, remove code from UI view and ArrayList.
                                    if(task.isSuccessful()){
                                        Toast.makeText(MngInfoActivity.this, "성공적으로 삭제했습니다.", Toast.LENGTH_SHORT).show();

                                        driverCodeContainer.removeView(codeInfo);
                                        driverInfoList.remove(codeInfo);
                                        driverCodes.remove(codeInfo.getCodeOnEditText());
                                        updateTitleAll(driverInfoList, getResources().getString(R.string.driver));
                                    }
                                    // If it fails, it shows a failure message.
                                    else{
                                        Toast.makeText(MngInfoActivity.this, "삭제를 하지 못했습니다..", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else if(type == CodeType.ADMIN) {
                            // Request the server to delete the admin codes
                            db = FirebaseDatabase.getInstance().getReference("admin");
                            db.child(codeInfo.getCodeOnEditText()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // If successful, remove code from UI view and ArrayList.
                                    if(task.isSuccessful()){
                                        Toast.makeText(MngInfoActivity.this, "성공적으로 삭제했습니다.", Toast.LENGTH_SHORT).show();

                                        adminCodeContainer.removeView(codeInfo);
                                        adminInfoList.remove(codeInfo);
                                        adminCodes.remove(codeInfo.getCodeOnEditText());
                                        updateTitleAll(adminInfoList, getResources().getString(R.string.admin_abbrev));
                                    }
                                    // If it fails, it shows a failure message.
                                    else{
                                        Toast.makeText(MngInfoActivity.this, "삭제를 하지 못했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { }
                });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Update the code title from number 0 in order.
    private void updateTitleAll(ArrayList<CodeInfo> list, String title) {
        for(int i = 0; i < list.size(); i++) {
            list.get(i).setTitle(title + " " + i);
        }
    }

    // Add admin code to the Firebase database.
    public void addAdminCodeOnServer(String code) {
        db = FirebaseDatabase.getInstance().getReference();
        db.child("admin").child(code).setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
            // If successful, add the admin code and show the message.
            @Override
            public void onSuccess(Void unused) {
                addAdminCode(code);

                String toast = "관리자 코드  " + code + "가 추가되었습니다.";
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Add driver code to the Firebase database.
    public void addDriverCodeOnServer(String code) {
        db = FirebaseDatabase.getInstance().getReference();
        db.child("driver").child(code).setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
            // If successful, add the driver code and show the message.s
            @Override
            public void onSuccess(Void unused) {
                addDriverCode(code);

                String toast = "운전자 코드 " + code + "가 추가되었습니다.";
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
            }
        });
    }
}
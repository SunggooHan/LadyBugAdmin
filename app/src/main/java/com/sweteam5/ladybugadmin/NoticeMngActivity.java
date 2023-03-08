package com.sweteam5.ladybugadmin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NoticeMngActivity extends AppCompatActivity implements RecyclerViewInterface{
    RecyclerView recyclerView;              // RecyclerView that displays the notice list
    ArrayList<NoticeInfo> noticeArrayList;  // Notice data array list
    NoticeAdapter noticeAdapter;            // Notice Adapter for recycler view
    ProgressDialog progressDialog;          // Progress dialog for loading from server
    static DataManage dm;                   // Object communicating with the server about the notice

    // Refresh the list after user have been to another activity(NoticeWriteActivity).
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        updateNoticeList();
    }

    // Refresh the notice list with the latest
    public void updateNoticeList() {
        noticeArrayList = new ArrayList<NoticeInfo>();
        noticeAdapter = new NoticeAdapter(NoticeMngActivity.this, noticeArrayList, this);
        recyclerView.setAdapter(noticeAdapter);
        // Get noticeList from server and set notice list with noticeAdapter
        dm.showNoticeList(progressDialog, noticeArrayList, noticeAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_mng);
        // Construct DataManage object
        dm = new DataManage();

        // Get command recyclerview refresh
        String refe = (String) getIntent().getSerializableExtra("refresh");

        // Attach notice list with loading progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ArrayList, Adapter, and recycler view
        noticeArrayList = new ArrayList<NoticeInfo>();
        noticeAdapter = new NoticeAdapter(NoticeMngActivity.this, noticeArrayList, this);
        recyclerView.setAdapter(noticeAdapter);
        dm.showNoticeList(progressDialog, noticeArrayList, noticeAdapter);

        // Set write notice button listener
        Button writeBtn = findViewById(R.id.writeButton);
        writeBtn.setOnClickListener(new View.OnClickListener() {//click write button
           @Override
            public void onClick(View view) {
                // Start NoticeWriteActivity in "WRITE_NEW" mode
                Intent intent = new Intent(getApplicationContext(), NoticeWriteActivity.class);
                startActivityForResult(intent, NoticeWriteType.WRITE_NEW.ordinal());
                // Refresh notice list
                noticeAdapter.notifyDataSetChanged();
            }
        });

        // If there's some refresh command, refresh the notice list
        if(refe!=null){
            Log.d("NoticeMngActivity", refe);
            noticeAdapter.notifyDataSetChanged();//refresh notice list
        }

    }

    @Override
    public void onItemClick(int position){}
}
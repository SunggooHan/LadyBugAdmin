package com.sweteam5.ladybugadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoticeWriteActivity extends AppCompatActivity {

    private EditText titleEditText;     // Title EditText of notice under writing
    private EditText contentEditText;   // Content EditText of notice under writing
    private String documentID = null;   // Document ID of notice in firebase database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_write);

        // Set views that is drawn in activity
        titleEditText = findViewById(R.id.noticeWriteTitleEditText);
        contentEditText = findViewById(R.id.noticeWriteContentEditText);

        // If the user modifies the notice, fill in the EditText content with the existing content.
        writeExistingContents();

        // Set upload button listener
        AppCompatActivity activity = this;
        Button uploadBtn = findViewById(R.id.uploadBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get notice related data from UI and system
                String title = titleEditText.getText().toString();
                long now = System.currentTimeMillis();
                SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String date = (String)mFormat.format(new Date(now));
                String content = contentEditText.getText().toString();

                // If there is no documentID, add a new notice written.
                if(documentID == null) {
                    NoticeMngActivity.dm.uploadNotice(title, date, content);
                }
                // If there is documentID, modify the existing notice.
                else {
                    NoticeMngActivity.dm.uploadModification(activity, title, date, content, documentID);
                }
                finish();
            }
        });
    }

    // Modify the existing notice
    private void writeExistingContents()
    {
        // Get EditTexts from activity
        EditText noticeWriteTitleEditText = findViewById(R.id.noticeWriteTitleEditText);
        EditText noticeWriteContentEditText = findViewById(R.id.noticeWriteContentEditText);

        // Check for any intents received from previous activity
        Intent existingIntent = getIntent();
        Bundle existingBundle = existingIntent.getBundleExtra("contentBundle");
        if (existingBundle != null) {
            // Receive data from the intent and fill in the contents.
            String title = existingBundle.getString("title");
            String content = existingBundle.getString("content");
            documentID = existingBundle.getString("documentid");
            noticeWriteTitleEditText.setText(title);
            noticeWriteContentEditText.setText(content);
        }
    }
}
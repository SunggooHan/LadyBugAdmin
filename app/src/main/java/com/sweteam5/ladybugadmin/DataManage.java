package com.sweteam5.ladybugadmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class DataManage {
    // Firebase instance to communicate with Firestore Database
    FirebaseFirestore firestoreDatabase= FirebaseFirestore.getInstance();
    private int noticeAmount;   // Size of notice list

    // Delete notice if the target notice is not last one.
    public void deleteNotice(Context context, String title){
        // Check if size of notice list is larger than 1
        if(noticeAmount > 1) {
            // Get document ID of target notice with title
            firestoreDatabase.collection("notice").whereEqualTo("title", title).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {//id를 가져옴
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String documentID = documentSnapshot.getId();

                        // Deletes the notice data corresponding to the documentID from the database.
                        firestoreDatabase.collection("notice").document(documentID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // If the deletion is successful, update the notice list.
                                ((NoticeMngActivity)context).updateNoticeList();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            });
        }
        else {
            Toast.makeText(context, "마지막 공지는 삭제할 수 없어요.", Toast.LENGTH_LONG);
        }
    }

    // Read the data of the notice to be modified, hand it over to the notice writing activity, and start it.
    public void findModifyNotice(AppCompatActivity activity, Context context, String title){
        // Get document ID and notice object of target notice with title
        firestoreDatabase.collection("notice").whereEqualTo("title", title).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()&&!task.getResult().isEmpty()) {
                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                    String documentID = documentSnapshot.getId();
                    NoticeInfo notice = documentSnapshot.toObject(NoticeInfo.class);

                    // Start the activity by making a bundle to hand over to the notification writing activity.
                    Bundle contentBundle = new Bundle();
                    contentBundle.putString("title", notice.getTitle());
                    contentBundle.putString("content", notice.getContent());
                    contentBundle.putString("documentid", (String)documentID);
                    Intent intent = new Intent(context, NoticeWriteActivity.class);
                    intent.putExtra("contentBundle", contentBundle);
                    activity.startActivityForResult(intent, 1001);
                }
            }
        });
    }

    // The modifications of the notice are reflected in the database.
    public void uploadModification(AppCompatActivity activity, String title, String date, String content, String DocumentID){
        // The modified data is turned into an object and handed over to the database server.
        NoticeInfo noticeInfo = new NoticeInfo(title,date, content);
        firestoreDatabase.collection("notice").document(DocumentID).set(noticeInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // If the upload is successful, the notice writing activity will be terminated.
                activity.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Load notice list from database and show it
    public void showNoticeList(ProgressDialog progressDialog, ArrayList<NoticeInfo> noticeArrayList, NoticeAdapter noticeAdapter){
        // Load the notice list in reverse order of the written date.
        firestoreDatabase.collection("notice").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                // If there is an error, print the error to the logcat and terminate the function.
                if(error != null){
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                    Log.e("Firestore error", error.getMessage());
                    return;
                }

                // Add the read data to the notice list one by one and display it on the screen.
                for(DocumentChange dc  :value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        NoticeInfo notice = dc.getDocument().toObject(NoticeInfo.class);
                        noticeArrayList.add(notice);
                    }
                    noticeAdapter.notifyDataSetChanged();
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                }
                // Set notice list size
                noticeAmount = noticeArrayList.size();
            }
        });
    }

    // Add new notice to the database
    public void uploadNotice(String title, String date, String content){
        // The new notice data is turned into an object and handed over to the database server.
        NoticeInfo noticeInfo = new NoticeInfo(title,date, content);
        firestoreDatabase.collection("notice").add(noticeInfo)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) { }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) { e.printStackTrace(); }
            });
    }
}

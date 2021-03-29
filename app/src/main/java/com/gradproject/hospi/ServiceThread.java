package com.gradproject.hospi;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class ServiceThread extends Thread{
    private static final String TAG = "ServiceThread";

    Handler handler;
    boolean isRun = true;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public ServiceThread(Handler handler){
        this.handler = handler;
    }

    public void stopForever(){
        synchronized (this) {
            this.isRun = false;
        }
    }

    public void run(){
        realTimeCheckInquiry();
    }

    public void realTimeCheckInquiry(){
        db.collection(Inquiry.DB_NAME)
                .whereEqualTo("id", user.getEmail())
                .addSnapshotListener((value, error) -> {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                            case REMOVED:
                                break;
                            case MODIFIED:
                                Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                String docId = dc.getDocument().getId();
                                checkAnswer(docId);
                                break;
                        }
                    }

                });
    }

    public void checkAnswer(String docId){
        DocumentReference docRef = db.collection(Inquiry.DB_NAME).document(docId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    boolean isAnswer = (boolean) document.get("checkedAnswer");
                    if(isAnswer){
                        handler.sendEmptyMessage(0);//쓰레드에 있는 핸들러에게 메세지를 보냄
                    }
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }
}
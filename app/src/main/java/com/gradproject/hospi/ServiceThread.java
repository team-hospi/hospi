package com.gradproject.hospi;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class ServiceThread extends Thread{
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
        //반복적으로 수행할 작업을 한다.
        while(isRun){
            try{
                db.collection(Inquiry.DB_NAME)
                        .whereEqualTo("id", user.getEmail())
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                for (DocumentChange dc : value.getDocumentChanges()) {
                                    switch (dc.getType()) {
                                        case ADDED:
                                        case REMOVED:
                                            break;
                                        case MODIFIED:
                                            Log.d("pushthread", "Modified city: " + dc.getDocument().getData());
                                            handler.sendEmptyMessage(0);//쓰레드에 있는 핸들러에게 메세지를 보냄
                                            break;
                                    }
                                }
                            }
                        });
                Thread.sleep(10000); //10초씩 쉰다.
            }catch (Exception e) {}
        }
    }
}
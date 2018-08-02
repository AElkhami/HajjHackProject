package com.elkhamitech.hajjhackproject.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.elkhamitech.hajjhackproject.R;
import com.elkhamitech.hajjhackproject.model.Operator;
import com.google.android.gms.tasks.OnCompleteListener;
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

import javax.annotation.Nullable;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "MyLog --> ";
    private EditText natIdEditText;
    private String userNatId;
    private FirebaseFirestore db;
    private Operator operator;


    @Override
    protected void onStart() {
        super.onStart();
         db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        natIdEditText = findViewById(R.id.nat_id_login);

        operator = new Operator();

    }

    private String checkPermission (){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        } else {

            TelephonyManager tMgr = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number();

            return mPhoneNumber;
        }

        return null;
    }



    public void login(View view) {

        userNatId = natIdEditText.getText().toString();

        DocumentReference docRef =  db.collection("operator").document("QhHw8ES8IXEmKSTcgZd7");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                operator = documentSnapshot.toObject(Operator.class);
                if(userNatId.equals(operator.getNational_id())){

                    if(operator.getPhone_number().equals(checkPermission())){

                        Toast.makeText(LoginActivity.this, "Welcome "+ operator.getName(), Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                    }else {
                        Toast.makeText(LoginActivity.this, "Your phone number is not authorised. "+checkPermission(), Toast.LENGTH_SHORT).show();
                    }
                }else {

                    Toast.makeText(LoginActivity.this, "Your national ID is incorrect.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}

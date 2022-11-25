package com.example.groceriesapp.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.groceriesapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddPromoActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private EditText promoCodeEt,promoDescriptionEt,promoPriceEt,minimumOrderPriceEt;
    private TextView expireDateTv,titleTv;
    private Button addBtn;

    //firebase Auth
    FirebaseAuth firebaseAuth;
    //ProgressDialog
    ProgressDialog progressDialog;

    private String promoId;
    private boolean isUpdating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promo);
        backBtn = findViewById(R.id.backBtn);
        promoCodeEt = findViewById(R.id.promoCodeEt);
        promoDescriptionEt = findViewById(R.id.promoDescriptionEt);
        promoPriceEt = findViewById(R.id.promoPriceEt);
        minimumOrderPriceEt = findViewById(R.id.minimumOrderPriceEt);
        expireDateTv = findViewById(R.id.expireDateTv);
        addBtn = findViewById(R.id.addBtn);
        titleTv = findViewById(R.id.titleTv);
        firebaseAuth = FirebaseAuth.getInstance();
        //progress
        progressDialog =new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        //get promo id from intent
        Intent intent = getIntent();
        if (intent.getStringExtra("promoId") != null){
            //came here adapter from update here
            promoId = intent.getStringExtra("promoId");
            titleTv.setText("Update Promotion code");
            addBtn.setText("Update");

            isUpdating = true;

            loadPromoInfo();
        }
        else{
            //came here from promoCodes
            titleTv.setText("Add Promotion code");
            addBtn.setText("Add");

            isUpdating = false;
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //handle clicks, pick data
        expireDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickDialog();
            }
        });
        //handle click, add promotion code to firebase db
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });

    }

    private void loadPromoInfo() {
        //db path
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Promotions").child(promoId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        //get info
                        String id = ""+snapshot.child("id").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String description = ""+snapshot.child("description").getValue();
                        String promoCode = ""+snapshot.child("promoCode").getValue();
                        String promoPrice = ""+snapshot.child("promoPrice").getValue();
                        String minimumOrderPrice = ""+snapshot.child("minimumOrderPrice").getValue();
                        String expireDate = ""+snapshot.child("expireDate").getValue();

                        //set data
                        promoCodeEt.setText(promoCode);
                        promoDescriptionEt.setText(description);
                        promoPriceEt.setText(promoPrice);
                        minimumOrderPriceEt.setText(minimumOrderPrice);
                        expireDateTv.setText(expireDate);
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
    }

    private void datePickDialog() {
        //Get current  date to set on calender
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        //date pick Dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                DecimalFormat mFormat = new DecimalFormat("00");
                String pDay = mFormat.format(dayOfMonth);
                String pMonth = mFormat.format(monthOfYear);
                String pYear = ""+year;
                String pDate = pDay +"/"+ pMonth+ "/" +pYear;

                expireDateTv.setText(pDate);

            }
        },mYear,mMonth,mDay);

        //show dialog
        datePickerDialog.show();
        //disable past date selection on calender
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
    }

    private String description, promoCode, promoPrice, minimumOrderPrice, expireDate;
    private void inputData(){
        //input Data
        promoCode = promoCodeEt.getText().toString().trim();
        description = promoDescriptionEt.getText().toString().trim();
        promoPrice = promoPriceEt.getText().toString().trim();
        minimumOrderPrice = minimumOrderPriceEt.getText().toString();
        expireDate = expireDateTv.getText().toString().trim();

        //validate form date
        if (TextUtils.isEmpty(promoCode)){
            Toast.makeText(this, "Enter Discount Code...", Toast.LENGTH_SHORT).show();
            return;
        }if (TextUtils.isEmpty(description)){
            Toast.makeText(this, "Enter Description...", Toast.LENGTH_SHORT).show();
            return;
        }if (TextUtils.isEmpty(minimumOrderPrice)){
            Toast.makeText(this, "Enter Minimum Order Price...", Toast.LENGTH_SHORT).show();
            return;
        }if (TextUtils.isEmpty(expireDate)){
            Toast.makeText(this, "Enter ExpireDate...", Toast.LENGTH_SHORT).show();
            return;
        }if (TextUtils.isEmpty(promoPrice)){
            Toast.makeText(this, "Enter Promo Price...", Toast.LENGTH_SHORT).show();
            return;
        }
        //all fields entered
        if (isUpdating){
            //updating
            updateDataToDb();
        }else{
            //addData
            addDataToDb();
        }

    }

    private void updateDataToDb() {
        //all date store db
        progressDialog.setMessage("Updating Promotion Code...");
        progressDialog.show();

        //setup date to add
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("description", ""+description);
        hashMap.put("promoCode", ""+ promoCode);
        hashMap.put("promoPrice", ""+ promoPrice);
        hashMap.put("minimumOrderPrice", minimumOrderPrice);
        hashMap.put("expireDate", ""+expireDate);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Promotions").child(promoId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //updated
                        progressDialog.dismiss();
                        Toast.makeText(AddPromoActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                        //failed update
                        progressDialog.dismiss();
                        Toast.makeText(AddPromoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void addDataToDb() {
        //all date store db
        progressDialog.setMessage("Adding Promotion Code...");
        progressDialog.show();

        String timestamp = ""+System.currentTimeMillis();
        //setup date to add
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", ""+ timestamp);
        hashMap.put("timestamp", ""+ timestamp);
        hashMap.put("description", ""+description);
        hashMap.put("promoCode", ""+ promoCode);
        hashMap.put("promoPrice", ""+ promoPrice);
        hashMap.put("minimumOrderPrice", minimumOrderPrice);
        hashMap.put("expireDate", ""+expireDate);

        //init db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Promotions").child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //code Added
                        progressDialog.dismiss();
                        Toast.makeText(AddPromoActivity.this, "Promotion code added...", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       //code failed
                        progressDialog.dismiss();
                        Toast.makeText(AddPromoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
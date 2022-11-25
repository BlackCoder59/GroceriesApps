package com.example.groceriesapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceriesapp.R;
import com.example.groceriesapp.adapter.AdapterPromotion;
import com.example.groceriesapp.models.ModelPromotion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PromotionCodesActivity extends AppCompatActivity {

    private ImageButton addPromoBtn,backBtn,filterBtn;
    private TextView filteredTv;
    private RecyclerView promoRv;

    private ArrayList<ModelPromotion> promotionArrayList;
    private AdapterPromotion adapterPromotion;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_codes);
        addPromoBtn = findViewById(R.id.addPromoBtn);
        backBtn = findViewById(R.id.backBtn);
        filteredTv = findViewById(R.id.filteredTv);
        filterBtn = findViewById(R.id.filterBtn);
        promoRv = findViewById(R.id.promoRv);
        firebaseAuth = FirebaseAuth.getInstance();
        loadAllPromoCode();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        addPromoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PromotionCodesActivity.this, AddPromoActivity.class));
            }
        });

        //handle filter button click
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog();
            }
        });



    }

    private void filterDialog() {
        //option
        String[] options = {"All", "Expired", "Not Expired"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Promotion Code")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handel items click
                        if (which == 0){
                            //All clicked
                            filteredTv.setText("All Promotion Codes");
                            loadAllPromoCode();
                        }
                        else if (which == 1){
                            //Expire Date
                            filteredTv.setText("Expired Promotion Codes");
                            loadExpirePromoCode();
                        }
                        else if (which == 2){
                            //Not Expire clicked
                            filteredTv.setText("Not Expired Promotion Codes");
                            loadNotExpirePromoCode();
                        }
                    }
                }).show();
    }

    private void loadAllPromoCode(){
        //init list
        promotionArrayList= new ArrayList<>();

        // db refrnc user
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Promotions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        //clear list before adding data
                        promotionArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelPromotion modelPromotion = ds.getValue(ModelPromotion.class);
                            //add to list
                            promotionArrayList.add(modelPromotion);

                        }
                        //setup adapter
                        adapterPromotion = new AdapterPromotion(PromotionCodesActivity.this, promotionArrayList);
                        //set Adapter
                        promoRv.setAdapter(adapterPromotion);
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
    }

    private void loadExpirePromoCode(){

        //get current date
        DecimalFormat format = new DecimalFormat("00");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String todayDate = day +"/"+ month +"/"+ year;

        //init list
        promotionArrayList= new ArrayList<>();

        // db refrnc user
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Promotions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        //clear list before adding data
                        promotionArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelPromotion modelPromotion = ds.getValue(ModelPromotion.class);

                            String expDate = modelPromotion.getExpireDate();
                            try {
                                SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
                                Date currentDate = sdformat.parse(todayDate);
                                Date expireDate = sdformat.parse(expDate);
                                if (expireDate.compareTo(currentDate)>0){
                                    //date 1 occurs after date 2
                                }
                                else if (expireDate.compareTo(currentDate) < 0){
                                    //date 1 occurs before date 2
                                    //add to list
                                    promotionArrayList.add(modelPromotion);

                                }else if (expireDate.compareTo(currentDate) == 0){
                                    //bath date equal

                                }

                            } catch (Exception e) {

                            }

                        }
                        //setup adapter
                        adapterPromotion = new AdapterPromotion(PromotionCodesActivity.this, promotionArrayList);
                        //set Adapter
                        promoRv.setAdapter(adapterPromotion);
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });


    }

    private void loadNotExpirePromoCode(){
        //get current date
        DecimalFormat format = new DecimalFormat("00");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String todayDate = day +"/"+ month +"/"+ year;

        //init list
        promotionArrayList= new ArrayList<>();

        // db refrnc user
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Promotions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        //clear list before adding data
                        promotionArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelPromotion modelPromotion = ds.getValue(ModelPromotion.class);

                            String expDate = modelPromotion.getExpireDate();
                            try {
                                SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
                                Date currentDate = sdformat.parse(todayDate);
                                Date expireDate = sdformat.parse(expDate);
                                if (expireDate.compareTo(currentDate)>0){
                                    //date 1 occurs after date 2
                                    //add to list
                                    promotionArrayList.add(modelPromotion);
                                }
                                else if (expireDate.compareTo(currentDate) < 0){
                                    //date 1 occurs before date 2


                                }else if (expireDate.compareTo(currentDate) == 0){
                                    //bath date equal
                                    //add to list
                                    promotionArrayList.add(modelPromotion);

                                }

                            } catch (Exception e) {

                            }

                        }
                        //setup adapter
                        adapterPromotion = new AdapterPromotion(PromotionCodesActivity.this, promotionArrayList);
                        //set Adapter
                        promoRv.setAdapter(adapterPromotion);
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });

    }


}
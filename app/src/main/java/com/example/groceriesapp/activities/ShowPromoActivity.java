package com.example.groceriesapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceriesapp.R;
import com.example.groceriesapp.adapter.AdapterShowPromo;
import com.example.groceriesapp.models.ModelPromotion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowPromoActivity extends AppCompatActivity {

    private ImageButton backBtn,filterBtn;
    private TextView filteredTv;
    private RecyclerView promoRv;

    private ArrayList<ModelPromotion> promotionArrayList;
    private AdapterShowPromo adapterShowPromo;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_promo);
        backBtn = findViewById(R.id.backBtn);
        filteredTv = findViewById(R.id.filteredTv);
        filterBtn = findViewById(R.id.filteredTv);
        promoRv = findViewById(R.id.promoRv);
        firebaseAuth = FirebaseAuth.getInstance();
        loadAllPromoCode();



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



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
                        adapterShowPromo = new AdapterShowPromo(ShowPromoActivity.this, promotionArrayList);
                        //set Adapter
                        promoRv.setAdapter(adapterShowPromo);
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
    }

}
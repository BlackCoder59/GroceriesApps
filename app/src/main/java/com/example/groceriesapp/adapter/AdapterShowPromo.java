package com.example.groceriesapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceriesapp.R;
import com.example.groceriesapp.models.ModelPromotion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterShowPromo extends RecyclerView.Adapter<AdapterShowPromo.HolderPromotion> {

    private Context context;
    private ArrayList<ModelPromotion> promotionArrayList;

    private FirebaseAuth firebaseAuth;

    public AdapterShowPromo(Context context, ArrayList<ModelPromotion> promotionArrayList) {
        this.context = context;
        this.promotionArrayList = promotionArrayList;

        firebaseAuth = FirebaseAuth.getInstance();
    }



    @NonNull
    @Override
    public HolderPromotion onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_promo, parent, false);

        return new HolderPromotion(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  HolderPromotion holder, int position) {
        //get data
        ModelPromotion modelPromotion = promotionArrayList.get(position);
        String id = modelPromotion.getId();
        String timestamp = modelPromotion.getTimestamp();
        String description = modelPromotion.getDescription();
        String promoCode = modelPromotion.getPromoCode();
        String promoPrice = modelPromotion.getPromoPrice();
        String expireDate = modelPromotion.getExpireDate();
        String minimumOrderPrice = modelPromotion.getMinimumOrderPrice();

        loadPromoData(modelPromotion,holder);

        //set data
        holder.descriptionTv.setText(description);
        holder.promoCodeTv.setText("Code:"+ promoCode);
        holder.promoPriceTv.setText(promoPrice);
        holder.expireDateTv.setText("Expire Date:"+ expireDate);
        holder.minimumOrderPriceTv.setText(minimumOrderPrice);


    }

    private void loadPromoData(ModelPromotion modelPromotion, HolderPromotion holder) {
        promotionArrayList= new ArrayList<>();
        String uid = modelPromotion.getId();
        //child(firebaseAuth.getUid()).child("Promotions")

        // db refrnc user
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        String id = ""+snapshot.child("timestamp").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String description = ""+snapshot.child("description:").getValue();
                        String promoCode = ""+snapshot.child("promoCode:").getValue();
                        String promoPrice =""+snapshot.child("promoPrice:").getValue();
                        String expireDate = ""+snapshot.child("expireDate").getValue();
                        String minimumOrderPrice = ""+snapshot.child("minimumOrderPrice").getValue();

                        //set data
                        holder.promoCodeTv.setText(promoCode);
                        holder.descriptionTv.setText(description);
                        holder.expireDateTv.setText(expireDate);
                        holder.promoPriceTv.setText(promoPrice);
                        holder.minimumOrderPriceTv.setText(minimumOrderPrice);


                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
    }


    @Override
    public int getItemCount() {
        return promotionArrayList.size();
    }

    class HolderPromotion extends RecyclerView.ViewHolder{

        //views of row
        private ImageView iconIv;
        private TextView promoCodeTv,promoPriceTv,minimumOrderPriceTv,expireDateTv,descriptionTv;

        public HolderPromotion(@NonNull View itemView) {
            super(itemView);
            iconIv = itemView.findViewById(R.id.iconIv);
            promoCodeTv = itemView.findViewById(R.id.promoCodeTv);
            promoPriceTv = itemView.findViewById(R.id.promoPriceTv);
            minimumOrderPriceTv = itemView.findViewById(R.id.minimumOrderPriceTv);
            expireDateTv = itemView.findViewById(R.id.expireDateTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);


        }
    }
}

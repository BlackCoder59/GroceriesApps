package com.example.groceriesapp.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceriesapp.R;
import com.example.groceriesapp.activities.AddPromoActivity;
import com.example.groceriesapp.models.ModelPromotion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterPromotion extends RecyclerView.Adapter<AdapterPromotion.HolderPromotion> {

    private Context context;
    private ArrayList<ModelPromotion> promotionArrayList;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    public AdapterPromotion(Context context, ArrayList<ModelPromotion> promotionArrayList) {
        this.context = context;
        this.promotionArrayList = promotionArrayList;

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderPromotion onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_promotion_shop, parent, false);

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

        //set data
        holder.descriptionTv.setText(description);
        holder.promoCodeTv.setText("Code:"+ promoCode);
        holder.promoPriceTv.setText(promoPrice);
        holder.expireDateTv.setText("Expire Date:"+ expireDate);
        holder.minimumOrderPriceTv.setText(minimumOrderPrice);


        /*handle click show Edit delete Dialog*/
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               editDeleteDialog(modelPromotion,holder);

           }
       });

    }

    private void editDeleteDialog(ModelPromotion modelPromotion, HolderPromotion holder) {
        //option to display in dialog
        String[] options = {"Edit", "Delete"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Option")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       if (which==0){
                           //Edit
                           editPromoCode(modelPromotion);
                       }
                       else if (which==1){
                           //Delete
                           deletePromoCode(modelPromotion);
                       }
                    }
                })
                .show();
    }

    private void deletePromoCode(ModelPromotion modelPromotion) {
        //show progress bar
        progressDialog.setMessage("Deleting Promotion Code");
        progressDialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Promotions")
                .child(modelPromotion.getId())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //delete
                        progressDialog.dismiss();
                        Toast.makeText(context, "Deleted...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                        //failed
                        progressDialog.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void editPromoCode(ModelPromotion modelPromotion) {
        //start and pass data to adapter
        Intent intent = new Intent(context, AddPromoActivity.class);
        intent.putExtra("promoId", modelPromotion.getId());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return promotionArrayList.size() ;
    }


    class HolderPromotion extends RecyclerView.ViewHolder{

        //views of row
        private ImageView iconIv,nextIv;
        private TextView promoCodeTv,promoPriceTv,minimumOrderPriceTv,expireDateTv,descriptionTv;

        public HolderPromotion(@NonNull  View itemView) {
            super(itemView);
            iconIv = itemView.findViewById(R.id.iconIv);
            promoCodeTv = itemView.findViewById(R.id.promoCodeTv);
            promoPriceTv = itemView.findViewById(R.id.promoPriceTv);
            minimumOrderPriceTv = itemView.findViewById(R.id.minimumOrderPriceTv);
            expireDateTv = itemView.findViewById(R.id.expireDateTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            nextIv = itemView.findViewById(R.id.nextIv);

        }
    }
}

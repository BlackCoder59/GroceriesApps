package com.example.groceriesapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceriesapp.FilterProduct;
import com.example.groceriesapp.R;
import com.example.groceriesapp.activities.EditProductActivity;
import com.example.groceriesapp.models.ModelProducts;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductSeller extends RecyclerView.Adapter<AdapterProductSeller.HolderProductSeller> implements Filterable {

    private Context context;
    public ArrayList<ModelProducts> productsList, filterList;
    private FilterProduct filter;

    public AdapterProductSeller(Context context, ArrayList<ModelProducts> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;

    }

    @NonNull
    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate Layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_seller, parent, false);
        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  HolderProductSeller holder, int position) {
        //get data
        ModelProducts modelProducts = productsList.get(position);
        String id = modelProducts.getProductId();
        String uid = modelProducts.getUid();
        String discountAvailable = modelProducts.getDiscountAvailable();
        String productCategory = modelProducts.getProductCategory();
        String discountNote = modelProducts.getDiscountNote();
        String title = modelProducts.getProductTitle();
        String quantity = modelProducts.getProductQuantity();
        String description = modelProducts.getProductDescription();
        String discountPrice = modelProducts.getDiscountPrice();
        String originalPrice = modelProducts.getOriginalPrice();
        String icon = modelProducts.getProductIcon();
        String timestamp = modelProducts.getTimestamp();

        //set data
        holder.titleTv.setText(title);
        holder.quantityTv.setText(quantity);
        holder.discountNoteTv.setText(discountNote);
        holder.discountPriceTv.setText("$"+discountPrice);
        holder.originalPriceTv.setText("$"+originalPrice);

        if (discountAvailable.equals("true")){
            //product is on discount
            holder.discountPriceTv.setVisibility(View.VISIBLE);
            holder.discountNoteTv.setVisibility(View.VISIBLE);
            holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
        else{
            //product is not on discount
            holder.discountPriceTv.setVisibility(View.GONE);
            holder.discountNoteTv.setVisibility(View.GONE);
            holder.originalPriceTv.setPaintFlags(0);

        }
        try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_shopping_cblue).into(holder.productIconIv);

        } catch (Exception e) {
         holder.productIconIv.setImageResource(R.drawable.ic_shopping_cblue);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handle item clicks, show item details
                detailsBottomSheet(modelProducts);

            }
        });

    }

    private void detailsBottomSheet(ModelProducts modelProducts) {
        //bottom sheet
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        //inflate view for bottomSheet
        View view = LayoutInflater.from(context).inflate(R.layout.bs_product_details_seller, null);
        //set view to bottomSheet
        bottomSheetDialog.setContentView(view);



        //init views of bottomSheet
        ImageButton backBtn = view.findViewById(R.id.backBtn);
        ImageButton deleteBtn = view.findViewById(R.id.deleteBtn);
        ImageButton editBtn = view.findViewById(R.id.editBtn);
        ImageView profileIconIv = view.findViewById(R.id.profileIconIv);
        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView descriptionTv = view.findViewById(R.id.descriptionTv);
        TextView discountNoteTv = view.findViewById(R.id.discountNoteTv);
        TextView categoryTv = view.findViewById(R.id.categoryTv);
        TextView quantityTv = view.findViewById(R.id.quantityTv);
        TextView discountPriceTv = view.findViewById(R.id.discountPriceTv);
        TextView originalPriceTv = view.findViewById(R.id.originalPriceTv);

        //get data
        String id = modelProducts.getProductId();
        String uid = modelProducts.getUid();
        String discountAvailable = modelProducts.getDiscountAvailable();
        String productCategory = modelProducts.getProductCategory();
        String discountNote = modelProducts.getDiscountNote();
        String title = modelProducts.getProductTitle();
        String quantity = modelProducts.getProductQuantity();
        String description = modelProducts.getProductDescription();
        String discountPrice = modelProducts.getDiscountPrice();
        String originalPrice = modelProducts.getOriginalPrice();
        String icon = modelProducts.getProductIcon();
        String timestamp = modelProducts.getTimestamp();

        //set data
        titleTv.setText(title);
        descriptionTv.setText(description);
        categoryTv.setText(productCategory);
        quantityTv.setText(quantity);
        discountNoteTv.setText(discountNote);
        discountPriceTv.setText("$"+ discountPrice);
        originalPriceTv.setText("$"+ originalPrice);
        if (discountAvailable.equals("true")){
            //product is on discount
            discountPriceTv.setVisibility(View.VISIBLE);
            discountNoteTv.setVisibility(View.VISIBLE);
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
        else{
            //product is not on discount
            discountPriceTv.setVisibility(View.GONE);
            discountNoteTv.setVisibility(View.GONE);

        }
        try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_shopping_cblue).into(profileIconIv);

        } catch (Exception e) {
            profileIconIv.setImageResource(R.drawable.ic_shopping_cblue);
        }

        //show dialog
        bottomSheetDialog.show();

        //edit click
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //open edit profile activity
                Intent intent = new Intent(context, EditProductActivity.class);
                intent.putExtra("productId", id);
                context.startActivity(intent);

            }
        });
        //delete click
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //show delete confirm dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete product "+title+"?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete
                                deleteProduct(id);

                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //cancel
                                dialog.dismiss();

                            }
                        })
                        .show();

            }
        });
        //back btn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss bottom sheet
                bottomSheetDialog.dismiss();

            }
        });


    }

    private void deleteProduct(String id) {
        //delete product using its id

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Product deleted...", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                        //failed delete product
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterProduct(this, filterList);
        }
        return filter;
    }

    class HolderProductSeller extends RecyclerView.ViewHolder{
        /*holds views of recycler view*/

        private ImageView productIconIv,nextIv;
        private TextView discountNoteTv,titleTv,quantityTv,discountPriceTv,originalPriceTv;

        public HolderProductSeller(@NonNull View itemView) {
            super(itemView);
            productIconIv = itemView.findViewById(R.id.productIconIv);
            nextIv = itemView.findViewById(R.id.nextIv);
            discountNoteTv = itemView.findViewById(R.id.discountNoteTv);
            titleTv = itemView.findViewById(R.id.titleTv);
            quantityTv = itemView.findViewById(R.id.quantityTv);
            discountPriceTv = itemView.findViewById(R.id.discountPriceTv);
            originalPriceTv = itemView.findViewById(R.id.originalPriceTv);
        }
    }
}

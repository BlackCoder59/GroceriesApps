package com.example.groceriesapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.groceriesapp.FilterProductUser;
import com.example.groceriesapp.R;
import com.example.groceriesapp.activities.ShopDetailsActivity;
import com.example.groceriesapp.models.ModelProducts;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {

    public ArrayList<ModelProducts> productsList, filterList;
    private Context context;
    private FilterProductUser filter;



    public AdapterProductUser(Context context, ArrayList<ModelProducts> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_user, parent, false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  HolderProductUser holder, int position) {
        //get data
       final ModelProducts modelProducts = productsList.get(position);
        String discountAvailable = modelProducts.getDiscountAvailable();
        String discountNote = modelProducts.getDiscountNote();
        String discountPrice = modelProducts.getDiscountPrice();
        String productCategory = modelProducts.getProductCategory();
        String originalPrice = modelProducts.getOriginalPrice();
        String productDescription = modelProducts.getProductDescription();
        String productTitle = modelProducts.getProductTitle();
        String productQuantity = modelProducts.getProductQuantity();
        String productId = modelProducts.getProductId();
        String timestamp = modelProducts.getTimestamp();
        String productIcon = modelProducts.getProductIcon();

        //set data
        holder.titleTv.setText(productTitle);
        holder.discountNoteTv.setText(discountNote);
        holder.descriptionTv.setText(productDescription);
        holder.originalPriceTv.setText("$"+originalPrice);
        holder.discountPriceTv.setText("$"+discountPrice);
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
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_shopping_cblue).into(holder.productIconIv);

        } catch (Exception e) {
            holder.productIconIv.setImageResource(R.drawable.ic_shopping_cblue);
        }
        holder.addToCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add to product cart
                showQuantityDialog(modelProducts);

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show product details
            }
        });

    }

   private double cost = 0;
    private double finalCost = 0;
    private int quantity = 0;
    private void showQuantityDialog(ModelProducts modelProducts) {
        //inflater
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_quantity, null);
        //init layout
        CircularImageView productIv = view.findViewById(R.id.productIv);
        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView pQuantityTv = view.findViewById(R.id.pQuantityTv);
        TextView descriptionTv = view.findViewById(R.id.descriptionTv);
        TextView discountNoteTv = view.findViewById(R.id.discountNoteTv);
        TextView originalPriceTv = view.findViewById(R.id.originalPriceTv);
        TextView discountPriceTv = view.findViewById(R.id.discountPriceTv);
        TextView finalTv = view.findViewById(R.id.finalTv);
        TextView quantityTv = view.findViewById(R.id.quantityTv);
        ImageButton decrementBtn = view.findViewById(R.id.decrementBtn);
        ImageButton incrementBtn = view.findViewById(R.id.incrementBtn);
        Button continueBtn = view.findViewById(R.id.continueBtn);

        //get data from model
        String productId = modelProducts.getProductId();
        String title = modelProducts.getProductTitle();
        String productQuantity = modelProducts.getProductQuantity();
        String description = modelProducts.getProductDescription();
        String discountNote = modelProducts.getDiscountNote();
        String image = modelProducts.getProductIcon();

        String price;
        if (modelProducts.getDiscountAvailable().equals("true")){
            price = modelProducts.getDiscountPrice();
            discountNoteTv.setVisibility(View.VISIBLE);
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
        else{
            discountPriceTv.setVisibility(View.GONE);
            discountNoteTv.setVisibility(View.GONE);
            price = modelProducts.getOriginalPrice();
        }
        cost = Double.parseDouble(price.replaceAll("$", ""));
        finalCost = Double.parseDouble(price.replaceAll("$", ""));
        quantity = 1;

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_baseline_shopping_cart_gray).into(productIv);

        } catch (Exception e) {
            productIv.setImageResource(R.drawable.ic_baseline_shopping_cart_gray);
        }
        titleTv.setText(""+title);
        pQuantityTv.setText(""+productQuantity);
        descriptionTv.setText(""+description);
        discountNoteTv.setText(""+discountNote);
        quantityTv.setText(""+quantity);
        originalPriceTv.setText("$"+modelProducts.getOriginalPrice());
        discountPriceTv.setText("$"+modelProducts.getDiscountPrice());
        finalTv.setText("$"+finalCost);

        AlertDialog dialog = builder.create();
        dialog.show();

        //increaseQuantity
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost = finalCost+cost;
                quantity++;

                finalTv.setText("$"+finalCost);
                quantityTv.setText(""+quantity);
            }
        });
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity>1){
                    finalCost = finalCost - cost;
                    quantity--;
                    finalTv.setText("$"+finalCost);
                    quantityTv.setText(""+quantity);
                }
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleTv.getText().toString().trim();
                String priceEch = price;
                String totalPrice = finalTv.getText().toString().trim().replace("$", "");
                String quantity = quantityTv.getText().toString().trim();

                addToCart(productId, title, priceEch, totalPrice, quantity);

                dialog.dismiss();
            }
        });


    }
private int itemId = 1;
    private void addToCart(String productId, String title, String priceEch, String price, String quantity) {
        itemId++;

        EasyDB easyDB = EasyDB.init(context, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        Boolean b = easyDB.addData("Item_Id", itemId)
                .addData("Item_PID", productId)
                .addData("Item_Name", title)
                .addData("Item_Price_Each", priceEch)
                .addData("Item_Price", price)
                .addData("Item_Quantity", quantity)
                .doneDataAdding();

        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();


        //update cart count
        ((ShopDetailsActivity)context).cartCount();

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterProductUser(this, filterList);
        }
        return filter;
    }

    class HolderProductUser extends RecyclerView.ViewHolder{

        //uid views
        private ImageView productIconIv;
        private TextView discountNoteTv,titleTv,descriptionTv,addToCartTv,
                discountPriceTv,originalPriceTv,nextIv;

        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            //init ui views
            productIconIv = itemView.findViewById(R.id.productIconIv);
            discountNoteTv = itemView.findViewById(R.id.discountNoteTv);
            titleTv = itemView.findViewById(R.id.titleTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            addToCartTv = itemView.findViewById(R.id.addToCartTv);
            discountPriceTv = itemView.findViewById(R.id.discountPriceTv);
            originalPriceTv = itemView.findViewById(R.id.originalPriceTv);


        }
    }
}

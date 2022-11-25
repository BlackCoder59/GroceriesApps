package com.example.groceriesapp.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.groceriesapp.Constants;
import com.example.groceriesapp.R;
import com.example.groceriesapp.adapter.AdapterCartItem;
import com.example.groceriesapp.adapter.AdapterProductUser;
import com.example.groceriesapp.models.ModelCartItem;
import com.example.groceriesapp.models.ModelProducts;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class ShopDetailsActivity extends AppCompatActivity {

    private ImageView shopIv;
    private TextView shopNameTv,phoneTv,emailTv,openCloseTv,deliveryTv,addressTv
            ,filterProductsTv,cartCountTv;
    private ImageButton callBtn,mapBtn,cartIb,backBtn,filterProductBtn,reviewsIb;
    private EditText searchProductEt;
    private RecyclerView productsRv;
    private RatingBar ratingBar;

    private String shopUid;
    private String myLatitude, myLongitude,myPhone;
    private String shopName, shopPhone, shopEmail, shopAddress, shopLatitude, shopLongitude;
    public String deliveryFee;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelProducts> productsList;
    private AdapterProductUser adapterProductUser;

    private EasyDB easyDB;

    //cart
    private ArrayList<ModelCartItem> cartItems;
    private AdapterCartItem adapterCartItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        //init ui
        shopIv = findViewById(R.id.shopIv);
        shopNameTv = findViewById(R.id.shopNameTv);
        phoneTv = findViewById(R.id.phoneTv);
        emailTv = findViewById(R.id.emailTv);
        openCloseTv = findViewById(R.id.openCloseTv);
        deliveryTv = findViewById(R.id.deliveryTv);
        addressTv = findViewById(R.id.addressTv);
        filterProductsTv = findViewById(R.id.filterProductsTv);
        callBtn = findViewById(R.id.callBtn);
        mapBtn = findViewById(R.id.mapBtn);
        cartIb = findViewById(R.id.cartIb);
        backBtn = findViewById(R.id.backBtn);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        searchProductEt = findViewById(R.id.searchProductEt);
        productsRv = findViewById(R.id.productsRv);
        cartCountTv = findViewById(R.id.cartCountTv);
        reviewsIb = findViewById(R.id.reviewsIb);
        ratingBar = findViewById(R.id.ratingBar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //get uid of the shop from intent
        shopUid = getIntent().getStringExtra("shopUid");
        firebaseAuth = FirebaseAuth.getInstance();
        loadMyInfo();
        loadShopDetails();
        loadShopProducts();
        loadReviews();

        easyDB = EasyDB.init(this, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        //each item delete
        deleteCartData();
        cartCount();

        //search
        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterProductUser.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cartIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show cart dialog
                showCartDialog();

            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhone();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });
        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShopDetailsActivity.this);
                builder.setTitle("Choose Category:")
                        .setItems(Constants.options1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected item
                                String selected = Constants.options1[which];
                                filterProductsTv.setText(selected);
                                if (selected.equals("All")){
                                    //load all
                                    loadShopProducts();
                                }
                                else {
                                    //load filtered
                                    adapterProductUser.getFilter().filter(selected);
                                }

                            }
                        })
                        .show();
            }
        });

        //handle reviewsBtn click, open activity
        reviewsIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass shop uid to show its
                Intent intent = new Intent(ShopDetailsActivity.this, ShopReviewsActivity.class);
                intent.putExtra("shopUid", shopUid);
                startActivity(intent);

            }
        });



    }

    private float ratingSum=0;
    private void loadReviews() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        ratingSum = 0;
                        for (DataSnapshot ds : snapshot.getChildren()){
                            float rating = Float.parseFloat(""+ds.child("ratings").getValue());
                            ratingSum = ratingSum+rating;


                        }

                        long numberOfReviews = snapshot.getChildrenCount();
                        float avgRating = ratingSum/numberOfReviews;

                        ratingBar.setRating(avgRating);

                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });

    }

    private void deleteCartData() {

        easyDB.deleteAllDataFromTable();// delete all data

    }
    public void cartCount(){
        //keep it public so we can access in adapter
        //get cart count
        int count = easyDB.getAllData().getCount();
        if (count==0){
            //no item in cart, hide cart count textview
            cartCountTv.setVisibility(View.GONE);
        }
        else{
            //item in cart, hide cart count textview
            cartCountTv.setVisibility(View.VISIBLE);
            cartCountTv.setText(""+count);

        }
    }

    public double allTotalPrice = 0.0;
    public TextView sTotalTv,dFeeTv,totalFeeTv,promoDescriptionTv,discountTv;
    public EditText promoCodeEt;
    public Button applyBtn;
    private void showCartDialog() {
        //init list
        cartItems= new ArrayList<>();


        //inflate cart item
        View view = LayoutInflater.from(this).inflate(R.layout.dailog_cart, null);
        //init view
        TextView shopNameTv = view.findViewById(R.id.shopNameTv);
        promoDescriptionTv = view.findViewById(R.id.promoDescriptionTv);
         sTotalTv = view.findViewById(R.id.sTotalTv);
         dFeeTv = view.findViewById(R.id.dFeeTv);
        discountTv = view.findViewById(R.id.discountTv);
        applyBtn = view.findViewById(R.id.applyBtn);
         promoCodeEt = view.findViewById(R.id.promoCodeEt);
         totalFeeTv = view.findViewById(R.id.totalFeeTv);
        Button checkoutBtn = view.findViewById(R.id.checkoutBtn);
        FloatingActionButton validateBtn = view.findViewById(R.id.validateBtn);
        RecyclerView cartItemsRv = view.findViewById(R.id.cartItemsRv);

        if (isPromoCodeApplied){
            promoDescriptionTv.setVisibility(View.VISIBLE);
            applyBtn.setVisibility(View.VISIBLE);
            applyBtn.setText("Applied");
            promoCodeEt.setText(promoCode);
            promoDescriptionTv.setText(promoDescription);
        }
        else{
            //not applied
            promoDescriptionTv.setVisibility(View.GONE);
            applyBtn.setVisibility(View.GONE);
            applyBtn.setText("Apply");
        }

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set view to dialog
        builder.setView(view);

        EasyDB easyDB = EasyDB.init(this, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))

                .doneTableColumn();

        //get all records from db
        Cursor res = easyDB.getAllData();
        while (res.moveToNext()){
            String id = res.getString(1);
            String pId = res.getString(2);
            String name = res.getString(3);
            String price = res.getString(4);
            String cost = res.getString(5);
            String quantity = res.getString(6);


            allTotalPrice = allTotalPrice + Double.parseDouble(cost);

            ModelCartItem modelCartItem = new ModelCartItem(""+id,
                    ""+pId,
                    ""+name,
                    ""+price,
                    ""+cost,
                    ""+quantity
            );

            cartItems.add(modelCartItem);


        }
        //setup adapter
        adapterCartItem = new AdapterCartItem(this, cartItems);
        //set to recyclerView
        cartItemsRv.setAdapter(adapterCartItem);

        if (isPromoCodeApplied) {
            priceWithDiscount();
        }
        else{
            priceWithoutDiscount();
        }

       /* dFeeTv.setText("$"+deliveryFee);
        sTotalTv.setText("$"+String.format("%.2f", allTotalPrice));
        totalFeeTv.setText("$"+(allTotalPrice+Double.parseDouble(deliveryFee.replace("$", ""))));*/

        //show dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        //dialog dismiss
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allTotalPrice = 0.00;
            }
        });

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myLatitude.equals("") || myLatitude.equals("null") || myLongitude.equals("") || myLongitude.equals("null")){
                    Toast.makeText(ShopDetailsActivity.this, "Please enter your address in you profile before placing order...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (myPhone.equals("") || myPhone.equals("null")){
                    Toast.makeText(ShopDetailsActivity.this, "Please enter your phone Number in you profile before placing order...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cartItems.size()==0){
                    Toast.makeText(ShopDetailsActivity.this, "No item in cart", Toast.LENGTH_SHORT).show();
                    return;
                }

                submitOrder();
            }
        });
        //start validating promo code when validate button is pressed
        validateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String promotionCode = promoCodeEt.getText().toString().trim();
                if (TextUtils.isEmpty(promotionCode)){
                    Toast.makeText(ShopDetailsActivity.this, "Please enter promo code...", Toast.LENGTH_SHORT).show();
                }
                else{
                    checkCodeAvailability(promotionCode);
                }
            }
        });

        //apply code if valid, no need to chk if valid or not
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPromoCodeApplied = true;
                applyBtn.setText("Applied");

                priceWithDiscount();
            }
        });

    }
    private void priceWithDiscount() {
        discountTv.setText("$"+ promoPrice);
        dFeeTv.setText("$"+ deliveryFee);
        sTotalTv.setText("$"+ String.format("%.2f", allTotalPrice));
        totalFeeTv.setText("$" + (allTotalPrice + Double.parseDouble(deliveryFee.replace("$", "")) - Double.parseDouble(promoPrice)));

    }

    private void priceWithoutDiscount() {
        discountTv.setText("$0");
        dFeeTv.setText("$"+ deliveryFee);
        sTotalTv.setText("$"+ String.format("%.2f", allTotalPrice));
        totalFeeTv.setText("$" + (allTotalPrice + Double.parseDouble(deliveryFee.replace("$", ""))));


    }
    public boolean isPromoCodeApplied = false;
    public String promoId,promoTimestamp, promoCode, promoDescription, promoExpDate, promoMinimumOrderPrice, promoPrice;
    private void checkCodeAvailability(String promotionCode){//promotion code is promo code entered by user
        //progress bar
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait");
        progressDialog.setMessage("Checking Promo code");
        progressDialog.setCanceledOnTouchOutside(false);

        //promo is not applied yet
        isPromoCodeApplied = false;
        applyBtn.setText("Apply");
        priceWithoutDiscount();

        //check promo code available
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Promotions").orderByChild("promoCode").equalTo(promotionCode)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        //check if prime code exists
                        if (snapshot.exists()){
                            //promo code exists
                            progressDialog.dismiss();
                            for (DataSnapshot ds: snapshot.getChildren()){
                                promoId = ""+ds.child("id").getValue();
                                promoTimestamp = ""+ds.child("timestamp").getValue();
                                promoCode = ""+ds.child("promoCode").getValue();
                                promoDescription = ""+ds.child("description").getValue();
                                promoExpDate = ""+ds.child("expireDate").getValue();
                                promoMinimumOrderPrice = ""+ds.child("minimumOrderPrice").getValue();
                                promoPrice = ""+ds.child("promoPrice").getValue();

                                //now check if code is expired or not
                                checkCodeExpireDate();
                            }
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(ShopDetailsActivity.this, "Invalid promo code", Toast.LENGTH_SHORT).show();
                            applyBtn.setVisibility(View.GONE);
                            promoDescriptionTv.setVisibility(View.GONE);
                            promoDescriptionTv.setText("");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
    }

    private void checkCodeExpireDate() {
        //get current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) +1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //concatenate date
        String todayDate = day +"/"+ month +"/"+ year;


        //check expire
        try {
            SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
            Date currentDate = sdformat.parse(todayDate);
            Date expireDate = sdformat.parse(promoExpDate);
            //compare dates
            if (expireDate.compareTo(currentDate) >0){
                //date 1 occurs after date 2
                checkMinimumOrderPrice();
            }
            else  if (expireDate.compareTo(currentDate) < 0){
                //date 1 occurs after date 2
                Toast.makeText(this, "The promotion code is Expired on"+promoExpDate, Toast.LENGTH_SHORT).show();
                applyBtn.setVisibility(View.GONE);
                promoDescriptionTv.setVisibility(View.GONE);
                promoDescriptionTv.setText("");
            }
            else  if (expireDate.compareTo(currentDate) == 0){
                //date 1 occurs after date 2
                checkMinimumOrderPrice();
            }

        }catch (Exception e){
            //if anything goes wrong causing exception while comparing current date and expiry date
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            applyBtn.setVisibility(View.GONE);
            promoDescriptionTv.setVisibility(View.GONE);
            promoDescriptionTv.setText("");
        }
    }

    private void checkMinimumOrderPrice() {
        //each promo code have minimum order price requirement, if order price is less then required then does not to apply
        if (Double.parseDouble(String.format("%.2f", allTotalPrice)) < Double.parseDouble(promoMinimumOrderPrice)){
            //current order price is less then minimum order price required by promo code, so don't allow to apply
            Toast.makeText(this, "This code is valid for other with minimum amount: $"+promoMinimumOrderPrice, Toast.LENGTH_SHORT).show();
            applyBtn.setVisibility(View.GONE);
            promoDescriptionTv.setVisibility(View.GONE);
            promoDescriptionTv.setText("");
        }
        else{
            //current order price is equal to or greater than  minimum order price required by promo code,allow apply code
            applyBtn.setVisibility(View.VISIBLE);
            promoDescriptionTv.setVisibility(View.VISIBLE);
            promoDescriptionTv.setText(promoDescription);

        }
    }


    private void submitOrder() {
        progressDialog.setMessage("Placing order...");
        progressDialog.show();

        final String timestamp = ""+System.currentTimeMillis();
        String cost = totalFeeTv.getText().toString().trim().replace("$", "");

        //add latitude and longitude

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId",""+timestamp);
        hashMap.put("orderTime",""+timestamp);
        hashMap.put("orderStatus","InProgress"); //in prgrs/cncl/cmplt
        hashMap.put("orderCost",""+cost);
        hashMap.put("orderBy",""+firebaseAuth.getUid());
        hashMap.put("orderTo", ""+shopUid);
        hashMap.put("deliveryFee",""+deliveryFee);
        hashMap.put("latitude", ""+myLatitude);
        hashMap.put("longitude", ""+myLongitude);
        if (isPromoCodeApplied){
            //applied
            hashMap.put("discount",""+promoPrice);
        }else{
            //promo not applied
            hashMap.put("discount","0");
        }


        //add to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Orders");
        ref.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        for (int i = 0; i < cartItems.size(); i++) {
                            String pId = cartItems.get(i).getpId();
                            String id = cartItems.get(i).getId();
                            String cost = cartItems.get(i).getCost();
                            String name = cartItems.get(i).getName();
                            String price = cartItems.get(i).getPrice();
                            String quantity = cartItems.get(i).getQuantity();

                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("pId", pId);
                            hashMap1.put("name", name);
                            hashMap1.put("price", price);
                            hashMap1.put("quantity", quantity);
                            hashMap1.put("cost", cost);

                            ref.child(timestamp).child("Items").child(pId).setValue(hashMap1);

                        }
                        progressDialog.dismiss();
                        Toast.makeText(ShopDetailsActivity.this, "Order place successfully", Toast.LENGTH_SHORT).show();

                        prepareNotificationMessage(timestamp);



                    }


                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ShopDetailsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }


    private void openMap() {
        String address = "http://maps.google.com/maps?q=loc:" + myLatitude + "," +myLongitude + "&daddr=" + shopLatitude + "," +shopLongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse(address));
        startActivity(intent);

    }

    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(shopPhone))));
        Toast.makeText(this, ""+shopPhone, Toast.LENGTH_SHORT).show();
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String name = "" + ds.child("name").getValue();
                            String email = "" + ds.child("email").getValue();
                             myPhone = "" + ds.child("phone").getValue();
                            String city = "" + ds.child("city").getValue();
                            String profileImage = "" + ds.child("profileImage").getValue();
                            String accountType = "" + ds.child("accountType").getValue();
                            myLatitude = ""+ds.child("latitude").getValue();
                            myLongitude = ""+ds.child("longitude").getValue();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadShopDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                String name = "" + snapshot.child("name").getValue();
                shopName = "" + snapshot.child("shopName").getValue();
                shopEmail = "" + snapshot.child("email").getValue();
                shopPhone = "" + snapshot.child("phone").getValue();
                shopLatitude = "" + snapshot.child("latitude").getValue();
                shopAddress = "" + snapshot.child("address").getValue();
                shopLongitude = "" + snapshot.child("longitude").getValue();
                deliveryFee = ""+snapshot.child("deliveryFee").getValue();
                String profileImage = ""+snapshot.child("profileImage").getValue();
                String shopOpen = ""+snapshot.child("shopOpen").getValue();

                //set data
                shopNameTv.setText(shopName);
                emailTv.setText(shopEmail);
                deliveryTv.setText("Delivery Fee: $"+deliveryFee);
                addressTv.setText(shopAddress);
                phoneTv.setText(shopPhone);
                if (shopOpen.equals("true")){
                    openCloseTv.setText("Open");
                }
                else{
                    openCloseTv.setText("Close");
                }
                try{
                    Picasso.get().load(profileImage).into(shopIv);
                } catch (Exception e) {
                    shopIv.setImageResource(R.drawable.nb);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadShopProducts() {
        productsList = new ArrayList<>();

        DatabaseReference  reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        //clear list before adding
                        productsList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ModelProducts modelProducts = ds.getValue(ModelProducts.class);
                            productsList.add(modelProducts);

                        }
                        //setup adapter
                        adapterProductUser = new AdapterProductUser(ShopDetailsActivity.this, productsList);
                        //set adapter
                        productsRv.setAdapter(adapterProductUser);

                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });

    }

    private void prepareNotificationMessage(String orderId){
        //when user places order, send notification to seller

        //prepare data for notification
        String NOTIFICATION_TOPIC = "/topics/"+Constants.FCM_TOPIC; // must be same as subscribe by user
        String NOTIFICATION_TITLE = "New Order"+orderId;
        String NOTIFICATION_MESSAGE = "Congratulations....! You have new order.";
        String NOTIFICATION_TYPE = "NewOrder";

        //prepare json (what to send and where to send)
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();
        try{
            //what to send
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid", firebaseAuth.getUid()); //since we are logged in as buyer ui
            notificationBodyJo.put("sellerUid", shopUid);
            notificationBodyJo.put("orderId", orderId);
            notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);
            //where to send
            notificationJo.put("to", NOTIFICATION_TOPIC); //to all who subscribe to this topic
            notificationJo.put("data", NOTIFICATION_MESSAGE);

        } catch (Exception e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        sendFcmNotification(notificationJo, orderId);
    }

    private void sendFcmNotification(JSONObject notificationJo, String orderId) {
        //send volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //after sending fcm start
                //open order details Activity
                Intent intent = new Intent(ShopDetailsActivity.this, OrderDetailsUsersActivity.class);
                intent.putExtra("orderTo", shopUid);
                intent.putExtra("orderId", orderId);
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //if failed sending fcm start
                Intent intent = new Intent(ShopDetailsActivity.this, OrderDetailsUsersActivity.class);
                intent.putExtra("orderTo", shopUid);
                intent.putExtra("orderId", orderId);
                startActivity(intent);

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                //put request header
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key="+Constants.FCM_KEY);

                return headers;
            }
        };

        //EnQue  the volley request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }


}
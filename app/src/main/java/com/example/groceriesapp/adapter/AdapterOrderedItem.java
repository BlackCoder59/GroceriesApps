package com.example.groceriesapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceriesapp.R;
import com.example.groceriesapp.models.ModelOrderedItem;

import java.util.ArrayList;


public class AdapterOrderedItem extends RecyclerView.Adapter<AdapterOrderedItem.HolderOrderedItem> {

    private Context context;
    private ArrayList<ModelOrderedItem> modelOrderedItemList;

    public AdapterOrderedItem(Context context, ArrayList<ModelOrderedItem> modelOrderedItemList) {
        this.context = context;
        this.modelOrderedItemList = modelOrderedItemList;
    }

    @NonNull
    @Override
    public HolderOrderedItem onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        //inflate layout cart item
        View view = LayoutInflater.from(context).inflate(R.layout.row_orded_item, parent, false);
        return new HolderOrderedItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  HolderOrderedItem holder, int position) {

        //get data
        ModelOrderedItem modelOrderedItem = modelOrderedItemList.get(position);
        String getpId = modelOrderedItem.getpId();
        String name = modelOrderedItem.getName();
        String cost = modelOrderedItem.getCost();
        String price = modelOrderedItem.getPrice();
        String quantity = modelOrderedItem.getQuantity();

        //set data
        holder.itemTitleTv.setText(name);
        holder.itemPriceEachTv.setText("$"+price);
        holder.itemPriceTv.setText("$"+cost);
        holder.itemQuantityTv.setText("["+quantity+"]");

    }

    @Override
    public int getItemCount() {
        return modelOrderedItemList.size();
    }

    class HolderOrderedItem extends RecyclerView.ViewHolder{

        //views
        private TextView itemTitleTv,itemPriceTv,itemPriceEachTv,itemQuantityTv;

        public HolderOrderedItem(@NonNull View itemView) {
            super(itemView);

            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
        }
    }
}

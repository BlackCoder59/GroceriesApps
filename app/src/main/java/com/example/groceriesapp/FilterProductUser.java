package com.example.groceriesapp;

import android.widget.Filter;

import com.example.groceriesapp.adapter.AdapterProductUser;
import com.example.groceriesapp.models.ModelProducts;

import java.util.ArrayList;

public class FilterProductUser extends Filter {

    private AdapterProductUser adapter;
    private ArrayList<ModelProducts> filterList;

    public FilterProductUser(AdapterProductUser adapter, ArrayList<ModelProducts> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //validate data for search query
        if (constraint != null && constraint.length()>0){

            //change to upper case to make case insensitive
            constraint = constraint.toString().toUpperCase();
            //store our filtered list
            ArrayList<ModelProducts> filteredModels = new ArrayList<>();
            for (int i=0; i<filterList.size(); i++){
                //check
                if (filterList.get(i).getProductTitle().toUpperCase().contains(constraint) ||
                        filterList.get(i).getProductCategory().toUpperCase().contains(constraint)){
                    // add filtered data to list
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else {
            results.count = filterList.size();
            results.values = filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.productsList= (ArrayList<ModelProducts>) results.values;
        //refresh adapter
        adapter.notifyDataSetChanged();

    }
}

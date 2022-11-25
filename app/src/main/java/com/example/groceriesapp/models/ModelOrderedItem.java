package com.example.groceriesapp.models;

public class ModelOrderedItem {

    private String pId,name,price,quantity,cost;

    public ModelOrderedItem() {
    }

    public ModelOrderedItem(String pId, String name, String price, String quantity, String cost) {

        this.pId = pId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.cost = cost;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}

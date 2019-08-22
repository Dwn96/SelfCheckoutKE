package com.mdg.selfcheckoutke;

public class Product {
    private String productName;
    private String id;
    private int quantity, price;

    public Product(){
        this.id = "";
        this.productName = "";
        this.quantity = 0;
        this.price = 0;
    }

    public void setProductName(String productName){ this.productName = productName;}
    public void setPid(String id){ this.id = id;}
    public void setQuantity(int qty){ this.quantity = qty;}
    public void setPrice(int qty){ this.price = qty;}

    public String getProductName(){ return this.productName;}
    public String getPId(){ return this.id;}
    public int getQuantity(){ return this.quantity;}
    public int getPrice(){ return this.price;}
}

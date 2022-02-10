package com.example.e_commerce.Model;

public class Cart {
    private String ProductId, NoOfItems;

    public Cart() {
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }



    public String getNoOfItems() {
        return NoOfItems;
    }

    public void setNoOfItems(String noOfItems) {
        NoOfItems = noOfItems;
    }

    public Cart(String productId, String userPhone, String noOfItems){
        ProductId = productId;
        NoOfItems = noOfItems;
    }


}

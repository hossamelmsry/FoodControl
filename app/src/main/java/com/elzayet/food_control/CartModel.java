package com.elzayet.food_control;

import androidx.annotation.Keep;

@Keep
public class CartModel {
    private String productId,productQuantity,orderTopping,orderPrice,productSize;

    public CartModel() { }

    public String getProductQuantity() { return productQuantity; }

    public String getProductId() { return productId; }

    public String getProductSize() {  return productSize;   }

    public String getOrderTopping() { return orderTopping; }

    public String getOrderPrice() {
        return orderPrice;
    }
}
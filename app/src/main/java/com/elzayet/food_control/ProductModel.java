package com.elzayet.food_control;

import androidx.annotation.Keep;

@Keep
public class ProductModel {
    private String menuName,  productId,  productImage,  productName,  productDescription,  productPrice ;

    public ProductModel() { }

    public ProductModel(String menuName, String productId, String productImage, String productName, String productDescription, String productPrice) {
        this.menuName = menuName;
        this.productId = productId;
        this.productImage = productImage;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductImage() {
        return productImage;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getProductPrice() {
        return productPrice;
    }

}

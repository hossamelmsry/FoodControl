package com.elzayet.food_control;

import androidx.annotation.Keep;

@Keep
public class ProductModel {
    private String menuName,productId,productImage,productName,productDescription,smallSize,mediumSize,largeSize;

    public ProductModel() { }

    public ProductModel(String menuName,String productId,String productImage,String productName,String productDescription,String smallSize,String mediumSize,String largeSize) {
        this.menuName     = menuName;
        this.productId    = productId;
        this.productImage = productImage;
        this.productName  = productName;
        this.smallSize    = smallSize;
        this.mediumSize   = mediumSize;
        this.largeSize    = largeSize;
        this.productDescription = productDescription;
    }

    public String getMenuName() { return menuName; }

    public String getProductId() { return productId; }

    public String getProductImage() { return productImage; }

    public String getProductName() { return productName; }

    public String getProductDescription() { return productDescription; }

    public String getSmallSize() { return smallSize; }

    public String getMediumSize() { return mediumSize; }

    public String getLargeSize() { return largeSize; }
}

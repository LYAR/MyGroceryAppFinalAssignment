package com.example.mygroceryapp.models;

import java.io.Serializable;

public class CartModel implements Serializable {
    String cartId, userId, name, desc, price, quantity, image;

    public CartModel() {
    }

    public CartModel(String cartId, String userId, String name, String desc, String price, String quantity, String image) {
        this.cartId = cartId;
        this.userId = userId;
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

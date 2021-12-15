package com.elzayet.food_control;

import androidx.annotation.Keep;

@Keep
public class MenuModel {
    private String menuId,menuImage , menuName ;

    public MenuModel() { }

    public String getMenuId() {
        return menuId;
    }

    public String getMenuImage() {
        return menuImage;
    }

    public String getMenuName() {
        return menuName;
    }
}

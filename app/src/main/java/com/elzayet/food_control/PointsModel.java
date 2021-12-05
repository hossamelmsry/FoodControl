package com.elzayet.food_control;

import androidx.annotation.Keep;

@Keep
public class PointsModel {
    private String points;

    public PointsModel() { }

    public PointsModel(String points) {
        this.points = points;
    }

    public String getPoints() {
        return points;
    }
}

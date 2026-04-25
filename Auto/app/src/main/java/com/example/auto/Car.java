package com.example.auto;

import java.util.ArrayList;
public class Car {
    String image;
    String model;
    String platenumber;
    String insurance;
    String vignette;
    String inspection;

    public Car(String image, String model, String platenumber, String insurance, String vignette, String inspection){
        this.image = image;
        this.model = model;
        this.platenumber = platenumber;
        this.insurance = insurance;
        this.vignette = vignette;
        this.inspection = inspection;
    }

    public String get_image() {
        return image;
    }
    public String get_model() {
        return model;
    }
    public String get_platenumber() {
        return platenumber;
    }
    public String get_insurance() {
        return insurance;
    }
    public String get_vignette() {
        return vignette;
    }
    public String get_inspection() {
        return inspection;
    }
    public String get_model_plus_platenumber() {
        return model + " | " + platenumber;
    }
}

package com.influx.marcus.theatres.foodbeverage;

import java.util.ArrayList;

public class FoodCategoryList {
  ArrayList<FoodModel> combolist;
  ArrayList<FoodModel> popcornlist;

  public ArrayList<FoodModel> getCombolist() {
    return combolist;
  }

  public void setCombolist(ArrayList<FoodModel> combolist) {
    this.combolist = combolist;
  }

  public ArrayList<FoodModel> getPopcornlist() {
    return popcornlist;
  }

  public void setPopcornlist(ArrayList<FoodModel> popcornlist) {
    this.popcornlist = popcornlist;
  }

  public ArrayList<FoodModel> getRecommanedlist() {
    return recommanedlist;
  }

  public void setRecommanedlist(ArrayList<FoodModel> recommanedlist) {
    this.recommanedlist = recommanedlist;
  }

  ArrayList<FoodModel> recommanedlist;
}

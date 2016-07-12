package com.arifin.sujud.universal;

/**
 * Created by Shahzad Ahmad on 30-Jun-15.
 */
public class Constants {
    String CategoryName;

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }
    public Constants(String categoryName){
        this.CategoryName=categoryName;

    }
}

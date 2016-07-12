package com.arifin.sujud.universal;

/**
 * Created by BuAli_bluehorn on 27-May-15.
 */
public class Constant {
    String suhoor;
    String aftar;
    String date;
    public Constant(String suhoor, String aftar, String date){
        this.suhoor = suhoor;
        this.aftar = aftar;
        this.date=date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSuhoor() {
        return suhoor;
    }

    public void setSuhoor(String suhoor) {
        this.suhoor = suhoor;
    }

    public String getAftar() {
        return aftar;
    }

    public void setAftar(String aftar) {
        this.aftar = aftar;
    }


}

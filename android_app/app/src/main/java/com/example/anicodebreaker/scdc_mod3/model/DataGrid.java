package com.example.anicodebreaker.scdc_mod3.model;

/**
 * Created by anicodebreaker on 25/12/16.
 */

// Class to be used as template for the data in each item of the gridview
public class DataGrid {

    String titleG;
    String dataG;

    public DataGrid(String titleG, String dataG) {
        this.titleG = titleG;
        this.dataG = dataG;
    }

    public DataGrid() {
    }

    public String getTitleG() {
        return titleG;
    }

    public void setTitleG(String titleG) {
        this.titleG = titleG;
    }

    public String getDataG() {
        return dataG;
    }

    public void setDataG(String dataG) {
        this.dataG = dataG;
    }
}

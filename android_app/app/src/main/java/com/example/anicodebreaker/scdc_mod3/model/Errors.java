package com.example.anicodebreaker.scdc_mod3.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by anicodebreaker on 26/12/16.
 */

public class Errors {

    @SerializedName("msg")
    @Expose
    private String msg;

    /**
     * No args constructor for use in serialization
     *
     */
    public Errors() {
    }

    /**
     * Constructor of Errors object
     * @param msg message
     */
    public Errors(String msg) {
        super();
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}

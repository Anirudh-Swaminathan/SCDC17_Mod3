package com.example.anicodebreaker.scdc_mod3.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HealthData {

    @SerializedName("msg")
    @Expose
    private int msg;
    @SerializedName("errors")
    @Expose
    private Errors errors;
    @SerializedName("ret")
    @Expose
    private String ret;

    /**
     * No args constructor for use in serialization
     *
     */
    public HealthData() {
    }

    /**
     *
     * @param ret
     * @param errors
     * @param msg
     */
    public HealthData(int msg, Errors errors, String ret) {
        super();
        this.msg = msg;
        this.errors = errors;
        this.ret = ret;
    }

    public int getMsg() {
        return msg;
    }

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

}
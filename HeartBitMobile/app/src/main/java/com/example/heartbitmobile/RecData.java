package com.example.heartbitmobile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class RecData {
    @Expose
    @SerializedName("PatientId")
    public String patientId;
    @Expose
    @SerializedName("ParameterId")
    public String parameterId;
    @Expose
    @SerializedName("Date")
    public java.util.Date date;
    @Expose
    @SerializedName("Level")
    public int level;
}

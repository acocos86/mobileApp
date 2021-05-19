package com.example.heartbitmobile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pacient {
    @Expose
    @SerializedName("PatientId")
    public Integer patientId;
    @Expose
    @SerializedName("UserId")
    public Integer id;
    @Expose
    @SerializedName("DoctorId")
    public Integer doctorId;
    @Expose
    @SerializedName("Name")
    public String name;
    @Expose
    @SerializedName("Surname")
    public String surname;
    @Expose
    @SerializedName("PNC")
    public String cnp;
    @Expose
    @SerializedName("Age")
    public Integer age;
    @Expose
    @SerializedName("AddressStreet")
    public String street;
    @Expose
    @SerializedName("AddressNumber")
    public String stNumber;
    @Expose
    @SerializedName("AddressLocation")
    public String location;
    @Expose
    @SerializedName("AddressCounty")
    public String country;
    @Expose
    @SerializedName("AddressPostalCode")
    public String pcode;
    @Expose
    @SerializedName("Phone")
    public String phone;
    @Expose
    @SerializedName("Email")
    public String email;
    @Expose
    @SerializedName("Profession")
    public String prof;
    @Expose
    @SerializedName("Job")
    public String job;

}

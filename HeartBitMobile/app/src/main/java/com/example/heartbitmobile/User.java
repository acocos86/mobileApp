package com.example.heartbitmobile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @Expose
    @SerializedName("UserId")
    public Integer id;
    @Expose
    @SerializedName("RoleId")
    public String roleId;
    @Expose
    @SerializedName("Name")
    public String name;
    @Expose
    @SerializedName("Password")
    public String password;

    public String getPassword() {
        return password;
    }
    public String getName() {
        return name;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer userId) {
        this.id = userId;
    }
    public void setName(String name) {
        this.name = name;
    }

}

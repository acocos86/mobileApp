package com.example.heartbitmobile;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserService {
    @GET("/user/{id}")
    Call<List<User>> getUser(@Path("id") Long userId);
}

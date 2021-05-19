package com.example.heartbitmobile;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PacientService {
    @GET("/patient/{id}")
    Call<List<Pacient>> getPacient(@Path("id") Long userId);
}

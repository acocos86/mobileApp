package com.example.heartbitmobile;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RecDataSevice {
    @PUT("alert/")
    Call<ResponseBody> updateRecData(@Body RecData recData);
}

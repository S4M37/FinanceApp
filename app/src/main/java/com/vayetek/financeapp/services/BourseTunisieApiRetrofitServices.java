package com.vayetek.financeapp.services;


import com.vayetek.financeapp.config.Endpoints;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface BourseTunisieApiRetrofitServices {
    @POST(Endpoints.BOURSE_PERFORMANCE)
    Call<ResponseBody> getPerformanceIndices(@Header("Authorization") String token);

}

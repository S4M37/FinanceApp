package com.vayetek.financeapp.services;


import com.google.gson.JsonArray;
import com.vayetek.financeapp.config.Endpoints;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BourseTunisWebApiRetrofitServices {
    @POST(Endpoints.INDICE_SECTORIEL_API)
    Call<ResponseBody> getPerformanceIndices(@Body JsonArray requestBody);

    @POST(Endpoints.HANDSHAKE)
    Call<ResponseBody> handshake(@Body JsonArray requestBody);

}

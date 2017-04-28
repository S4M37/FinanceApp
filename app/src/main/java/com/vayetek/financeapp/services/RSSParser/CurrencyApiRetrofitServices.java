package com.vayetek.financeapp.services.RSSParser;


import com.vayetek.financeapp.config.Endpoints;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CurrencyApiRetrofitServices {
    @GET(Endpoints.CURRENCY_CONVERTER_API)
    Call<ResponseBody> convertCurrency(@Query("from") String codeCurrencyFrom, @Query("to") String codeCurrencyTo,
                                       @Query("amount") double amountCurrency);

    @GET(Endpoints.COURT_EXCHANGE_API)
    Call<ResponseBody> getCourtExchange();

}

package com.vayetek.financeapp.utils;

import com.google.gson.Gson;
import com.vayetek.financeapp.config.Endpoints;
import com.vayetek.financeapp.services.BourseTunisieApiRetrofitServices;
import com.vayetek.financeapp.services.CurrencyApiRetrofitServices;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utils {

    //TIMEOUT with Seconds
    private static final long TIMEOUT = 15;

    // CouchBase Api retrofit Singleton implementation

    //StreetHolesApiRetrofitServices singleton implementation
    private static CurrencyApiRetrofitServices currencyApiRetrofitServices;

    public static CurrencyApiRetrofitServices getCurrencyApiRetrofitServices() {
        if (currencyApiRetrofitServices == null) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(interceptor)
                    .connectTimeout(Utils.TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(Utils.TIMEOUT, TimeUnit.SECONDS).build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Endpoints.SERVER_IP)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            currencyApiRetrofitServices = retrofit.create(CurrencyApiRetrofitServices.class);
        }
        return currencyApiRetrofitServices;
    }

    private static BourseTunisieApiRetrofitServices bourseTunisieApiRetrofitServices;

    public static BourseTunisieApiRetrofitServices getBourseTunisieApiRetrofitServices() {
        if (bourseTunisieApiRetrofitServices == null) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(interceptor)
                    .connectTimeout(Utils.TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(Utils.TIMEOUT, TimeUnit.SECONDS).build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Endpoints.BOURSE_TUNIS_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            bourseTunisieApiRetrofitServices = retrofit.create(BourseTunisieApiRetrofitServices.class);
        }
        return bourseTunisieApiRetrofitServices;
    }

    private static Gson gson;

    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

}

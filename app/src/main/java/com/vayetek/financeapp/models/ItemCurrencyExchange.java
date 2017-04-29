package com.vayetek.financeapp.models;

import com.google.gson.annotations.SerializedName;

public class ItemCurrencyExchange {
    @SerializedName("nom")
    public String currency;
    public String code;
    @SerializedName("unit")
    public
    String unit;
    public String buy;
    public String sell;
}

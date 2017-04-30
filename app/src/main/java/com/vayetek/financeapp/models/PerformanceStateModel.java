package com.vayetek.financeapp.models;

import com.google.gson.annotations.SerializedName;

public class PerformanceStateModel {
    @SerializedName("INSTRUMENT_ID")
    private
    String instrumentId;
    @SerializedName("LAST")
    private
    String last;
    @SerializedName("PREVIOUS_CLOSE")
    private String prrviousClose;
    @SerializedName("QUANTITY")
    private
    String quantity;
    @SerializedName("VARIATION")
    private
    String variation;
    @SerializedName("VOLUME")
    private
    String volume;
    @SerializedName("cname")
    private
    String cname;
    @SerializedName("mname")
    private
    String mname;
    @SerializedName("sessiondate")
    private
    String sessionDate;

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getPrrviousClose() {
        return prrviousClose;
    }

    public void setPrrviousClose(String prrviousClose) {
        this.prrviousClose = prrviousClose;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(String sessionDate) {
        this.sessionDate = sessionDate;
    }
}

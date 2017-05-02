package com.vayetek.financeapp.models;

import java.io.Serializable;

public class IndicesSectoralModel implements Serializable {
    private String name;
    private String previousClose;
    private String value;
    private String variation;
    private double percentChange;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(String previousClose) {
        this.previousClose = previousClose;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    public double getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(double percentChange) {
        this.percentChange = percentChange;
    }

    @Override
    public String toString() {
        return "IndicesSectoralModel{" +
                "name='" + name + '\'' +
                ", previousClose='" + previousClose + '\'' +
                ", value='" + value + '\'' +
                ", variation='" + variation + '\'' +
                ", percentChange=" + percentChange +
                '}';
    }
}

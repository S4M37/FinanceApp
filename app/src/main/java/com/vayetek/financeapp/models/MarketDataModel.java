package com.vayetek.financeapp.models;


import java.io.Serializable;

public class MarketDataModel implements Serializable{
    private String name;
    private String openedWith;
    private String closedWith;
    private String up;
    private String down;
    private String volumeTitle;
    private String volumeTnd;
    private String variation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpenedWith() {
        return openedWith;
    }

    public void setOpenedWith(String openedWith) {
        this.openedWith = openedWith;
    }

    public String getClosedWith() {
        return closedWith;
    }

    public void setClosedWith(String closedWith) {
        this.closedWith = closedWith;
    }

    public String getUp() {
        return up;
    }

    public void setUp(String up) {
        this.up = up;
    }

    public String getDown() {
        return down;
    }

    public void setDown(String down) {
        this.down = down;
    }

    public String getVolumeTitle() {
        return volumeTitle;
    }

    public void setVolumeTitle(String volumeTitle) {
        this.volumeTitle = volumeTitle;
    }

    public String getVolumeTnd() {
        return volumeTnd;
    }

    public void setVolumeTnd(String volumeTnd) {
        this.volumeTnd = volumeTnd;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }
}

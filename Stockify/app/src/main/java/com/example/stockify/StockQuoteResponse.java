package com.example.stockify;

public class StockQuoteResponse {
    private double c;  // Current price
    private double dp; // Percentage change
    private double h;  // High price of the day
    private double l;  // Low price of the day
    private double o;  // Open price of the day
    private double pc; // Previous close price

    // Getters
    public double getCurrentPrice() {
        return c;
    }

    public double getPercentageChange() {
        return dp;
    }

    public double getHighPrice() {
        return h;
    }

    public double getLowPrice() {
        return l;
    }

    public double getOpenPrice() {
        return o;
    }

    public double getPreviousClosePrice() {
        return pc;
    }
}

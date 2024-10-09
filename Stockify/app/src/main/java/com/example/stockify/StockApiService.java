package com.example.stockify;

import com.example.stockify.CompanyProfileResponse;
import com.example.stockify.StockQuoteResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StockApiService {
    @GET("quote")
    Call<StockQuoteResponse> getStockQuote(@Query("symbol") String symbol, @Query("token") String apiKey);

    @GET("stock/profile2")
    Call<CompanyProfileResponse> getCompanyProfile(@Query("symbol") String symbol, @Query("token") String apiKey);
}

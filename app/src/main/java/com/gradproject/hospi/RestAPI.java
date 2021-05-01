package com.gradproject.hospi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface RestAPI {
    @GET("v2/local/search/address.json")
    Call<ResultSearchAddressPoint> getSearchAddressPoint(
            @Header("Authorization") String key,
            @Query("analyze_type") String analyze_type,
            @Query("query") String query
    );
}

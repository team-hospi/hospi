package com.gradproject.hospi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface RestAPI {
    @GET("v2/local/search/category.json")
    Call<ResultSearchCategory> getSearchCategory(
            @Header("Authorization") String key,
            @Query("category_group_code") String query,
            @Query("x") String longitude,
            @Query("y") String latitude,
            @Query("radius") String radius,
            @Query("page") String page
    );
}

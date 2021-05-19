package com.gradproject.hospi.rest.category;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface CategoryService {
    @GET("v2/local/search/category.json")
    Call<ResultCategorySearch> getSearchCategoryPoint(
            @Header("Authorization") String key,
            @Query("category_group_code") String categoryGroupCode,
            @Query("x") String longitude,
            @Query("y") String latitude,
            @Query("radius") String radius,
            @Query("page") String page,
            @Query("size") String size
    );
}

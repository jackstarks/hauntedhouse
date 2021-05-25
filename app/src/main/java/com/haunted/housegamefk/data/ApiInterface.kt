package com.haunted.housegamefk.data

import com.haunted.housegamefk.model.DataModel
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("t559TgGR")
    suspend fun getProjectList(
        @Query("af_status") afStatus: String,
        @Query("user_country") userCountry: String
    ): DataModel
}
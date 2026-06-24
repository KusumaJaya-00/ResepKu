package com.example.resepku.data.remote

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("resep.json")
    fun getResep(): Call<List<Resep>>
}
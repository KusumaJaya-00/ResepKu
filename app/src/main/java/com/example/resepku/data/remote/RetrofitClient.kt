package com.example.resepku.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 1. BASE_URL hanya boleh sampai folder induknya saja (wajib diakhiri /)
    private const val BASE_URL = "https://raw.githubusercontent.com/LucioousSs/Data-Resep-Mobile/refs/heads/main/"

    // 2. Inisialisasi Retrofit secara Lazy (hanya dibuat saat dipanggil)
    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Mengubah JSON menjadi List<Resep>
            .build()

        retrofit.create(ApiService::class.java)
    }
}
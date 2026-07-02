package com.example.resepku.viewmodel

import androidx.lifecycle.ViewModel
import com.example.resepku.data.remote.ApiService
import com.example.resepku.data.remote.Resep
import com.example.resepku.data.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
    // Mengambil instance ApiService dari RetrofitClient
    private val api = RetrofitClient.instance

    // Fungsi untuk mengambil data resep dari API
    fun ambilResep(
        onBerhasil: (List<Resep>) -> Unit,
        onGagal: () -> Unit
    ) {
        api.getResep().enqueue(object : Callback<List<Resep>> {
            override fun onResponse(call: Call<List<Resep>>, response: Response<List<Resep>>) {
                // Jika request sukses (kode HTTP 200-299)
                if (response.isSuccessful) {
                    onBerhasil(response.body() ?: emptyList())
                } else {
                    onGagal()
                }
            }

            override fun onFailure(call: Call<List<Resep>>, t: Throwable) {
                // Jika terjadi kesalahan jaringan atau server mati
                onGagal()
            }
        })
    }
}

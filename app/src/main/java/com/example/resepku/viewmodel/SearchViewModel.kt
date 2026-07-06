package com.example.resepku.viewmodel

import androidx.lifecycle.ViewModel
import com.example.resepku.data.remote.Resep
import com.example.resepku.data.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel : ViewModel() {
    private val api = RetrofitClient.instance

    private var semuaResep: List<Resep> = emptyList()

    fun ambilSemuaResep(
        onBerhasil: (List<Resep>) -> Unit,
        onGagal: () -> Unit
    ) {
        api.getResep().enqueue(object : Callback<List<Resep>> {
            override fun onResponse(call: Call<List<Resep>>, response: Response<List<Resep>>) {
                if (response.isSuccessful) {
                    semuaResep = response.body() ?: emptyList()
                    onBerhasil(semuaResep)
                } else {
                    onGagal()
                }
            }

            override fun onFailure(call: Call<List<Resep>>, t: Throwable) {
                onGagal()
            }
        })
    }

    fun getDaftarKategori(): List<String> {
        val kategoriUnik = semuaResep.map { it.kategori }.distinct().sorted()
        return listOf(KATEGORI_SEMUA) + kategoriUnik
    }

    fun cariResep(keyword: String, kategoriTerpilih: String): List<Resep> {
        return semuaResep.filter { resep ->
            val cocokNama = resep.nama.contains(keyword, ignoreCase = true)
            val cocokKategori = kategoriTerpilih == KATEGORI_SEMUA || resep.kategori == kategoriTerpilih
            cocokNama && cocokKategori
        }
    }

    companion object {
        const val KATEGORI_SEMUA = "Semua"
    }
}
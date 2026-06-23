package com.example.resepku.data.remote

import java.io.Serializable

// Dipakai semua screen. Serializable supaya bisa dikirim lewat Intent ke DetailActivity.
data class Resep(
    val id: String = "",
    val nama: String = "",
    val gambar: String = "",
    val kategori: String = "",
    val deskripsi: String = "",
    val bahan: List<String> = emptyList(),
    val langkah: List<String> = emptyList()
) : Serializable
package com.example.resepku.viewmodel

import androidx.lifecycle.ViewModel
import com.example.resepku.data.local.DatabaseHelper
import com.example.resepku.data.remote.Resep

class FavoriteViewModel : ViewModel() {

    // Fungsi mengambil semua favorit
    fun muatFavorit(db: DatabaseHelper, onHasil: (List<Resep>) -> Unit) {
        val list = db.ambilSemuaFavorit()
        onHasil(list)
    }

    fun hapusDariFavorit(db: DatabaseHelper, id: String, onSelesai: (Boolean) -> Unit) {
        val berhasil = db.hapusFavorit(id)
        onSelesai(berhasil)
    }
}

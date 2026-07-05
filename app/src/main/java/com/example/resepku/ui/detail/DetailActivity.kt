package com.example.resepku.ui.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.resepku.R
import com.example.resepku.data.local.DatabaseHelper
import com.example.resepku.data.remote.Resep

class DetailActivity : AppCompatActivity() {

    private var isFavorit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Ambil objek Resep yang dikirim lewat Intent
        val resep = intent.getSerializableExtra("resep") as? Resep ?: run {
            finish()
            return
        }

        // Inisialisasi view
        val ivGambar = findViewById<ImageView>(R.id.ivGambarResep)
        val tvNama = findViewById<TextView>(R.id.tvNamaResep)
        val tvKategori = findViewById<TextView>(R.id.tvKategoriResep)
        val tvBahan = findViewById<TextView>(R.id.tvBahan)
        val tvLangkah = findViewById<TextView>(R.id.tvLangkah)
        val btnFavorit = findViewById<ImageButton>(R.id.btnFavorit)

        // Tampilkan data resep
        tvNama.text = resep.nama
        tvKategori.text = resep.kategori

        // Gambar resep via Glide
        Glide.with(this)
            .load(resep.gambar)
            .placeholder(R.drawable.bg_image_placeholder)
            .error(R.drawable.bg_image_placeholder)
            .centerCrop()
            .into(ivGambar)

        ivGambar.contentDescription = getString(R.string.desc_gambar_resep_format, resep.nama)

        // Bahan: gabung list jadi teks bernomor
        tvBahan.text = resep.bahan.mapIndexed { i, b -> "${i + 1}. $b" }
            .joinToString("\n")

        // Langkah: gabung list jadi teks bernomor
        tvLangkah.text = resep.langkah.mapIndexed { i, l -> "${i + 1}. $l" }
            .joinToString("\n")

        // Cek status favorit dari database lokal
        val dbHelper = DatabaseHelper(this)
        isFavorit = dbHelper.isFavorit(resep.id)
        aturTampilanFavorit(btnFavorit)

        // Toggle favorit saat tombol diklik
        btnFavorit.setOnClickListener {
            if (isFavorit) {
                dbHelper.hapusFavorit(resep.id)
                Toast.makeText(this, R.string.msg_favorit_dihapus, Toast.LENGTH_SHORT).show()
            } else {
                dbHelper.tambahFavorit(resep)
                Toast.makeText(this, R.string.msg_favorit_ditambah, Toast.LENGTH_SHORT).show()
            }
            isFavorit = !isFavorit
            aturTampilanFavorit(btnFavorit)
        }
    }

    // Ganti icon & warna tombol favorit sesuai status
    private fun aturTampilanFavorit(btnFavorit: ImageButton) {
        btnFavorit.setImageResource(
            if (isFavorit) R.drawable.ic_menu_favorit
            else R.drawable.ic_menu_favorit_outline
        )
        val warna = if (isFavorit) R.color.red_600 else R.color.stone_900
        btnFavorit.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, warna)
        )
        btnFavorit.contentDescription = getString(
            if (isFavorit) R.string.action_favorite_remove
            else R.string.action_favorite_add
        )
    }
}

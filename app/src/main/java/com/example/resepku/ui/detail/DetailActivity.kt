package com.example.resepku.ui.detail

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.resepku.R
import com.example.resepku.data.local.DatabaseHelper
import com.example.resepku.data.remote.Resep

class DetailActivity : AppCompatActivity() {
    private lateinit var mainContainer: LinearLayout
    private var isFavorit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        mainContainer = findViewById(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainContainer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
        val btnFavorit = findViewById<Button>(R.id.btnFavorit)

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

    // Ganti background, teks, & warna teks tombol favorit sesuai status
    private fun aturTampilanFavorit(btnFavorit: Button) {
        if (isFavorit) {
            btnFavorit.setBackgroundResource(R.drawable.bg_tombol_favorit_aktif)
            btnFavorit.setText(R.string.label_batal_favorit)
            btnFavorit.setTextColor(ContextCompat.getColor(this, R.color.orange_600))
        } else {
            btnFavorit.setBackgroundResource(R.drawable.bg_tombol_favorit)
            btnFavorit.setText(R.string.label_favorit)
            btnFavorit.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
        btnFavorit.contentDescription = getString(
            if (isFavorit) R.string.action_favorite_remove
            else R.string.action_favorite_add
        )
    }
}

package com.example.resepku.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.resepku.R
import com.example.resepku.data.remote.Resep

// Adapter = jembatan antara data (List<Resep>) dan tampilan (RecyclerView).
// onItemClick & onFavoriteClick = callback dari Fragment, dipanggil pas user klik.
class ResepAdapter(
    private val onItemClick: (Resep) -> Unit,
    private val onFavoriteClick: (Resep) -> Unit
) : RecyclerView.Adapter<ResepAdapter.ResepViewHolder>() {

    // mutableListOf = list yang bisa diubah (tambah/hapus data).
    // Set = kumpulan ID favorit yang unik, dipakai buat cek apakah resep ini favorit.
    private val daftarResep = mutableListOf<Resep>()
    private var favoriteIds: Set<String> = emptySet()

    // Dipanggil dari Fragment tiap data berubah: hapus lama → masukin baru → refresh.
    // Parameter favoriteIds bisa dikirim atau tidak (otomatis pakai nilai terakhir).
    fun submitList(daftarBaru: List<Resep>, favoriteIds: Set<String> = this.favoriteIds) {
        this.daftarResep.clear()
        this.daftarResep.addAll(daftarBaru)
        this.favoriteIds = favoriteIds
        notifyDataSetChanged()
    }

    // onCreateViewHolder: cuma dipanggil beberapa kali (sejumlah item yang muat di layar).
    // fungsinya: ambil layout XML → jadiin View → bungkus pake ViewHolder.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResepViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_resep, parent, false)
        return ResepViewHolder(view, onItemClick, onFavoriteClick)
    }

    override fun getItemCount(): Int = daftarResep.size

    // RecyclerView mau nampilin item di posisi tertentu.
    // Cek resep.id ada di favoriteIds atau tidak → true/false.
    override fun onBindViewHolder(holder: ResepViewHolder, position: Int) {
        val resep = daftarResep[position]
        holder.bind(resep, isFavorit = resep.id in favoriteIds)
    }

    class ResepViewHolder(
        itemView: View,
        private val onItemClick: (Resep) -> Unit,
        private val onFavoriteClick: (Resep) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvNama: TextView = itemView.findViewById(R.id.tvNama)
        private val chipKategori: TextView = itemView.findViewById(R.id.chipKategori)
        private val imgResep: ImageView = itemView.findViewById(R.id.imgResep)
        private val btnFavorit: ImageView = itemView.findViewById(R.id.btnFavorit)

        // Isi data ke view-view yang sudah disimpan di konstruktor ResepViewHolder.
        fun bind(resep: Resep, isFavorit: Boolean) {
            tvNama.text = resep.nama
            chipKategori.text = resep.kategori

            // Glide: library buat load gambar dari URL internet.
            // placeholder = tampil selama loading, error = kalau gagal load.
            // centerCrop = gambar dipotong rata tengah agar tidak strechy.
            Glide.with(imgResep)
                .load(resep.gambar)
                .placeholder(R.drawable.bg_image_placeholder)
                .error(R.drawable.bg_image_placeholder)
                .centerCrop()
                .into(imgResep)

            // itemView = root layout item_resep.xml. Klik card → buka detail.
            itemView.setOnClickListener { onItemClick(resep) }

            btnFavorit.apply {
                setImageResource(
                    if (isFavorit) R.drawable.ic_menu_favorit
                    else R.drawable.ic_menu_favorit_outline
                )
                imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        if (isFavorit) R.color.red_600 else R.color.stone_900
                    )
                )
                setOnClickListener { onFavoriteClick(resep) }
            }
        }
    }
}

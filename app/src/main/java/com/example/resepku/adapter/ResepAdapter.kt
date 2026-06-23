package com.example.resepku.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.resepku.R
import com.example.resepku.data.remote.Resep
import com.example.resepku.databinding.ItemResepBinding
import com.google.android.material.color.MaterialColors

// Adapter RecyclerView buat nampilin daftar resep (Home / Search / Favorite).
// Fragment yg panggil & tentuin aksi lewat 2 callback: klik kartu (buka detail)
// & klik tombol favorit (toggle favorite).
class ResepAdapter(
    private val onItemClick: (Resep) -> Unit,
    private val onFavoriteClick: (Resep) -> Unit
) : RecyclerView.Adapter<ResepAdapter.ResepViewHolder>() {

    // Data internal adapter. Pakai List biasa + notifyDataSetChanged() biar simpel
    // (DiffUtil overkill buat scope UAS).
    private val daftarResep = mutableListOf<Resep>()

    // Set ID resep yang sudah difavoritkan. Dipakai buat tint tombol favorit
    // (merah kalau favorit, abu-abu kalau belum). Disuplai dari Fragment
    // via submitList(...) setelah baca dari SQLite.
    private var favoriteIds: Set<String> = emptySet()

    // Ganti isi daftar & (opsional) update set favorit, lalu render ulang.
    fun submitList(daftarBaru: List<Resep>, favoriteIds: Set<String> = this.favoriteIds) {
        this.daftarResep.clear()
        this.daftarResep.addAll(daftarBaru)
        this.favoriteIds = favoriteIds
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResepViewHolder {
        val binding = ItemResepBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ResepViewHolder(binding, onItemClick, onFavoriteClick)
    }

    override fun getItemCount(): Int = daftarResep.size

    override fun onBindViewHolder(holder: ResepViewHolder, position: Int) {
        val resep = daftarResep[position]
        holder.bind(resep, isFavorit = resep.id in favoriteIds)
    }

    // ViewHolder = pembungkus 1 item view. Re-use oleh RecyclerView saat scroll.
    class ResepViewHolder(
        private val binding: ItemResepBinding,
        private val onItemClick: (Resep) -> Unit,
        private val onFavoriteClick: (Resep) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(resep: Resep, isFavorit: Boolean) {
            // Teks langsung dari objek Resep (data, bukan hardcode).
            binding.tvNama.text = resep.nama
            binding.chipKategori.text = resep.kategori

            // Glide: download gambar dari URL, kasih placeholder + error fallback.
            Glide.with(binding.imgResep)
                .load(resep.gambar)
                .placeholder(R.drawable.bg_image_placeholder)
                .error(R.drawable.bg_image_placeholder)
                .centerCrop()
                .into(binding.imgResep)

            // Content description dinamis: "Gambar [nama resep]" buat screen reader.
            binding.imgResep.contentDescription = binding.root.context.getString(
                R.string.desc_gambar_resep_format,
                resep.nama
            )

            // Klik kartu -> delegasi ke callback Fragment (buka Detail).
            binding.root.setOnClickListener { onItemClick(resep) }

            // Tombol favorit: ikon filled kalau sudah favorit, outline kalau belum.
            binding.btnFavorit.setImageResource(
                if (isFavorit) R.drawable.ic_menu_favorit
                else R.drawable.ic_menu_favorit_outline
            )

            // Tombol favorit: tint merah kalau sudah favorit, abu-abu kalau belum.
            val attrWarna = if (isFavorit) {
                com.google.android.material.R.attr.colorError
            } else {
                com.google.android.material.R.attr.colorOnSurfaceVariant
            }
            binding.btnFavorit.imageTintList = ColorStateList.valueOf(
                MaterialColors.getColor(binding.btnFavorit, attrWarna)
            )
            binding.btnFavorit.contentDescription = binding.root.context.getString(
                if (isFavorit) R.string.action_favorite_remove
                else R.string.action_favorite_add
            )
            binding.btnFavorit.setOnClickListener { onFavoriteClick(resep) }
        }
    }
}
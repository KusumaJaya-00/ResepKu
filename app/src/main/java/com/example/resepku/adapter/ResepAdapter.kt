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

class ResepAdapter(
    private val onItemClick: (Resep) -> Unit,
    private val onFavoriteClick: (Resep) -> Unit
) : RecyclerView.Adapter<ResepAdapter.ResepViewHolder>() {

    private val daftarResep = mutableListOf<Resep>()
    private var favoriteIds: Set<String> = emptySet()

    fun submitList(daftarBaru: List<Resep>, favoriteIds: Set<String> = this.favoriteIds) {
        this.daftarResep.clear()
        this.daftarResep.addAll(daftarBaru)
        this.favoriteIds = favoriteIds
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResepViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_resep, parent, false)
        return ResepViewHolder(view, onItemClick, onFavoriteClick)
    }

    override fun getItemCount(): Int = daftarResep.size

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

        fun bind(resep: Resep, isFavorit: Boolean) {
            tvNama.text = resep.nama
            chipKategori.text = resep.kategori

            Glide.with(imgResep)
                .load(resep.gambar)
                .placeholder(R.drawable.bg_image_placeholder)
                .error(R.drawable.bg_image_placeholder)
                .centerCrop()
                .into(imgResep)

            imgResep.contentDescription = itemView.context.getString(
                R.string.desc_gambar_resep_format,
                resep.nama
            )

            itemView.setOnClickListener { onItemClick(resep) }

            btnFavorit.setImageResource(
                if (isFavorit) R.drawable.ic_menu_favorit
                else R.drawable.ic_menu_favorit_outline
            )

            val warnaFavorit = if (isFavorit) {
                R.color.red_600
            } else {
                R.color.stone_900
            }
            btnFavorit.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(btnFavorit.context, warnaFavorit)
            )
            btnFavorit.contentDescription = itemView.context.getString(
                if (isFavorit) R.string.action_favorite_remove
                else R.string.action_favorite_add
            )
            btnFavorit.setOnClickListener { onFavoriteClick(resep) }
        }
    }
}

package com.example.resepku.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.resepku.R
import com.example.resepku.data.remote.Resep
import com.example.resepku.databinding.ItemResepBinding

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

    class ResepViewHolder(
        private val binding: ItemResepBinding,
        private val onItemClick: (Resep) -> Unit,
        private val onFavoriteClick: (Resep) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(resep: Resep, isFavorit: Boolean) {
            binding.tvNama.text = resep.nama
            binding.chipKategori.text = resep.kategori

            Glide.with(binding.imgResep)
                .load(resep.gambar)
                .placeholder(R.drawable.bg_image_placeholder)
                .error(R.drawable.bg_image_placeholder)
                .centerCrop()
                .into(binding.imgResep)

            binding.imgResep.contentDescription = binding.root.context.getString(
                R.string.desc_gambar_resep_format,
                resep.nama
            )

            binding.root.setOnClickListener { onItemClick(resep) }

            binding.btnFavorit.setImageResource(
                if (isFavorit) R.drawable.ic_menu_favorit
                else R.drawable.ic_menu_favorit_outline
            )

            val warnaFavorit = if (isFavorit) {
                R.color.red_600
            } else {
                R.color.stone_900
            }
            binding.btnFavorit.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(binding.btnFavorit.context, warnaFavorit)
            )
            binding.btnFavorit.contentDescription = binding.root.context.getString(
                if (isFavorit) R.string.action_favorite_remove
                else R.string.action_favorite_add
            )
            binding.btnFavorit.setOnClickListener { onFavoriteClick(resep) }
        }
    }
}

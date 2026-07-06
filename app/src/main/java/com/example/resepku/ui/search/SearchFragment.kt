package com.example.resepku.ui.search

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resepku.R
import com.example.resepku.adapter.ResepAdapter
import com.example.resepku.data.local.DatabaseHelper
import com.example.resepku.data.remote.Resep
import com.example.resepku.ui.detail.DetailActivity
import com.example.resepku.viewmodel.SearchViewModel

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var etSearch: EditText
    private lateinit var llKategori: LinearLayout
    private lateinit var rvHasil: RecyclerView
    private lateinit var pbLoading: ProgressBar
    private lateinit var tvPesan: TextView

    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: ResepAdapter
    private lateinit var dbHelper: DatabaseHelper

    private var kategoriTerpilih: String = SearchViewModel.KATEGORI_SEMUA
    private val daftarChip = mutableListOf<TextView>()

    override fun onViewCreated(view: View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etSearch = view.findViewById(R.id.etSearch)
        llKategori = view.findViewById(R.id.llKategori)
        rvHasil = view.findViewById(R.id.rvHasil)
        pbLoading = view.findViewById(R.id.pbLoading)
        tvPesan = view.findViewById(R.id.tvPesan)

        rvHasil.layoutManager = LinearLayoutManager(requireContext())

        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        dbHelper = DatabaseHelper(requireContext())

        adapter = ResepAdapter(
            onItemClick = { resep -> bukaDetail(resep) },
            onFavoriteClick = { resep -> toggleFavorit(resep) }
        )
        rvHasil.adapter = adapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                tampilkanHasil()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        muatDataAwal()
    }

    private fun muatDataAwal() {
        pbLoading.visibility = View.VISIBLE
        tvPesan.visibility = View.GONE
        rvHasil.visibility = View.GONE

        viewModel.ambilSemuaResep(
            onBerhasil = {
                pbLoading.visibility = View.GONE
                buatChipKategori()
                tampilkanHasil()
            },
            onGagal = {
                pbLoading.visibility = View.GONE
                tvPesan.text = getString(R.string.msg_load_failed)
                tvPesan.visibility = View.VISIBLE
            }
        )
    }

    private fun buatChipKategori() {
        llKategori.removeAllViews()
        daftarChip.clear()

        viewModel.getDaftarKategori().forEach { kategori ->
            val chip = TextView(requireContext()).apply {
                text = kategori
                textSize = 12f
                setPadding(dp(16), dp(8), dp(16), dp(8))
                setOnClickListener {
                    kategoriTerpilih = kategori
                    perbaruiTampilanChip()
                    tampilkanHasil()
                }
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginEnd = dp(8)
            llKategori.addView(chip, params)
            daftarChip.add(chip)
        }
        perbaruiTampilanChip()
    }

    private fun perbaruiTampilanChip() {
        daftarChip.forEach { chip ->
            val aktif = chip.text == kategoriTerpilih
            chip.setBackgroundResource(
                if (aktif) R.drawable.bg_chip_filled else R.drawable.bg_chip_outline
            )
            chip.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (aktif) R.color.white else R.color.orange_600
                )
            )
        }
    }

    private fun tampilkanHasil() {
        val keyword = etSearch.text.toString()
        val hasil = viewModel.cariResep(keyword, kategoriTerpilih)

        if (hasil.isEmpty()) {
            rvHasil.visibility = View.GONE
            tvPesan.text = getString(R.string.msg_empty_search)
            tvPesan.visibility = View.VISIBLE
        } else {
            tvPesan.visibility = View.GONE
            rvHasil.visibility = View.VISIBLE
            val favoriteIds = hasil.filter { dbHelper.isFavorit(it.id) }.map { it.id }.toSet()
            adapter.submitList(hasil, favoriteIds)
        }
    }

    private fun bukaDetail(resep: Resep) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra("resep", resep)
        startActivity(intent)
    }

    private fun toggleFavorit(resep: Resep) {
        if (dbHelper.isFavorit(resep.id)) {
            dbHelper.hapusFavorit(resep.id)
        } else {
            dbHelper.tambahFavorit(resep)
        }
        tampilkanHasil()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
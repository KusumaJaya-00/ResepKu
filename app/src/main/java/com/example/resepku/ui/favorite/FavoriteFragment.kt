package com.example.resepku.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.resepku.R
import com.example.resepku.adapter.ResepAdapter
import com.example.resepku.data.local.DatabaseHelper
import com.example.resepku.ui.detail.DetailActivity
import com.example.resepku.viewmodel.FavoriteViewModel

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ResepAdapter

    private lateinit var rvFavorit: RecyclerView
    private lateinit var pbLoading: ProgressBar
    private lateinit var tvPesan: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menghubungkan variabel dengan ID di XML menggunakan findViewById
        rvFavorit = view.findViewById(R.id.rvFavorit)
        pbLoading = view.findViewById(R.id.pbLoading)
        tvPesan = view.findViewById(R.id.tvPesan)

        // Inisialisasi ViewModel dan DatabaseHelper
        viewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]
        dbHelper = DatabaseHelper(requireContext())

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = ResepAdapter(
            onItemClick = { resep ->
                // Navigasi manual ke halaman Detail
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra("resep", resep)
                startActivity(intent)
            },
            onFavoriteClick = { resep ->
                // Aksi hapus dari favorit saat ikon hati di klik
                viewModel.hapusDariFavorit(dbHelper, resep.id) { berhasil ->
                    if (berhasil) {
                        muatDataFavorit() // Muat ulang list
                    }
                }
            }
        )
        rvFavorit.adapter = adapter
    }

    private fun muatDataFavorit() {
        // Tampilkan loading sebelum mengambil data dari SQLite
        pbLoading.visibility = View.VISIBLE
        tvPesan.visibility = View.GONE

        viewModel.muatFavorit(dbHelper) { list ->
            pbLoading.visibility = View.GONE
            
            if (list.isEmpty()) {
                // Tampilkan pesan jika tidak ada data favorit
                tvPesan.visibility = View.VISIBLE
                adapter.submitList(emptyList(), emptySet())
            } else {
                tvPesan.visibility = View.GONE
                // Ambil set ID favorit untuk menentukan status ikon hati di adapter
                val favoriteIds = list.map { it.id }.toSet()
                adapter.submitList(list, favoriteIds)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Muat data setiap kali user masuk ke fragment ini
        muatDataFavorit()
    }
}

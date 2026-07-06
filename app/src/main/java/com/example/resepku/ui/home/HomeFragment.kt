package com.example.resepku.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.resepku.R
import com.example.resepku.adapter.ResepAdapter
import com.example.resepku.data.local.DatabaseHelper
import com.example.resepku.data.remote.Resep
import com.example.resepku.ui.detail.DetailActivity
import com.example.resepku.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private lateinit var rvResep: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvPesan: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: ResepAdapter
    private lateinit var databaseHelper: DatabaseHelper
    
    // Simpan list resep agar bisa di-refresh status favoritnya tanpa panggil API lagi
    private var listResepSekarang: List<Resep> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi View pakai findViewById
        rvResep = view.findViewById(R.id.rvResep)
        progressBar = view.findViewById(R.id.progressBar)
        tvPesan = view.findViewById(R.id.tvPesan)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        
        // Inisialisasi DatabaseHelper
        databaseHelper = DatabaseHelper(requireContext())

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Konfigurasi Adapter (Sesuaikan dengan fitur teman sekelompok)
        adapter = ResepAdapter(
            onItemClick = { resep ->
                // Pindah ke DetailActivity dengan membawa objek resep (Serializable)
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra("resep", resep)
                startActivity(intent)
            },
            onFavoriteClick = { resep ->
                // Toggle status favorit di SQLite
                if (databaseHelper.isFavorit(resep.id)) {
                    databaseHelper.hapusFavorit(resep.id)
                } else {
                    databaseHelper.tambahFavorit(resep)
                }
                
                // Refresh ikon hati di list
                val favIds = databaseHelper.ambilSemuaFavorit().map { it.id }.toSet()
                adapter.submitList(listResepSekarang, favIds)
            }
        )
        rvResep.adapter = adapter

        swipeRefresh.setOnRefreshListener {
            tampilkanResep()
        }

        tampilkanResep()
    }

    private fun tampilkanResep() {
        if (!swipeRefresh.isRefreshing) {
            progressBar.visibility = View.VISIBLE
        }
        
        tvPesan.visibility = View.GONE

        viewModel.ambilResep(
            onBerhasil = { list ->
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
                
                listResepSekarang = list
                
                // Ambil daftar ID yang sudah difavoritkan
                val favIds = databaseHelper.ambilSemuaFavorit().map { it.id }.toSet()
                
                if (list.isEmpty()) {
                    tvPesan.visibility = View.VISIBLE
                    tvPesan.text = getString(R.string.msg_empty_recipe)
                    rvResep.visibility = View.GONE
                } else {
                    rvResep.visibility = View.VISIBLE
                    adapter.submitList(list, favIds)
                }
            },
            onGagal = {
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
                tvPesan.visibility = View.VISIBLE
                tvPesan.text = getString(R.string.msg_load_failed)
                rvResep.visibility = View.GONE
            }
        )
    }
}

package com.example.resepku.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.resepku.R
import com.example.resepku.adapter.ResepAdapter
import com.example.resepku.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private lateinit var rvResep: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvPesan: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: ResepAdapter

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

        rvResep.layoutManager = LinearLayoutManager(requireContext())

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        adapter = ResepAdapter(
            onItemClick = { resep -> },
            onFavoriteClick = { resep -> }
        )
        rvResep.adapter = adapter

        // Logika Swipe to Refresh
        swipeRefresh.setOnRefreshListener {
            tampilkanResep()
        }

        tampilkanResep()
    }

    private fun tampilkanResep() {
        // Tampilkan loading jika dipanggil bukan dari swipe refresh
        if (!swipeRefresh.isRefreshing) {
            progressBar.visibility = View.VISIBLE
        }
        
        tvPesan.visibility = View.GONE

        viewModel.ambilResep(
            onBerhasil = { list ->
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false // matikan putaran refresh
                
                if (list.isEmpty()) {
                    tvPesan.visibility = View.VISIBLE
                    tvPesan.text = getString(R.string.msg_empty_recipe)
                    rvResep.visibility = View.GONE
                } else {
                    rvResep.visibility = View.VISIBLE
                    adapter.submitList(list)
                }
            },
            onGagal = {
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false // matikan putaran refresh
                tvPesan.visibility = View.VISIBLE
                tvPesan.text = getString(R.string.msg_load_failed)
                rvResep.visibility = View.GONE
            }
        )
    }
}

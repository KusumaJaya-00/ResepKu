package com.example.resepku.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.resepku.R
import com.example.resepku.adapter.ResepAdapter
import com.example.resepku.databinding.FragmentHomeBinding
import com.example.resepku.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: ResepAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Inisialisasi Adapter (menggunakan ResepAdapter yang sudah ada)
        adapter = ResepAdapter(
            onItemClick = { resep ->
                // TODO: Implementasi Intent ke DetailActivity
            },
            onFavoriteClick = { resep ->
                // TODO: Implementasi klik favorit (SQLite)
            }
        )

        binding.rvResep.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        binding.rvResep.adapter = adapter

        // Ambil data pertama kali
        tampilkanResep()
    }

    private fun tampilkanResep() {
        // Mulai Loading
        binding.progressBar.visibility = View.VISIBLE
        binding.rvResep.visibility = View.GONE
        binding.tvPesan.visibility = View.GONE

        viewModel.ambilResep(
            onBerhasil = { list ->
                binding.progressBar.visibility = View.GONE
                
                if (list.isEmpty()) {
                    // Tampilkan pesan kosong dari strings.xml
                    binding.tvPesan.visibility = View.VISIBLE
                    binding.tvPesan.text = getString(R.string.msg_empty_recipe)
                } else {
                    // Tampilkan list resep
                    binding.rvResep.visibility = View.VISIBLE
                    adapter.submitList(list)
                }
            },
            onGagal = {
                // Tampilkan pesan error dari strings.xml
                binding.progressBar.visibility = View.GONE
                binding.tvPesan.visibility = View.VISIBLE
                binding.tvPesan.text = getString(R.string.msg_load_failed)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hapus binding untuk mencegah memory leak
        _binding = null
    }
}

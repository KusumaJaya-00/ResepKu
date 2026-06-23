package com.example.resepku

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.resepku.databinding.ActivityMainBinding
import com.example.resepku.ui.favorite.FavoriteFragment
import com.example.resepku.ui.home.HomeFragment
import com.example.resepku.ui.search.SearchFragment
import com.google.android.material.color.MaterialColors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Klik tab = ganti Fragment + pindahkan gaya aktif ke tab tsb.
        binding.tabHome.setOnClickListener {
            tampilkanFragment(HomeFragment())
            aturTabAktif(TAB_HOME)
        }
        binding.tabSearch.setOnClickListener {
            tampilkanFragment(SearchFragment())
            aturTabAktif(TAB_SEARCH)
        }
        binding.tabFavorit.setOnClickListener {
            tampilkanFragment(FavoriteFragment())
            aturTabAktif(TAB_FAVORIT)
        }

        if (savedInstanceState == null) {
            tampilkanFragment(HomeFragment())
            aturTabAktif(TAB_HOME)
        }
    }

    // Mengganti isi container sesuai menu yang dipilih user.
    private fun tampilkanFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private companion object {
        const val TAB_HOME = 0
        const val TAB_SEARCH = 1
        const val TAB_FAVORIT = 2
    }

    // Pindahkan pill + warna "aktif" ke tab yang dipilih, tab lain di-reset.
    private fun aturTabAktif(tab: Int) {
        nonaktifkan(binding.tabHome, binding.imgTabHome, binding.tvTabHome)
        nonaktifkan(binding.tabSearch, binding.imgTabSearch, binding.tvTabSearch)
        nonaktifkan(binding.tabFavorit, binding.imgTabFavorit, binding.tvTabFavorit)

        when (tab) {
            TAB_HOME -> aktifkan(binding.tabHome, binding.imgTabHome, binding.tvTabHome)
            TAB_SEARCH -> aktifkan(binding.tabSearch, binding.imgTabSearch, binding.tvTabSearch)
            TAB_FAVORIT -> aktifkan(binding.tabFavorit, binding.imgTabFavorit, binding.tvTabFavorit)
        }
    }

    // Pasang pill background + warna icon/teks versi "aktif".
    private fun aktifkan(tab: LinearLayout, img: ImageView, tv: TextView) {
        tab.setBackgroundResource(R.drawable.bg_tab_active)
        val warnaAktif = MaterialColors.getColor(
            tab,
            com.google.android.material.R.attr.colorOnSecondaryContainer
        )
        img.setColorFilter(warnaAktif)
        tv.setTextColor(warnaAktif)
    }

    // Balikin ke transparan + warna icon/teks versi "non-aktif".
    private fun nonaktifkan(tab: LinearLayout, img: ImageView, tv: TextView) {
        tab.setBackgroundColor(Color.TRANSPARENT)
        val warnaNonaktif = MaterialColors.getColor(
            tab,
            com.google.android.material.R.attr.colorOnSurfaceVariant
        )
        img.setColorFilter(warnaNonaktif)
        tv.setTextColor(warnaNonaktif)
    }
}
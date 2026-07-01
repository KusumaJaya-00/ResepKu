package com.example.resepku

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.resepku.databinding.ActivityMainBinding
import com.example.resepku.ui.favorite.FavoriteFragment
import com.example.resepku.ui.home.HomeFragment
import com.example.resepku.ui.search.SearchFragment

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

    private fun aktifkan(tab: LinearLayout, img: ImageView, tv: TextView) {
        tab.setBackgroundResource(R.drawable.bg_tab_active)
        if (tab.id == R.id.tabFavorit) {
            img.setImageResource(R.drawable.ic_menu_favorit)
        }
        val warnaAktif = ContextCompat.getColor(tab.context, R.color.orange_600)
        img.setColorFilter(warnaAktif)
        tv.setTextColor(warnaAktif)
    }

    private fun nonaktifkan(tab: LinearLayout, img: ImageView, tv: TextView) {
        tab.setBackgroundColor(ContextCompat.getColor(tab.context, R.color.transparent))
        if (tab.id == R.id.tabFavorit) {
            img.setImageResource(R.drawable.ic_menu_favorit_outline)
        }
        val warnaNonaktif = ContextCompat.getColor(tab.context, R.color.stone_900)
        img.setColorFilter(warnaNonaktif)
        tv.setTextColor(warnaNonaktif)
    }
}

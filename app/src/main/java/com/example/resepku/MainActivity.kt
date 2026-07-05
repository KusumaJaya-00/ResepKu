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
import com.example.resepku.ui.favorite.FavoriteFragment
import com.example.resepku.ui.home.HomeFragment
import com.example.resepku.ui.search.SearchFragment

class MainActivity : AppCompatActivity() {
    private lateinit var tabHome: LinearLayout
    private lateinit var tabSearch: LinearLayout
    private lateinit var tabFavorit: LinearLayout
    private lateinit var imgTabHome: ImageView
    private lateinit var imgTabSearch: ImageView
    private lateinit var imgTabFavorit: ImageView
    private lateinit var tvTabHome: TextView
    private lateinit var tvTabSearch: TextView
    private lateinit var tvTabFavorit: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        tabHome = findViewById(R.id.tabHome)
        tabSearch = findViewById(R.id.tabSearch)
        tabFavorit = findViewById(R.id.tabFavorit)
        imgTabHome = findViewById(R.id.imgTabHome)
        imgTabSearch = findViewById(R.id.imgTabSearch)
        imgTabFavorit = findViewById(R.id.imgTabFavorit)
        tvTabHome = findViewById(R.id.tvTabHome)
        tvTabSearch = findViewById(R.id.tvTabSearch)
        tvTabFavorit = findViewById(R.id.tvTabFavorit)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tabHome.setOnClickListener {
            tampilkanFragment(HomeFragment())
            aturTabAktif(TAB_HOME)
        }
        tabSearch.setOnClickListener {
            tampilkanFragment(SearchFragment())
            aturTabAktif(TAB_SEARCH)
        }
        tabFavorit.setOnClickListener {
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
        nonaktifkan(tabHome, imgTabHome, tvTabHome)
        nonaktifkan(tabSearch, imgTabSearch, tvTabSearch)
        nonaktifkan(tabFavorit, imgTabFavorit, tvTabFavorit)

        when (tab) {
            TAB_HOME -> aktifkan(tabHome, imgTabHome, tvTabHome)
            TAB_SEARCH -> aktifkan(tabSearch, imgTabSearch, tvTabSearch)
            TAB_FAVORIT -> aktifkan(tabFavorit, imgTabFavorit, tvTabFavorit)
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

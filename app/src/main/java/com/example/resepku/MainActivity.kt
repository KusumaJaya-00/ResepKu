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

// Single activity dengan 3 tab (Home, Search, Favorite).
// Setiap tab me-replace fragment dan mengubah gaya visual tab.
class MainActivity : AppCompatActivity() {
    private companion object {
        const val TAB_HOME = 0
        const val TAB_SEARCH = 1
        const val TAB_FAVORIT = 2
    }

    private lateinit var tabHome: LinearLayout
    private lateinit var tabSearch: LinearLayout
    private lateinit var tabFavorit: LinearLayout
    private lateinit var imgTabHome: ImageView
    private lateinit var imgTabSearch: ImageView
    private lateinit var imgTabFavorit: ImageView
    private lateinit var tvTabHome: TextView
    private lateinit var tvTabSearch: TextView
    private lateinit var tvTabFavorit: TextView
    private lateinit var mainContainer: androidx.constraintlayout.widget.ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // enableEdgeToEdge(): layout tembus sampai pojok layar.
        // Listener di bawah beri padding agar konten tidak tertutup status bar & nav bar.
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
        mainContainer = findViewById(R.id.main)

        // insets.getInsets() = ambil ukuran status bar & nav bar, lalu set padding agar konten tidak tertutup.
        ViewCompat.setOnApplyWindowInsetsListener(mainContainer) { view, insets ->
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

        // savedInstanceState == null = pertama kali dibuat (bukan rotasi).
        if (savedInstanceState == null) {
            tampilkanFragment(HomeFragment())
            aturTabAktif(TAB_HOME)
        }
    }

    // Pakai replace() bukan add() agar fragment lama dihapus, tidak menumpuk.
    private fun tampilkanFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // Nonaktifkan semua tab dulu, baru aktifkan tab yang dipilih.
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

    // Tab aktif: bg orange, teks/ikon orange. Favorit: ganti icon outline → filled.
    private fun aktifkan(tab: LinearLayout, img: ImageView, tv: TextView) {
        tab.setBackgroundResource(R.drawable.bg_tab_active)
        if (tab.id == R.id.tabFavorit) {
            img.setImageResource(R.drawable.ic_menu_favorit)
        }
        val warnaAktif = ContextCompat.getColor(tab.context, R.color.orange_600)
        img.setColorFilter(warnaAktif)
        tv.setTextColor(warnaAktif)
    }

    // Tab nonaktif: bg transparan, teks/ikon abu. Favorit: ganti icon filled → outline.
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

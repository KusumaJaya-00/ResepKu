# AGENTS.md — ResepKu (Aplikasi Resep Masakan)

## 1. Tentang Project
ResepKu adalah aplikasi Android (Kotlin) untuk menjelajahi resep masakan
BERBAHASA INDONESIA. Data resep diambil dari satu file JSON statis (resep.json)
yang di-host di GitHub (diakses lewat URL Raw), dan resep favorit disimpan
secara lokal di HP user (full-offline).

Ini adalah project UAS Mobile Programming, dikerjakan tim 5 orang.
TUJUAN UTAMA: aplikasi yang BERFUNGSI, UI menarik, dan yang paling penting
KODE HARUS MUDAH DIJELASKAN saat ujian lisan. Utamakan kesederhanaan.

## 2. Tech Stack (WAJIB diikuti)
- Bahasa: Kotlin
- UI: XML Layout (View system) + ViewBinding. TANPA Material Components -
  semua komponen (kartu, chip, tombol, bottom nav, input) dibuat manual dari view
  standar + background drawable (shape XML) biar bebas diatur
- Arsitektur: MVVM (Model - View - ViewModel) - TANPA Repository
- Networking: Retrofit + Gson converter
- Load gambar: Glide
- Database lokal: SQLite via SQLiteOpenHelper (tulis SQL manual); favorit disimpan
  full-offline sebagai objek Resep dalam bentuk JSON (Gson)
- Async: Retrofit Callback (enqueue) - TANPA Coroutines
- Navigasi: bottom nav CUSTOM (LinearLayout berisi item yang bisa diklik) + ganti
  Fragment manual (FragmentManager); pindah ke Detail pakai Intent ke DetailActivity
  (TANPA Navigation Component)
- minSdk 24

## 3. Setup Awal (tanggung jawab TEAM LEAD - biasanya SUDAH selesai)
Bagian ini dikerjakan Team Lead SEKALI di awal lalu di-push ke GitHub.
Anggota lain tinggal pull dan TIDAK perlu mengubahnya. Ditulis di sini sebagai
REFERENSI biar AI memakai versi yang sama & tidak menyuruh menambah ulang.
- minSdk 24, targetSdk 34, compileSdk 34.
- ViewBinding sudah diaktifkan: buildFeatures { viewBinding = true }.
- Izin INTERNET sudah ada di AndroidManifest.xml:
  <uses-permission android:name="android.permission.INTERNET" />
- Dependency utama (jangan ganti versinya tanpa koordinasi Lead):
  - Retrofit 2.9.0 + converter-gson 2.9.0
  - Glide 4.16.0
  - RecyclerView 1.3.2
  - lifecycle-viewmodel-ktx 2.7.0
  - SQLite: TANPA library tambahan (bawaan Android).
  (Navigation Component & kotlinx-coroutines TIDAK dipakai - navigasi pakai
   FragmentManager + Intent biasa, async pakai Retrofit enqueue).
- Kalau butuh library di luar daftar ini, koordinasi dengan Team Lead dulu.

## 4. LARANGAN (jangan pernah dilanggar)
- JANGAN pakai Jetpack Compose. Semua UI pakai XML + ViewBinding.
- JANGAN pakai Room, @Dao, atau @Entity. Database pakai SQLiteOpenHelper + SQL manual.
- JANGAN pakai findViewById berulang; pakai ViewBinding.
- JANGAN pakai library tambahan di luar daftar stack tanpa izin tim.
- JANGAN pakai Coroutines, Flow, atau RxJava untuk async; cukup Retrofit enqueue.
- JANGAN pakai framework rumit (Hilt/Dagger) yang tim belum paham.
- JANGAN pakai Material Components (MaterialCardView, BottomNavigationView, Chip,
  TextInputLayout, MaterialButton, dll) - kurang customizable. Semua komponen dibuat
  MANUAL dari view standar (LinearLayout/ConstraintLayout/TextView/ImageView/EditText/
  Button) + background drawable (shape XML).
  WAJIB PERBAIKI kalau menemukan kode/layout yang MASIH pakai Material - tanda-tandanya:
  tag <com.google.android.material...> di XML, class MaterialButton/MaterialCardView/Chip/
  TextInputLayout/BottomNavigationView di Kotlin, parent tema Theme.Material3/Theme.MaterialComponents,
  atribut app:* khas Material (app:cardCornerRadius, app:chipBackgroundColor, dll), atau
  dependency com.google.android.material di build.gradle.kts. Ganti ke versi manual
  (view standar + background drawable shape) TANPA diminta, lalu jelaskan singkat apa yang diubah.
  (Catatan: custom theme color via Theme.AppCompat.DayNight TETAP boleh - itu bukan Material.)
- JANGAN hardcode teks di kode Kotlin; taruh di res/values/strings.xml.
- JANGAN bikin solusi "paling canggih"; pilih yang paling sederhana & bisa dijelaskan.

## 5. Struktur Folder
com.kelompok.resepku/
  data/remote/      -> Retrofit ApiService + response model
  data/local/       -> SQLite DatabaseHelper (SQLiteOpenHelper)
  ui/home/          -> HomeFragment (daftar resep)
  ui/detail/        -> DetailActivity (halaman detail, dibuka via Intent)
  ui/search/        -> SearchFragment
  ui/favorite/      -> FavoriteFragment
  adapter/          -> RecyclerView adapter
  viewmodel/        -> ViewModel per fitur
  MainActivity.kt   -> host bottom nav custom (LinearLayout) + ganti 3 Fragment (FragmentManager)

## 6. Konteks Data (JSON statis di GitHub)
- Sumber data: SATU file resep.json yang di-host di GitHub (public), diakses lewat URL Raw.
- Base URL (sampai sebelum nama file):
  https://raw.githubusercontent.com/USERNAME/resepku-data/main/
- GET resep.json  -> mengembalikan SEMUA resep (List<Resep>) sekaligus.
- Tidak ada endpoint detail/search di server. Detail & pencarian diproses di app:
  - Detail: kirim objek Resep yang sudah dimuat lewat Intent (tanpa request lagi).
  - Search & filter kategori: saring list di sisi app pakai filter Kotlin (client-side).
- Data berbahasa Indonesia, read-only, gratis, tanpa API key.
- Tiap item resep.json: { id, nama, gambar, kategori, deskripsi, bahan[], langkah[] }.

## 7. Contoh Data Model (disepakati bersama - JANGAN ubah sendiri)
// Nama field HARUS sama persis dengan key di resep.json (kita yang bikin),
// jadi tidak perlu @SerializedName. Semua screen WAJIB pakai model ini.

// SATU model untuk semua: daftar (Home/Search) + detail.
// Serializable supaya objeknya bisa dikirim lewat Intent ke DetailActivity.
data class Resep(
    val id: String,
    val nama: String,
    val gambar: String,        // URL gambar
    val kategori: String,
    val deskripsi: String,
    val bahan: List<String>,
    val langkah: List<String>
) : java.io.Serializable

// Favorit TIDAK pakai model terpisah. Tabel favorit hanya 2 kolom:
//   id TEXT (primary key) + data TEXT (objek Resep dalam bentuk JSON).
// Simpan: gson.toJson(resep) ; Baca: gson.fromJson(data, Resep::class.java).
// Jadi favorit tersimpan UTUH & bisa dibuka offline.

## 8. Aturan Penulisan Kode (clean & konsisten)
- Penamaan: camelCase (variabel/fungsi), PascalCase (class), snake_case (file XML).
- Nama id view di XML pakai AWALAN TIPE SINGKAT:
  tv = TextView, btn = Button, img = ImageView, et = EditText,
  rv = RecyclerView, pb = ProgressBar, ll = LinearLayout (wadah/kartu).
  Contoh: tvJudul, btnFavorit, imgResep, rvResep, etCari, llKartu.
- Pola penamaan 2 lapis: id di XML pakai awalan tipe (tvJudul); saat di-assign
  ke variabel Kotlin boleh LEBIH SINGKAT tanpa awalan
  (mis. val judul = binding.tvJudul), ASAL tetap jelas.
  JANGAN sampai 1 huruf (val j) — harus tetap kebaca.
- Nama file XML: fragment_<fitur>.xml, activity_<nama>.xml, item_<list>.xml
  (mis. fragment_home.xml, item_resep.xml).
- 1 file = 1 tanggung jawab. Fungsi pendek; pecah kalau kepanjangan.
- Nama variabel deskriptif (resepList, bukan x atau data1).
- Komentar BAHASA INDONESIA: JANGAN di tiap baris. Cukup di bagian penting
  atau sebagai penjelasan umum sebuah blok/fungsi. Buat se-RINGKAS mungkin
  & mudah dipahami.
- Setiap pemanggilan API wajib ada penanganan loading & error state.
- Operasi jaringan pakai Retrofit enqueue (otomatis jalan di background).
  Operasi SQLite favorit datanya kecil, boleh dipanggil langsung.
- Ikuti pola MVVM: UI (Fragment) -> ViewModel -> (API / SQLite). TANPA Repository.

## 9. Ketentuan XML & Desain UI
Layout:
- Root layout pakai ConstraintLayout (atau LinearLayout untuk layar sederhana).
  Hindari nesting layout terlalu dalam (maksimal 2-3 tingkat).
- Item daftar dibuat di file XML terpisah (mis. item_resep.xml) untuk RecyclerView.
- TANPA Material Components. Komponen dibuat manual: kartu = LinearLayout + background
  drawable (shape rounded), tombol = Button + background drawable, chip kategori =
  TextView + background drawable (pill), input = EditText + background drawable.
  Bentuk membulat/stroke diatur di file res/drawable/*.xml.

Konsistensi desain (UI ikut dinilai, jadi buat rapi & seragam):

DESIGN SYSTEM - ambil angka dari SKALA, jangan asal (base Android = 4dp):
- Skala spasi (dp) untuk SEMUA padding/margin/jarak, pilih sesuai kebutuhan:
  2, 4, 8, 12, 16, 24, 32, 48, 56, 64. DILARANG angka di luar skala (7, 10, 13, 18, 20-an acak).
  Default: padding tepi layar = 16dp, jarak antar kartu/item = 8dp, antar section = 24dp.
- Type scale (sp) untuk ukuran teks: 12, 14, 16, 18, 20, 22, 24, 28, 34.
  Body = 16sp, subjudul = 18sp, judul kartu = 20-22sp, judul Detail = 24sp.
- Tulis nilainya MANUAL langsung di XML (TANPA dimens.xml), asalkan selalu dari skala di atas.
- Corner radius kartu = 12dp; elevation kartu = 1-2dp. Gambar: scaleType=centerCrop + tinggi tetap.

WARNA - taruh semua di colors.xml, pakai LANGSUNG via @color/... (1 sumber kebenaran):
- colors.xml = palette app (orange_600, orange_400, white, black, gray_100, gray_300, red_600).
- Di layout/kode pakai @color/... (mis. android:textColor="@color/black",
  android:background="@color/white"). DILARANG hardcode hex (#EA580C) langsung di view.
- themes.xml: style Theme.ResepKu parent Theme.AppCompat.DayNight.NoActionBar (BUKAN Material).
- Mau ganti warna app? Ubah hex di colors.xml saja (1 tempat), semua layar ikut berubah.
- Teks tetap di strings.xml; jangan hardcode teks di layout.
- Gambar resep: ImageView + Glide, selalu beri placeholder & gambar error.

Saat AI generate layout XML: ikuti skala + token di atas, tiru gaya item_resep.xml yang sudah ada,
dan SETELAH selesai cek ulang tiap nilai dp/sp/warna - kalau ada yang di luar skala atau hardcode hex (bukan @color/), perbaiki.

Wajib ada di tiap layar yang mengambil data:
- ProgressBar (loading), RecyclerView (data), TextView pesan (error/kosong)
  -> ikuti pola di Bagian 12 (Loading / Error / Empty state).

Navigasi:
- Home / Search / Favorit = 3 Fragment dalam SATU MainActivity, diganti lewat
  FragmentManager saat item bottom nav custom (LinearLayout) diklik (TANPA Navigation Component):
    supportFragmentManager.beginTransaction()
        .replace(R.id.container, HomeFragment())
        .commit()
- Pindah ke Detail = buka DetailActivity pakai Intent + kirim OBJEK Resep (Serializable):
    val intent = Intent(requireContext(), DetailActivity::class.java)
    intent.putExtra("resep", resep)
    startActivity(intent)
  Di DetailActivity ambil objeknya:
    val resep = intent.getSerializableExtra("resep") as Resep
  Detail TIDAK request API lagi — semua data (bahan, langkah) sudah ada di objek.

## 10. ViewBinding di Fragment & konvensi nama class
- Di Fragment, set binding ke null pada onDestroyView untuk cegah memory leak:
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    // di onDestroyView(): _binding = null
- Nama class per fitur dibuat KONSISTEN & sejajar:
  <Fitur>Fragment + <Fitur>ViewModel (mis. HomeFragment + HomeViewModel).
  Khusus Detail: DetailActivity (bukan Fragment). DetailViewModel TIDAK wajib karena
  Detail tidak mengambil data dari API (cukup baca objek Resep dari Intent).
- Adapter RecyclerView: <Nama>Adapter (mis. ResepAdapter), pakai
  RecyclerView.Adapter sederhana (ViewHolder + ViewBinding item layout).

## 11. Cara ambil & tampilkan data: Retrofit Callback (enqueue, tanpa Coroutines)
Async cukup pakai bawaan Retrofit (enqueue). TIDAK pakai Coroutines & TIDAK pakai LiveData.
- ApiService pakai Call<...> (BUKAN suspend), satu endpoint saja:
    @GET("resep.json")
    fun getResep(): Call<List<Resep>>
- Karena TANPA Repository, ApiService dipakai LANGSUNG oleh ViewModel.
- ViewModel membungkus enqueue & memberi hasil lewat callback berhasil/gagal:
    fun ambilResep(
        onBerhasil: (List<Resep>) -> Unit,
        onGagal: () -> Unit
    ) {
        api.getResep().enqueue(object : Callback<List<Resep>> {
            // dipanggil kalau request selesai (cek apakah sukses)
            override fun onResponse(call: Call<List<Resep>>, res: Response<List<Resep>>) {
                if (res.isSuccessful) onBerhasil(res.body() ?: emptyList()) else onGagal()
            }
            // dipanggil kalau gagal (mis. tidak ada internet)
            override fun onFailure(call: Call<List<Resep>>, t: Throwable) {
                onGagal()
            }
        })
    }
- Fragment cukup memanggil & menentukan aksi saat berhasil / gagal:
    viewModel.ambilResep(
        onBerhasil = { list -> adapter.submitList(list) },
        onGagal = { binding.tvPesan.text = "Gagal memuat data" }
    )
- Untuk SEARCH & FILTER: ambil SEMUA resep sekali pakai getResep(), simpan di list,
  lalu saring di sisi app (CLIENT-SIDE) pakai filter Kotlin
  (mis. semuaResep.filter { it.nama.contains(query, true) }). TIDAK ada query ke server.
- JANGAN taruh kode Retrofit langsung di Fragment; semua lewat ViewModel.

## 12. Pola Loading / Error / Empty state (seragam di semua layar)
Setiap layar yang ambil data dari API WAJIB menangani 3 kondisi:
- LOADING : tampilkan ProgressBar, sembunyikan list & pesan.
- SUKSES  : tampilkan RecyclerView berisi data, sembunyikan ProgressBar.
- ERROR / KOSONG : sembunyikan list, tampilkan TextView pesan
  (mis. "Gagal memuat data" / "Belum ada resep").
- Diatur lewat callback Retrofit: loading sebelum memanggil, sukses/error di callback.
- Cukup pakai View.VISIBLE / View.GONE; tidak perlu library tambahan.

Contoh pola (loading sebelum panggil, sukses & error di dalam callback):
    private fun tampilkanResep() {
        binding.progressBar.visibility = View.VISIBLE   // mulai loading
        binding.tvPesan.visibility = View.GONE
        viewModel.ambilResep(
            onBerhasil = { list ->
                binding.progressBar.visibility = View.GONE
                adapter.submitList(list)
                // tampilkan pesan kalau data kosong
                binding.tvPesan.visibility =
                    if (list.isEmpty()) View.VISIBLE else View.GONE
            },
            onGagal = {
                binding.progressBar.visibility = View.GONE
                binding.tvPesan.text = "Gagal memuat data"   // error
                binding.tvPesan.visibility = View.VISIBLE
            }
        )
    }

## 13. Konsistensi data & komponen
- Patuhi bentuk data model yang sudah disepakati; JANGAN ubah nama field sendiri.
  Kalau perlu diubah, koordinasi dulu karena dipakai banyak screen.
- Pakai ulang fungsi/komponen yang sudah ada; jangan bikin duplikat (DRY).
- Warna pakai @color/... dari colors.xml, jangan hardcode hex di layout.
  Ukuran spasi & teks ambil dari skala desain (lihat Bagian 9), ditulis manual di XML.
- Format kode konsisten: indentasi rapi, hapus import yang tidak terpakai.

## 14. Batas scope (jangan keluar dari bagian sendiri)
- Tiap anggota HANYA mengerjakan fitur & folder bagiannya sendiri.
- JANGAN mengubah file/fitur milik anggota lain.
  Contoh: yang mengerjakan Home (ui/home/) JANGAN mengubah ui/detail/,
  ui/search/, ui/favorite/, atau file punya orang lain.
- Kalau butuh sesuatu dari bagian lain (mis. fungsi/data milik anggota lain),
  koordinasikan dulu dengan pemiliknya; jangan diubah sendiri diam-diam.
- Tetap fokus hanya pada tugas yang sedang dikerjakan.

## 15. Alur WAJIB saat diminta membuat sebuah fitur
Kalau anggota minta dibuatkan suatu fitur, JANGAN langsung tulis semua kodenya.
Ikuti urutan ini:
1. Jelaskan detail teknisnya secara RINCI: file/komponen apa yang terlibat,
   alur datanya bagaimana, dan kegunaan tiap bagian (ringkas).
2. SARANKAN pecah fitur jadi bagian-bagian kecil (mis. buat model -> buat
   layout XML -> buat ViewModel -> sambungkan ke API), dikerjakan satu per satu.
3. TANYA persetujuan dulu ("mau lanjut dengan rencana ini?") SEBELUM ngoding.
4. Kalau ada yang kurang jelas dari permintaan, TANYA BALIK dulu: maksudnya
   bagaimana, maunya seperti apa, tujuannya apa — jangan berasumsi sendiri.
5. Kerjakan BERTAHAP per bagian kecil. HINDARI membuat banyak file/fitur
   sekaligus (bulk) dalam sekali jalan, karena rawan error & halusinasi.
6. Selesai satu bagian -> pastikan benar & jelas -> baru lanjut bagian berikutnya.

## 16. WAJIB bertanya saat ragu (jangan asal menebak)
- Kalau ada keputusan yang ambigu atau kamu tidak yakin (mis. ada 2 cara
  yang sama-sama valid), JANGAN langsung menebak. TANYA dulu ke user,
  jelaskan pilihan yang ada, dan lanjut hanya setelah dikonfirmasi.
- Kalau user bilang "terserah", baru pilih yang PALING SEDERHANA.

## 17. Selalu jelaskan SEBELUM mengubah kode
Sebelum membuat atau mengubah kode, jelaskan SINGKAT dulu:
- Apa saja yang akan diubah/dibuat (file mana).
- Efeknya apa terhadap aplikasi.
- Kelebihan & kekurangan pendekatan tersebut.
Baru lakukan perubahan setelah penjelasan itu.

## 18. Prinsip menjaga KESEDERHANAAN (biar mudah dikuasai & dijelaskan)
- Kerjakan HANYA yang diminta. Jangan menambah fitur, layar, atau library
  ekstra yang tidak diminta — walaupun niatnya "biar lebih bagus".
- IKUTI pola yang sudah ada. Untuk hal yang sama, pakai cara yang sama:
  ambil data ikut Bagian 11, loading/error ikut Bagian 12, semua daftar pakai
  RecyclerView + Adapter dengan gaya yang sama. JANGAN bikin pola/cara baru
  untuk hal yang sudah ada polanya.
- Layar/fitur tetap yang disepakati: Home, Search, Detail, Favorit. Jangan
  menambah layar/fitur baru tanpa kesepakatan tim.
- Pilih solusi yang bisa dijelaskan dalam 1-2 kalimat. Kalau sebuah kode terlalu
  ribet untuk dijelaskan, minta AI memberi versi yang lebih sederhana.
- Anggota WAJIB paham kode bagiannya. Kalau ada yang belum paham, minta AI
  menjelaskan ulang lebih sederhana SAMPAI paham sebelum dipakai atau di-push.

## 19. Yang harus selalu diingat AI
- Jawaban WAJIB konsisten dengan stack & larangan di atas.
- Selalu sertakan komentar/penjelasan singkat untuk kode yang dibuat.
- Kalau diminta sesuatu yang melanggar larangan, ingatkan dan tawarkan
  alternatif yang sesuai stack.
- Kalau menemukan kode/layout LAMA yang masih pakai Material Components, WAJIB
  perbaiki ke versi manual (view standar + background drawable shape) TANPA diminta,
  lalu jelaskan singkat apa yang diganti. Custom theme color (Theme.AppCompat) bukan
  Material, jadi itu TIDAK perlu diutak-atik.

## 20. Konvensi Git (3 lapis branch: main -> dev -> fitur)
- main  : HANYA untuk hasil FINAL yang sudah jadi & stabil. Jangan ngoding di sini.
- dev   : branch tempat semua fitur digabung selama masa pengembangan.
- fitur : tiap anggota bikin branch sendiri DARI dev.
          Format nama: nama/feat-namafitur (mis. jay/feat-home).
- Alur harian:
  1. `git checkout dev` lalu `git pull origin dev` (ambil update terbaru teman).
  2. `git checkout -b nama/feat-fitur` (bikin branch dari dev).
  3. Ngoding -> commit -> `git push origin nama/feat-fitur`.
  4. Merge branch fitur ke dev LEWAT Pull Request (biar bisa di-review teman).
- SELALU pull dev terbaru SEBELUM mulai & sebelum merge, biar minim konflik.
- Setelah semua fitur jadi & stabil di dev, baru FINAL merge dev -> main.
- Branch fitur TIDAK perlu dihapus setelah merge — biarkan sebagai history pengerjaan.
- JANGAN ngoding/push langsung ke main maupun dev; selalu lewat branch fitur.
- Pesan commit pakai bahasa Indonesia & deskriptif, diawali kata kerja:
  "menambahkan halaman detail", "memperbaiki bug favorit" (bukan "update"/"fix").
- Commit kecil & sering; jangan numpuk banyak perubahan dalam 1 commit.

## 21. Definition of Done (fitur dianggap selesai kalau)
- Aplikasi jalan tanpa crash.
- Sudah ada penanganan loading & error (untuk fitur yang ambil data).
- Sudah dites manual di emulator/HP.
- Kode rapi, ada komentar singkat, TANPA kode sampah (Log/komentar bekas coba-coba).
- Sudah di-commit & push ke branch sendiri.
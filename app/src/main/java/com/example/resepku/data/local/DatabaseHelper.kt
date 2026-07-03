package com.example.resepku.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.resepku.data.remote.Resep
import com.google.gson.Gson


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "resepku.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_FAVORIT = "favorit"
        private const val COL_ID = "id"
        private const val COL_DATA = "data" //
    }

    private val gson = Gson()

    override fun onCreate(db: SQLiteDatabase) {
        val query = "CREATE TABLE $TABLE_FAVORIT ($COL_ID TEXT PRIMARY KEY, $COL_DATA TEXT)"
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORIT")
        onCreate(db)
    }

    fun tambahFavorit(resep: Resep): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ID, resep.id)
            put(COL_DATA, gson.toJson(resep))
        }
        val hasil = db.insert(TABLE_FAVORIT, null, values)
        db.close()
        return hasil != -1L
    }

    fun hapusFavorit(id: String): Boolean {
        val db = writableDatabase
        val hasil = db.delete(TABLE_FAVORIT, "$COL_ID = ?", arrayOf(id))
        db.close()
        return hasil > 0
    }

    fun isFavorit(id: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COL_ID FROM $TABLE_FAVORIT WHERE $COL_ID = ?", arrayOf(id))
        val ada = cursor.count > 0
        cursor.close()
        db.close()
        return ada
    }

    fun ambilSemuaFavorit(): List<Resep> {
        val list = mutableListOf<Resep>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COL_DATA FROM $TABLE_FAVORIT", null)

        if (cursor.moveToFirst()) {
            do {
                val jsonData = cursor.getString(0)
                val resep = gson.fromJson(jsonData, Resep::class.java)
                list.add(resep)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }
}

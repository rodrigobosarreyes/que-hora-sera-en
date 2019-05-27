package com.bosarreyes.rodrigo.calculadorahoras.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.bosarreyes.rodrigo.calculadorahoras.R
import com.bosarreyes.rodrigo.calculadorahoras.dto.MyTimezone
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class DBHelper(private val context: Context):
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val sqlCrearTabla = "CREATE TABLE $NOMBRE_TABLA (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_CIUDAD TEXT NOT NULL, " +
                "$COL_TIMEZONE TEXT NOT NULL" +
                ");"

        db?.execSQL(sqlCrearTabla)

        cargarDatos(db)
    }

    fun cargarDatos(db: SQLiteDatabase?) {
        val raw: InputStream = context.resources.openRawResource(R.raw.timezones)
        val reader = BufferedReader(InputStreamReader(raw, "UTF8"))

        reader.forEachLine {
            if(it != "[" && it != "]" && it != "") {
                val jsonS = it.removeSuffix(",").trim()

                val jsonObj = JSONObject(jsonS)
                Log.d("DBHELPER", jsonObj["name"].toString())
                val tz = MyTimezone(null, jsonObj["name"].toString(), jsonObj["value"].toString())
                db?.insert(NOMBRE_TABLA, null, tz.toContentValues())
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun getTodasCiudades(db: SQLiteDatabase): List<String> {
        val cursor = db.query(
            NOMBRE_TABLA,
            arrayOf(COL_CIUDAD),
            null,
            null,
            null,
            null,
            null
        )

        val ciudades = generateSequence { if (cursor.moveToNext()) cursor else null }
            .map { it.getString(0) }
            .toList()
        cursor.close()

        return ciudades
    }

    fun getTimezoneCiudad(db: SQLiteDatabase, ciudad: String): String {
        val cursor = db.query(
            NOMBRE_TABLA,
            arrayOf(COL_TIMEZONE),
            "$COL_CIUDAD LIKE ?",
            arrayOf(ciudad),
            null,
            null,
            null
        )
        cursor.moveToFirst()
        val tz = cursor.getString(0)
        cursor.close()
        return tz

    }

    fun getCiudadTimezone(db: SQLiteDatabase, timezone: String): String {
        val cursor = db.query(
            NOMBRE_TABLA,
            arrayOf(COL_CIUDAD),
            "$COL_TIMEZONE LIKE ?",
            arrayOf(timezone),
            null,
            null,
            null
        )
        cursor.moveToFirst()
        val tz = cursor.getString(0)
        cursor.close()
        return tz

    }

    companion object {
        private const val DB_VERSION = 2
        private const val DB_NAME = "timezones.db"
        private val factory = null

        const val NOMBRE_TABLA = "timezones"
        const val COL_ID = "_id"
        const val COL_CIUDAD = "ciudad"
        const val COL_TIMEZONE = "timezone"
    }
}
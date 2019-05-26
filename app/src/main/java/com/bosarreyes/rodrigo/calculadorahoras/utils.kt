package com.bosarreyes.rodrigo.calculadorahoras

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class Utils() {
    fun cargarDatos(context: Context) {
        val raw: InputStream = context.resources.openRawResource(R.raw.timezones)
        val reader = BufferedReader(InputStreamReader(raw, "UTF8"))

        reader.forEachLine {
            line -> if(line != "[" && line != "]") {
                val jsonS = line.removeSuffix(",").trim()

                val jsonObj = JSONObject(jsonS)
                Log.d("DBHELPER", jsonObj["name"].toString())
            }
        }
    }
}
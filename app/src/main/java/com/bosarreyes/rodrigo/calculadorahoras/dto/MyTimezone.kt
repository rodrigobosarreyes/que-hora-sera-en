package com.bosarreyes.rodrigo.calculadorahoras.dto

import android.content.ContentValues
import android.os.Parcel
import android.os.Parcelable
import com.bosarreyes.rodrigo.calculadorahoras.dao.DBHelper

class MyTimezone(val id: Int?, val ciudad: String?, val timezone: String?) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    /**
     * Devuelve un objeto ContentValues con los valores de la instancia
     */
    fun toContentValues(): ContentValues {
        val cv = ContentValues()
        cv.put(DBHelper.COL_ID, id)
        cv.put(DBHelper.COL_CIUDAD, ciudad)
        cv.put(DBHelper.COL_TIMEZONE, timezone)

        return cv
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(ciudad)
        parcel.writeString(timezone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyTimezone> {
        override fun createFromParcel(parcel: Parcel): MyTimezone {
            return MyTimezone(parcel)
        }

        override fun newArray(size: Int): Array<MyTimezone?> {
            return arrayOfNulls(size)
        }
    }

}
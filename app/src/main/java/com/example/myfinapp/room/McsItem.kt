package com.example.myfinapp.room

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
@DatabaseView(
    "SELECT mcs.id, mcs.plus, mcs.minus, c.category_name, " +
            "strftime('%m.%Y', mcs.date, 'unixepoch', 'localtime') AS formattedDate " +
            "FROM monthly_category_summary mcs " +
            "JOIN category c ON mcs.category_id = c.id " +
            "ORDER BY mcs.date DESC"
)
data class McsItem(
    val id: Long,
    val plus: Double,
    val minus: Double,
    @ColumnInfo(name = "category_name") val categoryName: String?,
    val formattedDate: String?,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeDouble(plus)
        parcel.writeDouble(minus)
        parcel.writeString(categoryName)
        parcel.writeString(formattedDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<McsItem> {
        override fun createFromParcel(parcel: Parcel): McsItem {
            return McsItem(parcel)
        }

        override fun newArray(size: Int): Array<McsItem?> {
            return arrayOfNulls(size)
        }
    }
}
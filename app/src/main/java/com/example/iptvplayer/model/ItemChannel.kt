package com.example.iptvplayer.model

import android.os.Parcel
import android.os.Parcelable

class ItemChannel(
    val id: Int,
    val channelName: String,
    val streamUrl: String,
    val logoUrl: String,
    val category: String,
    var selected: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(), // Reading the id
        parcel.readString() ?: "", // Read and provide a default value in case it's null
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt() // Reading the selected value
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id) // Writing the id
        parcel.writeString(channelName)
        parcel.writeString(streamUrl)
        parcel.writeString(logoUrl)
        parcel.writeString(category)
        parcel.writeInt(selected) // Writing the selected value
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemChannel> {
        override fun createFromParcel(parcel: Parcel): ItemChannel {
            return ItemChannel(parcel)
        }

        override fun newArray(size: Int): Array<ItemChannel?> {
            return arrayOfNulls(size)
        }
    }
}

package com.example.trelloclone.models

import android.os.Parcel
import android.os.Parcelable

data class Task(
    var title: String = "",
    val createdBy: String = ""
): Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(title)
        writeString(createdBy)
    }

    companion object CREATOR: Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }
        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}

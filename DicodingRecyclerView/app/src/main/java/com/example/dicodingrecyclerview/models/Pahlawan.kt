package com.example.dicodingrecyclerview.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Pahlawan(
    val name: String,
    val description: String,
    val photo: Int
) : Parcelable


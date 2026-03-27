package com.example.freshyzoappmodule.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeBlogs( @SerializedName("blog_id") val blogId: String,
                      @SerializedName("title") val title: String,
                      @SerializedName("description") val description: String,
                      @SerializedName("image_url") val imageUrl: String,
) : Parcelable


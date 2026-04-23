package com.shyamdairyfarm.user.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Complaint(
    val id: String,
    val category: String,
    val issueType: String,
    val description: String,
    val status: String, // e.g., "Pending", "Resolved", "In Progress"
    val date: String,
    val imageUrl: String? = null
) : Parcelable

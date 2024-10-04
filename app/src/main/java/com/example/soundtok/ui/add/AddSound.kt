package com.example.soundtok.ui.add

import java.util.Date

data class AddSound(
    val title: String,
    val description: String,
    val time: Date,
    val latitude: String,
    val longitude: String,
    val duration: String,
    val ageRestriction: String,
    val rating: String,
    val fileUrl: String,
)

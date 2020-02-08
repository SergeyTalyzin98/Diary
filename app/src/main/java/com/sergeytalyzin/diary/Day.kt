package com.sergeytalyzin.diary

data class Day(
    val id: Int, val day: String, val date: String,
    val text: String, val color: String,
    val photos: MutableList<Array<String>>, val countPhotos: Int = 0
)




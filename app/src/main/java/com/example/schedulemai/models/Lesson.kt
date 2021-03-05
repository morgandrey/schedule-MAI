package com.example.schedulemai.models

import java.io.Serializable


/**
 * Created by Andrey Morgunov on 04/03/2021.
 */

data class Lesson(
    var lessonDate: String,
    val lessonName: List<String>,
    val lessonType: List<String>,
    var lessonTime: List<String>,
    var lessonLocation: List<String>
) : Serializable

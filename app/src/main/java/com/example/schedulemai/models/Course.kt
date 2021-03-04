package com.example.schedulemai.models

import java.io.Serializable


/**
 * Created by Andrey Morgunov on 04/03/2021.
 */

data class Course(
        val course: Int,
        val institute: Int,
        val group: String
) : Serializable

package com.example.schedulemai.presentation

import com.example.schedulemai.models.Lesson


/**
 * Created by Andrey Morgunov on 04/03/2021.
 */

class LessonListContract {
    interface View {
        fun onError(e: Throwable)
        fun onSuccess(list: List<Lesson>)
    }

    interface Presenter {
        fun getGroupLessons(group: String)
        fun onDestroy()
    }
}
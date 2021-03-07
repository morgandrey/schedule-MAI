package com.example.schedulemai.presentation

import com.example.schedulemai.models.Lesson


/**
 * Created by Andrey Morgunov on 04/03/2021.
 */

interface LessonListContract {
    interface View {
        fun onError(e: Throwable)
        fun onSuccessGetWeeks(weeks: Map<Int, String>)
        fun onSuccessGetGroups(list: List<Lesson>)
    }

    interface Presenter {
        fun getGroupLessons(group: String, week: Int?)
        fun getWeeks(group: String)
        fun onDestroy()
    }
}
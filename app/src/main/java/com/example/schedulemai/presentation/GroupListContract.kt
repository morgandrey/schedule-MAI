package com.example.schedulemai.presentation

import com.example.schedulemai.models.Course


/**
 * Created by Andrey Morgunov on 04/03/2021.
 */

interface GroupListContract
{
    interface View {
        fun onError(e: Throwable)
        fun onSuccess(list: List<Course>)
    }

    interface Presenter {
        fun getGroups()
        fun onDestroy()
    }
}
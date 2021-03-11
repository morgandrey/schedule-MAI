package com.example.schedulemai.presentation

import com.example.schedulemai.models.Course
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution


/**
 * Created by Andrey Morgunov on 04/03/2021.
 */

@AddToEndSingle
interface GroupListView : MvpView {
    fun switchProgressBar(show: Boolean = true)
    @OneExecution
    fun showError(exception: Throwable)
    fun onSuccess(list: List<Course>)
}
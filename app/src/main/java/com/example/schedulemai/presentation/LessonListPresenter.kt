package com.example.schedulemai.presentation

import com.example.schedulemai.remote.HtmlParser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.MvpPresenter


/**
 * Created by Andrey Morgunov on 04/03/2021.
 */

class LessonListPresenter : MvpPresenter<LessonListView>() {

    private val compositeDisposable = CompositeDisposable()

    fun getGroupLessons(group: String, week: Int?) {
        viewState.switchProgressBar(true)
        compositeDisposable.add(HtmlParser.parseLessonsFromGroup(group, week)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    viewState.onSuccessGetGroups(it)
                    viewState.switchProgressBar(false)
                },
                { error -> viewState.showError(error) }
            )
        )
    }

    fun getWeeks(group: String) {
        compositeDisposable.add(HtmlParser.parseWeeksFromGroup(group)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { weeks -> viewState.showChooseWeekDialog(weeks) },
                { error -> viewState.showError(error) }
            )
        )
    }

    override fun onDestroy() {
        compositeDisposable.clear()
    }
}
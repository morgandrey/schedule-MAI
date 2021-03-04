package com.example.schedulemai.presentation

import com.example.schedulemai.models.Course
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import java.io.IOException


/**
 * Created by Andrey Morgunov on 04/03/2021.
 */

class GroupListPresentation(private val view: GroupListContract.View) :
    GroupListContract.Presentation {

    private val compositeDisposable = CompositeDisposable()

    private fun getObservableGroups(): Observable<List<Course>> {
        return Observable.create {
            try {
                val totalList = mutableListOf<Course>()
                val doc = Jsoup.connect("http://mai.ru/education/schedule/").get()
                val courses = doc.select("div.sc-container").select("h5")
                for (course in courses) {
                    val cour = course.text()
                        .removeSuffix(" курс")
                        .toInt()
                    val institutes = doc.select("div.sc-table.sc-table-groups")
                        .select("div.sc-table-row")
                        .select("a.sc-table-col")
                    for (institute in institutes) {
                        val inst = institute.attr("href")
                            .substringAfter("Институт-№")
                            .toInt()
                        if (institute.attr("href") == "#fac-$cour-Институт-№$inst") {
                            val groupsDoc = doc.select("div.sc-container")
                                .select("div.sc-table.sc-table-groups")
                                .select("div.sc-table-row")
                                .select("div.sc-table-col-body")
                            for (group in groupsDoc) {
                                val attr = group.attr("id")
                                if (attr == "fac-$cour-Институт-№$inst") {
                                    group.select("div.sc-groups").select("a.sc-group-item")
                                        .forEach { elem ->
                                            totalList.add(
                                                Course(
                                                    course = cour,
                                                    institute = inst,
                                                    group = elem.text()
                                                )
                                            )
                                        }
                                }
                            }
                        }
                    }
                }
                it.onNext(totalList)
            } catch (io: IOException) {
                it.onError(io)
            } finally {
                it.onComplete()
            }
        }
    }

    override fun getGroups() {
        compositeDisposable.add(getObservableGroups()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    view.onSuccess(it)
                },
                {
                    view.onError(it)
                }
            )
        )
    }

    override fun onDestroy() {
        compositeDisposable.clear()
    }
}
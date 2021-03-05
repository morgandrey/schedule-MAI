package com.example.schedulemai.presentation

import com.example.schedulemai.models.Lesson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import java.io.IOException


/**
 * Created by Andrey Morgunov on 04/03/2021.
 */

class LessonListPresenter(private val view: LessonListContract.View) :
    LessonListContract.Presenter {

    private val compositeDisposable = CompositeDisposable()
    private fun getObservableGroupLessons(group: String): Observable<List<Lesson>> {
        return Observable.create {
            try {
                val doc =
                    Jsoup.connect("http://mai.ru/education/schedule/detail.php?group=$group")
                        .get()
                val resList = mutableListOf<Lesson>()
                val rows = doc.select("div.sc-container")
                for (row in rows) {
                    val lessonDate = row.select("div.sc-table")
                        .select("div.sc-table-row").select("div.sc-day-header")
                        .text()
                    val lessons = row.select("div.sc-table").select("div.sc-table-row")
                        .select("div.sc-table-detail").select("div.sc-table-row")
                    val lessonTypeList = mutableListOf<String>()
                    val lessonTimeList = mutableListOf<String>()
                    val lessonNameList = mutableListOf<String>()
                    val lessonLocationList = mutableListOf<String>()
                    for (lesson in lessons) {
                        lessonTimeList.add(lesson.select("div.sc-item-time").text())
                        lessonTypeList.add(lesson.select("div.sc-item-type").text())
                        lessonNameList.add(lesson.select("div.sc-item-title-body")
                            .select("span.sc-title")
                            .text()
                        )
                        lessonLocationList.add(lesson.select("div.sc-item-location").text())
                    }
                    resList.add(
                        Lesson(
                            lessonDate = lessonDate,
                            lessonType = lessonTypeList,
                            lessonTime = lessonTimeList,
                            lessonName = lessonNameList,
                            lessonLocation = lessonLocationList
                        )
                    )
                }
                it.onNext(resList)
            } catch (io: IOException) {
                it.onError(io)
            } finally {
                it.onComplete()
            }
        }
    }

    override fun getGroupLessons(group: String) {
        compositeDisposable.add(getObservableGroupLessons(group)
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
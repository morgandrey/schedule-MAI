package com.example.schedulemai.remote

import com.example.schedulemai.models.Course
import com.example.schedulemai.models.Lesson
import io.reactivex.Observable
import org.jsoup.Jsoup
import java.io.IOException


/**
 * Created by Andrey Morgunov on 11/03/2021.
 */

object HtmlParser {
    fun parseLessonsFromGroup(group: String, week: Int?): Observable<List<Lesson>> {
        return Observable.create {
            try {
                val doc = if (week == null) {
                    Jsoup.connect("http://mai.ru/education/schedule/detail.php?group=$group")
                        .get()
                } else {
                    Jsoup.connect("http://mai.ru/education/schedule/detail.php?group=$group&week=$week")
                        .get()
                }
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
                        lessonNameList.add(
                            lesson.select("div.sc-item-title-body")
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
            }
        }
    }

    fun parseWeeksFromGroup(group: String): Observable<Map<Int, String>> {
        val doc = Jsoup.connect("http://mai.ru/education/schedule/detail.php?group=$group").get()
        val resultMap = mutableMapOf<Int, String>()
        val weeksDoc = doc.select("table.table").select("tr")
        for (weekDoc in weeksDoc) {
            val weekNumber = weekDoc.text()
                .substring(0..1)
                .replace(" ", "")
                .toInt()
            val weekDates = weekDoc.text().substring(2)
            resultMap.put(weekNumber, weekDates)
        }
        return Observable.just(resultMap)
    }

    fun parseGroups(): Observable<List<Course>> {
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
}
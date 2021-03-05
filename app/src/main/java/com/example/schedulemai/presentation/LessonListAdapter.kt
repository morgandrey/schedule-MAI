package com.example.schedulemai.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulemai.R
import com.example.schedulemai.models.Lesson


/**
 * Created by Andrey Morgunov on 04/03/2021.
 */

class LessonListAdapter(private val dataSet: List<Lesson>) :
    RecyclerView.Adapter<LessonListAdapter.ViewHolder>() {

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val lessonDateTextView = itemView.findViewById<TextView>(R.id.lesson_date_text_view)
        private val lessonNamesTextView =
            itemView.findViewById<TextView>(R.id.lesson_names_text_view)

        fun bind(item: Lesson) {
            lessonDateTextView.text = item.lessonDate
            var resStr = ""
            for (i in item.lessonLocation.indices) {
                resStr +=
                    "${item.lessonTime[i]} ${item.lessonType[i]}\n${item.lessonName[i]}, ${item.lessonLocation[i]}"
                if (i != item.lessonLocation.size - 1) {
                    resStr += "\n"
                }
            }
            lessonNamesTextView.text = resStr
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.lesson_item_view, parent, false)
                return ViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        holder.bind(item)
    }

    override fun getItemCount() = dataSet.size
}
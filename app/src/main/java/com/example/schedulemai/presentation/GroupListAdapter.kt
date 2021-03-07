package com.example.schedulemai.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulemai.R
import com.example.schedulemai.models.Course
import com.example.schedulemai.utils.SharedPreferencesServiceImpl


/**
 * Created by Andrey Morgunov on 04/03/2021.
 */

class GroupListAdapter(
    private val dataSet: List<Course>,
    private val service: SharedPreferencesServiceImpl
) :
    RecyclerView.Adapter<GroupListAdapter.ViewHolder>() {

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courseTextView = itemView.findViewById<TextView>(R.id.course_text_view)
        private val instituteTextView = itemView.findViewById<TextView>(R.id.institute_text_view)
        private val groupTextView = itemView.findViewById<TextView>(R.id.group_text_view)

        fun bind(item: Course, service: SharedPreferencesServiceImpl) {
            courseTextView.text = item.course.toString()
            instituteTextView.text = item.institute.toString()
            groupTextView.text = item.group
            itemView.setOnClickListener {
                service.setGroup(item.group)
                it.findNavController()
                    .navigate(R.id.action_groupListFragment_to_lessonListFragment)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.group_item_view, parent, false)
                return ViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        holder.bind(item, service)
    }

    override fun getItemCount() = dataSet.size
}
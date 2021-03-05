package com.example.schedulemai.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.schedulemai.R
import com.example.schedulemai.databinding.FragmentLessonListBinding
import com.example.schedulemai.models.Lesson
import com.example.schedulemai.presentation.LessonListAdapter
import com.example.schedulemai.presentation.LessonListContract
import com.example.schedulemai.presentation.LessonListPresenter
import com.google.android.material.snackbar.Snackbar


class LessonListFragment : Fragment(R.layout.fragment_lesson_list), LessonListContract.View {

    private val binding: FragmentLessonListBinding by viewBinding()
    private lateinit var lessonListPresenter: LessonListPresenter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lesson_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val group = requireArguments().get("group") as String
        lessonListPresenter = LessonListPresenter(this)
        with(binding) {
            groupTitle.text = group
            lessonsProgressBar.visibility = View.VISIBLE
            lessonRecyclerView.visibility = View.INVISIBLE
        }
        lessonListPresenter.getGroupLessons(group)
    }

    override fun onError(e: Throwable) {
        Snackbar.make(requireView(), e.toString(), Snackbar.LENGTH_LONG).show()
    }

    override fun onSuccess(list: List<Lesson>) {
        with(binding) {
            lessonsProgressBar.visibility = View.GONE
            lessonRecyclerView.visibility = View.VISIBLE
            lessonRecyclerView.layoutManager = LinearLayoutManager(activity)
            lessonRecyclerView.adapter = LessonListAdapter(list)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lessonListPresenter.onDestroy()
    }
}
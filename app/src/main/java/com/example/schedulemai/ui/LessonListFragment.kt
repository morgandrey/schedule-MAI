package com.example.schedulemai.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.schedulemai.R
import com.example.schedulemai.databinding.FragmentLessonListBinding


class LessonListFragment : Fragment(R.layout.fragment_lesson_list) {

    private val binding: FragmentLessonListBinding by viewBinding()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lesson_list, container, false)
    }

}
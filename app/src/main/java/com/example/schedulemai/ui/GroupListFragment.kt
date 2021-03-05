package com.example.schedulemai.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.schedulemai.R
import com.example.schedulemai.databinding.FragmentGroupListBinding
import com.example.schedulemai.models.Course
import com.example.schedulemai.presentation.GroupListAdapter
import com.example.schedulemai.presentation.GroupListContract
import com.example.schedulemai.presentation.GroupListPresenter
import com.google.android.material.snackbar.Snackbar


class GroupListFragment : Fragment(R.layout.fragment_group_list), GroupListContract.View {

    private val binding: FragmentGroupListBinding by viewBinding()
    private lateinit var groupListPresenter: GroupListPresenter
    private var courseList: List<Course> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_group_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        groupListPresenter = GroupListPresenter(this)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.courses,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            binding.courseSpinner.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.institutes,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            binding.instituteSpinner.adapter = adapter
        }
        with(binding) {
            groupListRecyclerView.layoutManager = LinearLayoutManager(activity)
            groupListRecyclerView.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            findGroupsButton.setOnClickListener {
                filterGroupsByParameters(
                    courseSpinner.selectedItemPosition,
                    instituteSpinner.selectedItemPosition
                )
            }
        }
        groupListPresenter.getGroups()
    }

    private fun filterGroupsByParameters(course: Int, institute: Int) {
        if (course == 0 && institute == 0) {
            binding.groupListRecyclerView.adapter = GroupListAdapter(courseList)
            return
        }
        binding.groupListRecyclerView.adapter = GroupListAdapter(courseList.filter {
            when {
                course == 0 -> it.institute == institute
                institute == 0 -> it.course == course
                else -> it.institute == institute && it.course == course
            }
        })
    }

    override fun onError(e: Throwable) {
        binding.progressBar.visibility = View.GONE
        Snackbar.make(
            requireView(),
            e.message.toString(),
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        groupListPresenter.onDestroy()
    }

    override fun onSuccess(list: List<Course>) {
        binding.groupListRecyclerView.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        courseList = list
        binding.groupListRecyclerView.adapter = GroupListAdapter(list)
    }

}
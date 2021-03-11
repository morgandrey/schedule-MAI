package com.example.schedulemai.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.schedulemai.R
import com.example.schedulemai.databinding.FragmentGroupListBinding
import com.example.schedulemai.models.Course
import com.example.schedulemai.presentation.GroupListAdapter
import com.example.schedulemai.presentation.GroupListView
import com.example.schedulemai.presentation.GroupListPresenter
import com.example.schedulemai.utils.NetworkMonitorUtil
import com.example.schedulemai.utils.SharedPreferencesServiceImpl
import com.example.schedulemai.utils.NetworkUtils.showNetworkConnectionLostSnackBar
import com.google.android.material.snackbar.Snackbar
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import timber.log.Timber


class GroupListFragment : MvpAppCompatFragment(R.layout.fragment_group_list), GroupListView {

    private val presenter: GroupListPresenter by moxyPresenter { GroupListPresenter() }
    private val binding: FragmentGroupListBinding by viewBinding()

    private var courseList: List<Course> = listOf()
    private lateinit var sharedPreferencesServiceImpl: SharedPreferencesServiceImpl
    private lateinit var networkMonitor: NetworkMonitorUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkMonitor = NetworkMonitorUtil(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_group_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferencesServiceImpl =
            SharedPreferencesServiceImpl(requireActivity().applicationContext)
        if (sharedPreferencesServiceImpl.getGroup() != null) {
            view.findNavController().navigate(R.id.action_groupListFragment_to_lessonListFragment)
        } else {
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
                findGroupsButton.setOnClickListener {
                    filterGroupsByParameters(
                        courseSpinner.selectedItemPosition,
                        instituteSpinner.selectedItemPosition
                    )
                }
            }
            activity?.onBackPressedDispatcher?.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        val intent = Intent(Intent.ACTION_MAIN)
                        intent.addCategory(Intent.CATEGORY_HOME)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                })
            networkMonitor.result = { isAvailable, _ ->
                requireActivity().runOnUiThread {
                    when (isAvailable) {
                        true -> {
                            presenter.getGroups()
                        }
                        false -> {
                            showNetworkConnectionLostSnackBar(requireView())
                        }
                    }
                }
            }
        }
    }

    private fun filterGroupsByParameters(course: Int, institute: Int) {
        if (course == 0 && institute == 0) {
            binding.groupListRecyclerView.adapter =
                GroupListAdapter(courseList, sharedPreferencesServiceImpl)
            return
        }
        binding.groupListRecyclerView.adapter = GroupListAdapter(courseList.filter {
            when {
                course == 0 -> it.institute == institute
                institute == 0 -> it.course == course
                else -> it.institute == institute && it.course == course
            }
        }, sharedPreferencesServiceImpl)
    }

    override fun switchProgressBar(show: Boolean) {
        when (show) {
            true -> {
                binding.groupListProgressBar.visibility = View.VISIBLE
                binding.groupListRecyclerView.visibility = View.GONE
            }
            false -> {
                binding.groupListProgressBar.visibility = View.GONE
                binding.groupListRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    override fun showError(exception: Throwable) {
        Timber.e(exception)
        Snackbar.make(
            requireView(),
            getString(R.string.communication_error),
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        networkMonitor.register()
    }

    override fun onStop() {
        super.onStop()
        networkMonitor.unregister()
    }

    override fun onSuccess(list: List<Course>) {
        courseList = list
        binding.groupListRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.groupListRecyclerView.adapter = GroupListAdapter(list, sharedPreferencesServiceImpl)
    }
}
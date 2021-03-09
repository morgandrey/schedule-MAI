package com.example.schedulemai.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.schedulemai.R
import com.example.schedulemai.databinding.FragmentLessonListBinding
import com.example.schedulemai.models.Lesson
import com.example.schedulemai.presentation.LessonListAdapter
import com.example.schedulemai.presentation.LessonListContract
import com.example.schedulemai.presentation.LessonListPresenter
import com.example.schedulemai.utils.NetworkMonitorUtil
import com.example.schedulemai.utils.SharedPreferencesServiceImpl
import com.example.schedulemai.utils.Utils.showNetworkConnectionLostSnackBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LessonListFragment : Fragment(R.layout.fragment_lesson_list), LessonListContract.View {

    private val binding: FragmentLessonListBinding by viewBinding()
    private lateinit var lessonListPresenter: LessonListPresenter
    private lateinit var group: String
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
        return inflater.inflate(R.layout.fragment_lesson_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        sharedPreferencesServiceImpl =
            SharedPreferencesServiceImpl(requireActivity().applicationContext)
        group = sharedPreferencesServiceImpl.getGroup()!!
        lessonListPresenter = LessonListPresenter(this)
        with(binding) {
            groupTitle.text = group
            lessonsProgressBar.visibility = View.VISIBLE
            lessonRecyclerView.visibility = View.GONE
            changeGroupTextView.setOnClickListener {
                sharedPreferencesServiceImpl.deleteGroup()
                view.findNavController()
                    .navigate(R.id.action_lessonListFragment_to_groupListFragment)
            }
            changeWeekTextView.setOnClickListener {
                GlobalScope.launch {
                    lessonListPresenter.getWeeks(group)
                }
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(
                true
            ) {
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
                    true -> { lessonListPresenter.getGroupLessons(group, null) }
                    false -> { showNetworkConnectionLostSnackBar(requireView()) }
                }
            }
        }
    }

    override fun onError(e: Throwable) {
        Snackbar.make(requireView(), e.toString(), Snackbar.LENGTH_LONG).show()
    }

    override fun onSuccessGetWeeks(weeks: Map<Int, String>) {
        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.select_dialog_singlechoice,
                weeks.values.toList()
            )
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.week_dialog_title)
            .setSingleChoiceItems(adapter, -1, null)
            .setPositiveButton(
                R.string.dialog_ok
            ) { dialog, _ ->
                val week = (dialog as AlertDialog).listView.checkedItemPosition + 1
                binding.weekTextView.text = dialog.listView.getItemAtPosition(week - 1).toString()
                GlobalScope.launch { lessonListPresenter.getGroupLessons(group, week) }
            }
            .setNegativeButton(
                R.string.dialog_cancel
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onSuccessGetGroups(list: List<Lesson>) {
        with(binding) {
            lessonsProgressBar.visibility = View.GONE
            lessonRecyclerView.visibility = View.VISIBLE
            lessonRecyclerView.layoutManager = LinearLayoutManager(activity)
            lessonRecyclerView.adapter = LessonListAdapter(list)
        }
    }

    override fun onResume() {
        super.onResume()
        networkMonitor.register()

    }

    override fun onStop() {
        super.onStop()
        networkMonitor.unregister()
    }

    override fun onDestroy() {
        super.onDestroy()
        lessonListPresenter.onDestroy()
    }
}
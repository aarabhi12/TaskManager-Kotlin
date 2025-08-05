package com.example.myapplication.fragments

import AddToDoPopupFragment
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.utils.a.ToDoAdapter
import com.example.myapplication.utils.a.ToDoData
import com.google.android.material.textfield.TextInputEditText
import java.util.UUID

class HomeFragment : Fragment(), AddToDoPopupFragment.DialogNextBtnClickListener,
    ToDoAdapter.ToDoAdapterClicksInterface {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var adapter: ToDoAdapter
    private var mList: MutableList<ToDoData> = mutableListOf()

    private val TASK_LIST_KEY = "task_list_key"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("ToDoPrefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // Set up RecyclerView
        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)

        // Load tasks from SharedPreferences
        loadTasksFromPreferences()

        adapter = ToDoAdapter(mList)
        adapter.setListener(this)
        binding.mainRecyclerView.adapter = adapter

        registerEvents()
    }

    private fun registerEvents() {
        binding.addTaskBtn.setOnClickListener {
            showAddTaskPopup()
        }
    }

    private fun showAddTaskPopup() {
        val popupFragment = AddToDoPopupFragment()
        popupFragment.setListener(this)
        popupFragment.show(childFragmentManager, "AddToDoPopupFragment")
    }

    private fun saveTasksToPreferences() {
        val taskStrings = mList.map { it.task } // Extract task descriptions
        editor.putStringSet(TASK_LIST_KEY, taskStrings.toSet()) // Save as a Set
        editor.apply() // Apply the changes
    }

    private fun loadTasksFromPreferences() {
        val taskSet = sharedPreferences.getStringSet(TASK_LIST_KEY, null)

        taskSet?.let {
            mList = it.map { task -> ToDoData(UUID.randomUUID().toString(), task) }.toMutableList()
        } ?: run {
            mList = mutableListOf() // Initialize empty list if no tasks found
        }
    }

    override fun onSaveTask(todo: String, todoEt: TextInputEditText) {
        // Create new ToDoData object
        val newTask = ToDoData(UUID.randomUUID().toString(), todo)
        mList.add(newTask)

        // Save the updated list to SharedPreferences
        saveTasksToPreferences()

        // Notify adapter of the data change
        adapter.notifyDataSetChanged()

        Toast.makeText(context, "Task Added Successfully", Toast.LENGTH_SHORT).show()
        todoEt.text = null
    }

    override fun onUpdateTask(todoData: ToDoData, todoEt: TextInputEditText) {
        // Update existing task data
        val index = mList.indexOfFirst { it.taskId == todoData.taskId }
        if (index != -1) {
            mList[index].task = todoData.task
            saveTasksToPreferences()
            adapter.notifyDataSetChanged()
            Toast.makeText(context, "Task Updated Successfully", Toast.LENGTH_SHORT).show()
        }
        todoEt.text = null
    }

    override fun onDeleteTaskBtnClicked(toDoData: ToDoData) {
        mList.removeIf { it.taskId == toDoData.taskId }
        saveTasksToPreferences()
        adapter.notifyDataSetChanged()
        Toast.makeText(context, "Task Deleted Successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onEditTaskBtnClicked(toDoData: ToDoData) {
        val popupFragment = AddToDoPopupFragment.newInstance(toDoData.taskId, toDoData.task)
        popupFragment.setListener(this)
        popupFragment.show(childFragmentManager, "AddToDoPopupFragment")
    }
}

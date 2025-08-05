package com.example.myapplication.utils.a

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.EachTodoItemBinding

class ToDoAdapter(private val list: MutableList<ToDoData>) :
RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {

    private var listner:ToDoAdapterClicksInterface? = null
    fun setListener(listner:ToDoAdapterClicksInterface){
        this.listner = listner
    }



    inner class ToDoViewHolder(val binding: EachTodoItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding = EachTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToDoViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.todoTask.text = this.task

                binding.deleteTask.setOnClickListener {
                    listner?.onDeleteTaskBtnClicked(this)
                }

                binding.editTask.setOnClickListener {
                    listner?.onEditTaskBtnClicked(this)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


    interface ToDoAdapterClicksInterface{
        fun onDeleteTaskBtnClicked(toDoData: ToDoData)
        fun onEditTaskBtnClicked(toDoData: ToDoData)

    }

}



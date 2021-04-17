package com.example.mvvmtodo.ui.tasks

import android.app.Activity
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmtodo.data.Task
import com.example.mvvmtodo.data.TaskDao
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow()

    val task = state.get<Task>("task")
    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("taskImportance", value)
        }

    var taskDueDate = state.get<Long>("taskDueDate") ?: task?.dueTime ?: 0
        set(value) {
            field = value
            state.set("taskDueDate", value)
        }



    fun onSaveClick() {
        if (taskName.isBlank()) {
            Log.d("DUPA", "BLANK")
            ShowInvalidInputMessage("Name cannot be empty!");
            return
        }
        if (task != null) {
            val updatedTask =
                task.copy(name = taskName, important = taskImportance, dueTime = taskDueDate)
            updateTask(updatedTask)
        } else {
            val newTask = Task(name=taskName, important = taskImportance, dueTime = taskDueDate)
            createTask(newTask)
        }
        return
    }

    private fun updateTask(task: Task)  = viewModelScope.launch {
        taskDao.update(task)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBack(Activity.RESULT_OK))
    }

    private fun createTask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        Log.d("DUPA", "DODANO TASK")
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBack(Activity.RESULT_OK))

    }

    private fun ShowInvalidInputMessage(msg: String) = viewModelScope.launch {
        addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(msg))
    }

    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val msg : String) : AddEditTaskEvent()
        data class NavigateBack(val code : Int) : AddEditTaskEvent()
    }

}
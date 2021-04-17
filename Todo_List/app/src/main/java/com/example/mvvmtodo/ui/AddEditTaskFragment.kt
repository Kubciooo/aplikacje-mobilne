package com.example.mvvmtodo.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mvvmtodo.R
import com.example.mvvmtodo.databinding.FragmentAddEditTaskBinding
import com.example.mvvmtodo.ui.tasks.AddEditTaskViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task) {
    private val viewModel: AddEditTaskViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var binding = FragmentAddEditTaskBinding.bind(view)
        val cal = Calendar.getInstance()
        cal.timeInMillis = viewModel.task?.dueTime ?: Calendar.getInstance().timeInMillis
        binding.apply {

            buttonPickDate.setOnClickListener {
                val timeSetListener =
                    DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                        cal.set(Calendar.DAY_OF_MONTH, day)
                        cal.set(Calendar.YEAR, year)
                        cal.set(Calendar.MONTH, month)
                        textViewDueDate.text =
                            SimpleDateFormat("dd/MM HH:mm", Locale.ENGLISH).format(cal.timeInMillis)
                        viewModel.taskDueDate = cal.timeInMillis
                    }
                DatePickerDialog(
                    requireContext(),
                    timeSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            buttonPickTime.setOnClickListener {
                val timeSetListener =
                    TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                        cal.set(Calendar.HOUR_OF_DAY, hour)
                        cal.set(Calendar.MINUTE, minute)
                        textViewDueDate.text =
                            SimpleDateFormat("dd/MM HH:mm", Locale.ENGLISH).format(cal.timeInMillis)
                        viewModel.taskDueDate = cal.timeInMillis
                    }
                TimePickerDialog(
                    requireContext(),
                    timeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true
                ).show()
            }


            editTextTaskName.setText(viewModel.taskName)
            checkBoxImportant.isChecked = viewModel.taskImportance
            checkBoxImportant.jumpDrawablesToCurrentState()
            textViewDateCreated.isVisible = viewModel.task != null
            textViewDateCreated.text = "Created ${viewModel.task?.createdDateFormatted}"
            textViewDueDate.text =
                SimpleDateFormat("dd/MM HH:mm", Locale.ENGLISH).format(cal.timeInMillis)

            editTextTaskName.addTextChangedListener {
                viewModel.taskName = it.toString()
            }

            checkBoxImportant.setOnCheckedChangeListener { _, isChecked ->
                viewModel.taskImportance = isChecked
            }

            fabSaveTask.setOnClickListener {
                Log.d("DUPA", "SAVED TASK XD");
                viewModel.onSaveClick()
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.addEditTaskEvent.collect { evt ->
                    when (evt) {
                        is AddEditTaskViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                            Snackbar.make(requireView(), evt.msg, Snackbar.LENGTH_LONG).show()
                        }
                        is AddEditTaskViewModel.AddEditTaskEvent.NavigateBack -> {
                            binding.editTextTaskName.clearFocus() // clear keyboard
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }
}
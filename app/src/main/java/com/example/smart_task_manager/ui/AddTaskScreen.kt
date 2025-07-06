package com.example.smart_task_manager.ui

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smart_task_manager.viewmodels.TaskViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smart_task_manager.models.Task


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    navController: NavController,
    taskId: String = "",
    titleArg: String = "",
    categoryArg: String = "",
    deadlineArg: String = "",
    viewModel: TaskViewModel = viewModel()
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val categories by viewModel.categories.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    var taskTitle by remember { mutableStateOf(titleArg) }
    var selectedCategory by remember { mutableStateOf(categoryArg) }
    var selectedDateTime by remember { mutableStateOf(deadlineArg) }


    // Date & Time pickers
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun showDateTimePicker() {
        android.app.DatePickerDialog(
            context,
            { _, year, month, day ->
                TimePickerDialog(context, { _, hour, minute ->
                    calendar.set(year, month, day, hour, minute)
                    selectedDateTime = dateFormatter.format(calendar.time)
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    LaunchedEffect(Unit) {
        viewModel.fetchCategories(userId)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Add Task") }) },
        content = { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Dropdown for category
                var expanded by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {
                        selectedCategory = it
                        expanded = true
                    },
                    label = { Text("Category") },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                DropdownMenu(
                    expanded = expanded && categories.isNotEmpty(),
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.filter { it.contains(selectedCategory, ignoreCase = true) }.forEach { suggestion ->
                        DropdownMenuItem(
                            text = { Text(suggestion) },
                            onClick = {
                                selectedCategory = suggestion
                                expanded = false
                            }
                        )
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedDateTime,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Deadline") },
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    // Transparent clickable overlay
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                showDateTimePicker()
                            }
                    )
                }


                Spacer(modifier = Modifier.height(16.dp))

                var errorMessage by remember { mutableStateOf("") }

                Button(onClick = {
                    val success = if (taskId.isEmpty()) {
                        viewModel.addTask(userId, taskTitle, selectedCategory, selectedDateTime)
                    } else {
                        viewModel.updateTask(userId, Task(taskId, taskTitle, selectedCategory, selectedDateTime))
                        true
                    }

                    if (success) {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }

                    } else {
                        errorMessage = "Please fill in all fields."
                    }

                    viewModel.scheduleTaskNotification(
                        context = context,
                        taskId = taskId.ifEmpty { System.currentTimeMillis().toString() },
                        title = taskTitle,
                        deadline = selectedDateTime
                    )
                }) {
                    Text("Save")
                }

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage, color = androidx.compose.ui.graphics.Color.Red)
                }

            }
        }
    )
}

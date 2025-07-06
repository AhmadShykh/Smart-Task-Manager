package com.example.smart_task_manager.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smart_task_manager.models.Task
import com.example.smart_task_manager.ui.components.TaskCard
import com.example.smart_task_manager.viewmodels.TaskViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: TaskViewModel = viewModel()
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val tasks by viewModel.tasks.collectAsState()

    // Fetch tasks on screen load
    LaunchedEffect(Unit) {
        viewModel.fetchTasks(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Task Manager") },
                actions = {
//                    TextButton(onClick = {
//                        FirebaseAuth.getInstance().signOut()
//                        navController.navigate("login") {
//                            popUpTo("home") { inclusive = true }
//                        }
//                    }) {
//                        Text("Logout", color = MaterialTheme.colorScheme.primary)
//                    }
                    IconButton(onClick = { FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        } }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("add")
            }) {
                Text("+")
            }
        },
        content = { padding ->
            if (tasks.isEmpty()) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("No tasks available.")
                }
            } else {
                LazyColumn(contentPadding = padding) {
                    items(tasks) { task ->
                        TaskCard(
                            task = task,
                            onEdit = {
                                navController.navigate(
                                    "add?taskId=${task.id}&title=${task.title}&category=${task.category}&deadline=${task.deadline}"
                                )
                            },
                            onDelete = {
                                viewModel.deleteTask(userId, task.id)
                            }
                        )
                    }

                }
            }
        }
    )
}

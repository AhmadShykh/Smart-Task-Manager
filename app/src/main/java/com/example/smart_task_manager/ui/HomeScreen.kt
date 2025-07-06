package com.example.smart_task_manager.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.smart_task_manager.ui.components.TaskCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val tasks = remember { mutableStateListOf("Buy groceries", "Study Kotlin", "Finish app UI") }
    Scaffold(
        topBar = { TopAppBar(title = { Text("Smart Task Manager") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add") }) {
                Text("+")
            }
        },
        content = { padding ->
            LazyColumn(contentPadding = padding) {
                items(tasks) { task -> TaskCard(task) }
            }
        }
    )
}

package com.example.smart_task_manager.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smart_task_manager.models.Task

@Composable
fun TaskCard(task: Task, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task.title)
            Text(text = "Category: ${task.category}")
            Text(text = "Deadline: ${task.deadline}")
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                TextButton(onClick = onEdit) {
                    Text("Edit")
                }
                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}


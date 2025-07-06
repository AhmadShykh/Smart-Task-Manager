package com.example.smart_task_manager.viewmodels


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.smart_task_manager.models.Task
import com.example.smart_task_manager.notifications.TaskNotificationReceiver
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Locale

class TaskViewModel : ViewModel() {
    private val dbRef = FirebaseDatabase.getInstance().reference

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    fun fetchTasks(userId: String) {
        dbRef.child("users").child(userId).child("tasks")
            .get()
            .addOnSuccessListener { snapshot ->
                val taskList = mutableListOf<Task>()
                snapshot.children.forEach {
                    val task = it.getValue(Task::class.java)
                    task?.let { taskList.add(it) }
                }
                _tasks.value = taskList
            }
    }

    fun fetchCategories(userId: String) {
        dbRef.child("users").child(userId).child("categories")
            .get()
            .addOnSuccessListener { snapshot ->
                val categoryList = mutableListOf<String>()
                snapshot.children.forEach {
                    val value = it.getValue(String::class.java)
                    value?.let { categoryList.add(it) }
                }
                _categories.value = categoryList
            }
    }

    fun addTask(userId: String, title: String, category: String, deadline: String): Boolean {
        if (title.isBlank() || category.isBlank() || deadline.isBlank()) {
            return false
        }

        val userRef = dbRef.child("users").child(userId)
        val taskId = userRef.child("tasks").push().key ?: return false

        val task = mapOf(
            "id" to taskId,
            "title" to title,
            "category" to category,
            "deadline" to deadline
        )

        userRef.child("tasks").child(taskId).setValue(task)

        if (!_categories.value.contains(category)) {
            userRef.child("categories").push().setValue(category)
            _categories.value = _categories.value + category
        }

        return true
    }

    fun deleteTask(userId: String, taskId: String) {
        dbRef.child("users").child(userId).child("tasks").child(taskId).removeValue()
        // Optionally refresh tasks after deletion
        fetchTasks(userId)
    }

    fun updateTask(userId: String, task: Task) {
        dbRef.child("users").child(userId).child("tasks").child(task.id).setValue(task)
        fetchTasks(userId)
    }

    fun scheduleTaskNotification(context: Context, taskId: String, title: String, deadline: String) {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val deadlineTime = formatter.parse(deadline)?.time ?: return
        val triggerTime = deadlineTime - (10 * 60 * 1000) // 10 minutes before deadline

        if (triggerTime < System.currentTimeMillis()) return // Don't schedule past notifications

        val intent = Intent(context, TaskNotificationReceiver::class.java).apply {
            putExtra("taskTitle", title)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode(), // Unique request code per task
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }

}

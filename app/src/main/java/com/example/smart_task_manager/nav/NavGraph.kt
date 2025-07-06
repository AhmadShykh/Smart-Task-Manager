import androidx.compose.runtime.Composable

import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.smart_task_manager.ui.AddEditTaskScreen
import com.example.smart_task_manager.ui.HomeScreen
import com.example.smart_task_manager.ui.LoginScreen
import com.example.smart_task_manager.ui.SignupScreen


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("add?taskId={taskId}&title={title}&category={category}&deadline={deadline}",
            arguments = listOf(
                navArgument("taskId") { defaultValue = "" },
                navArgument("title") { defaultValue = "" },
                navArgument("category") { defaultValue = "" },
                navArgument("deadline") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val category = backStackEntry.arguments?.getString("category") ?: ""
            val deadline = backStackEntry.arguments?.getString("deadline") ?: ""
            AddEditTaskScreen(navController, taskId, title, category, deadline)
        }

    }
}

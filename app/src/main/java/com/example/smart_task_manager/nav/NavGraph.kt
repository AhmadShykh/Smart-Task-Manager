import androidx.compose.runtime.Composable

import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
        composable("add") { AddEditTaskScreen(navController) }
    }
}

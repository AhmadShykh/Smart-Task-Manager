package com.example.smart_task_manager.ui

import com.example.smart_task_manager.R

import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smart_task_manager.viewmodels.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val credentialManager = CredentialManager.create(context)

    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true } // prevents back navigation to login
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Login") }) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    viewModel.loginUser(email, password) { success, message ->
                        if (success) navController.navigate("home")
                        else errorMessage = message
                    }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.height(8.dp))

//                val scope = rememberCoroutineScope()
//
//                Button(onClick = {
//                    scope.launch {
//                        try {
//                            val googleIdOption = GetGoogleIdOption.Builder()
//                                .setFilterByAuthorizedAccounts(false)
//                                .setServerClientId(context.getString(R.string.default_web_client_id))
//                                .build()
//
//                            val request = GetCredentialRequest.Builder()
//                                .addCredentialOption(googleIdOption)
//                                .build()
//
//                            val result = credentialManager.getCredential(context, request)
//                            val credential = result.credential
//
//                            if (credential is GoogleIdTokenCredential) {
//                                val idToken = credential.idToken
//                                viewModel.signInWithGoogle(idToken) { success, message ->
//                                    if (success) navController.navigate("home")
//                                    else errorMessage = message
//                                }
//                            } else {
//                                Log.w("LoginScreen", "Credential is not of type Google ID!")
//                            }
//                        } catch (e: Exception) {
//                            Log.e("LoginScreen", "Google Sign-In failed: ${e.localizedMessage}")
//                            errorMessage = "Google Sign-In failed: ${e.localizedMessage}"
//                        }
//                    }
//                }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
//                    Text("Continue with Google", color = PrimaryBlue)
//                }

                TextButton(onClick = { navController.navigate("signup") }) {
                    Text("Don't have an account? Sign Up")
                }

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMessage, color = Color.Red)
                }
            }
        }
    )
}


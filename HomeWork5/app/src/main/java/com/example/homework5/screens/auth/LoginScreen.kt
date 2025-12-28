package com.example.homework5.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.homework5.R
import com.example.homework5.di.ServiceLocator
import com.example.homework5.navigation.NavigationDestination
import com.example.homework5.screens.LoadingDialog
import com.example.homework5.model.LoginResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    onNavigate: (NavigationDestination) -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val userRepository = ServiceLocator.getUserRepository()

    LaunchedEffect(Unit) {
        val isLoggedIn = userRepository.isLoggedIn()
        if (isLoggedIn) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.login),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    errorMessage = null
                },
                label = { Text(stringResource(R.string.username)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = errorMessage != null
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                label = { Text(stringResource(R.string.password)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                isError = errorMessage != null
            )

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (username.isBlank()) {
                        errorMessage = context.getString(R.string.error_username_empty)
                        return@Button
                    }

                    if (password.isBlank()) {
                        errorMessage = context.getString(R.string.error_password_empty)
                        return@Button
                    }

                    scope.launch {
                        isLoading = true
                        val result = userRepository.login(username, password)
                        isLoading = false

                        result.fold(
                            onSuccess = { loginResult ->
                                when (loginResult) {
                                    is LoginResult.Success -> {
                                        onLoginSuccess()
                                    }
                                    is LoginResult.AccountDeleted -> {
                                        onNavigate(NavigationDestination.AccountRecovery(loginResult.user.id))
                                    }
                                }
                            },
                            onFailure = {
                                errorMessage = when (it.message) {
                                    context.getString(R.string.account_not_found) -> context.getString(R.string.error_user_not_found)
                                    context.getString(R.string.wrong_password) -> context.getString(R.string.error_invalid_credentials)
                                    else -> it.message ?: "Ошибка входа"
                                }
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(stringResource(R.string.login_button))
            }

            TextButton(
                onClick = { onNavigate(NavigationDestination.Register) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.dont_have_account))
            }
        }
    }

    if (isLoading) {
        LoadingDialog()
    }
}
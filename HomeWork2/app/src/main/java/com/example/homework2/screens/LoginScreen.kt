package com.example.homework2.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.homework2.R

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var emailErrorMessage by remember { mutableStateOf("") }
    var passwordErrorMessage by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.navigationBarsPadding()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.login_title),
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (emailError) {
                                emailError = false
                                emailErrorMessage = ""
                            }
                        },
                        label = { Text(stringResource(R.string.email_hint)) },
                        isError = emailError,
                        supportingText = {
                            if (emailError) {
                                Text(emailErrorMessage)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                keyboardController?.hide()
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (passwordError) {
                                passwordError = false
                                passwordErrorMessage = ""
                            }
                        },
                        label = { Text(stringResource(R.string.password_hint)) },
                        isError = passwordError,
                        supportingText = {
                            if (passwordError) {
                                Text(passwordErrorMessage)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation =
                            if (isPasswordVisible) {
                            VisualTransformation.None
                            }
                            else {
                                PasswordVisualTransformation()
                            },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                performLoginValidation(
                                    email = email,
                                    password = password,
                                    context = context,
                                    onEmailError = { message ->
                                        emailError = true
                                        emailErrorMessage = message
                                    },
                                    onPasswordError = { message ->
                                        passwordError = true
                                        passwordErrorMessage = message
                                    },
                                    onSuccess = {
                                        navController.navigate("notes/$email")
                                    }
                                )
                            }
                        ),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector =
                                        if (isPasswordVisible) {
                                        Icons.Default.Visibility
                                        }
                                        else {
                                            Icons.Default.VisibilityOff
                                        },
                                    contentDescription =
                                        if (isPasswordVisible) {
                                        stringResource(R.string.hide_password)
                                        }
                                        else {
                                            stringResource(R.string.show_password)
                                        }
                                )
                            }
                        }
                    )
                }

                Button(
                    onClick = {
                        keyboardController?.hide()
                        performLoginValidation(
                            email = email,
                            password = password,
                            context = context,
                            onEmailError = { message ->
                                emailError = true
                                emailErrorMessage = message
                            },
                            onPasswordError = { message ->
                                passwordError = true
                                passwordErrorMessage = message
                            },
                            onSuccess = {
                                navController.navigate("notes/$email")
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp)
                ) {
                    Text(stringResource(R.string.login_button))
                }
            }
        }
    }
}


private fun performLoginValidation(
    email: String,
    password: String,
    context: android.content.Context,
    onEmailError: (String) -> Unit,
    onPasswordError: (String) -> Unit,
    onSuccess: () -> Unit
) {
    var hasError = false

    if (email.isBlank()) {
        onEmailError(context.getString(R.string.email_empty_error))
        hasError = true
    }
    else if (!isValidEmail(email)) {
        onEmailError(context.getString(R.string.email_invalid_error))
        hasError = true
    }

    if (password.isBlank()) {
        onPasswordError(context.getString(R.string.password_empty_error))
        hasError = true
    }
    else if (password.length < 8) {
        onPasswordError(context.getString(R.string.password_length_error))
        hasError = true
    }

    if (!hasError) {
        onSuccess()
    }
}


private fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})".toRegex()
    return email.matches(emailRegex)
}
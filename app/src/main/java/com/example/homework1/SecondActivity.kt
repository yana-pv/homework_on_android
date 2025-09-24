package com.example.homework1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homework1.ui.theme.Homework1Theme

class SecondActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Homework1Theme {
                val userText = intent.getStringExtra("userText") ?: ""
                SecondScreen(userText)
            }
        }
    }
}

@Composable
fun SecondScreen(userText: String)
{
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(24.dp, 32.dp, 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Экран 2",
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 30.sp)
        )

        Spacer(modifier = Modifier.height(height = 32.dp))

        Text(
            text = userText.ifBlank { "Экран 2" }
        )

        Spacer(modifier = Modifier.height(height = 24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    val intent = Intent(context, ThirdActivity::class.java).apply {
                        if (userText.isNotBlank()) {
                            putExtra("userText", userText)
                        }
                    }
                    context.startActivity(intent)
                }
            ) {
                Text(text = "Перейти на\n    экран 3")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    val intent = Intent(context, FirstActivity::class.java)
                    context.startActivity(intent)
                }
            ) {
                Text(text = "Перейти на\n    экран 1")
            }
        }
    }
}
package com.example.tujapp.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { 
            SignInScreen()
        }
    }
}

@Composable
fun SignInScreen() {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sign in")

        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text(text = "Email") }
        )

        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text(text = "Password") }
        )

        Button(onClick = {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email.value, password.value)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // after successfully logged in
                    } else {
                        // todo
                    }
                }
        }) {
            Text(text = "Sign in")
        }
    }
}
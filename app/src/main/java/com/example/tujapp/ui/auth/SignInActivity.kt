package com.example.tujapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tujapp.MainActivity
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { 
            SignInScreen(
                navigateToSignUpActivity = {
                    val intent = Intent(this, SignUpActivity::class.java)
                    startActivity(intent)
                },
                navigateToMainActivity = {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                },
                toastErrorMessage = {
                    Toast.makeText(baseContext, "Failed to sign in", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun SignInScreen(
    navigateToSignUpActivity: () -> Unit,
    navigateToMainActivity: () -> Unit,
    toastErrorMessage: () -> Unit,
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sign in", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Input Email
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text(text = "Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Sign up button
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text(text = "Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Sign in button
        Button(
            onClick = {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email.value, password.value)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // after successfully logged in
                            navigateToMainActivity()
                        } else {
                            toastErrorMessage()
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(164, 30, 53)
            )
        ) {
            Text(text = "Sign in")
        }

        // Navigation to SignUp
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Already have an account?",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Login",
                fontSize = 14.sp,
                color = Color(164, 30, 53),
                modifier = Modifier.clickable { navigateToSignUpActivity() }
            )
        }
    }
}
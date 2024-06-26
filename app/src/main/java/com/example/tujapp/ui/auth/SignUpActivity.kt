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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tujapp.MainActivity
import com.example.tujapp.data.User
import com.example.tujapp.ui.theme.TujAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TujAppTheme {
                SignUpScreen(
                    onSignUpClicked = { name, email, password ->
                        signUpWithEmailAndPassword(name, email, password)
                    },
                    navigateToSignInActivity = {
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }

    private fun signUpWithEmailAndPassword(name: String, email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    Toast.makeText(baseContext, "Successfully registered!", Toast.LENGTH_SHORT).show()

                    val databaseRef = Firebase.database.reference
                    val newUser = User(
                        uid = user?.uid,
                        email = user?.email.toString(),
                        name = name,
                        //graduationYear = 0,
                        contact = "",
                        bio = "",
                    )
                    databaseRef.child("users").child(user!!.uid).setValue(newUser)

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(baseContext, "Failed to sign up", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

@Composable
fun SignUpScreen(
    onSignUpClicked: (String, String, String) -> Unit,
    navigateToSignInActivity: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sign up", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Input name
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(text = "Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Input email address
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Input password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sign up button
        Button(
            onClick = { onSignUpClicked(name, email, password) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(164, 30, 53)
            )
        ) {
            Text("Sign up")
        }

        // Navigation to SignIn
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
                modifier = Modifier.clickable { navigateToSignInActivity() }
            )
        }
    }
}
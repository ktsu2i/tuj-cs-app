package com.example.tujapp.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.tujapp.data.User

@Composable
fun ContactScreen(
    currentUser: User?
) {
    Text(text = "This is a contact screen")
}
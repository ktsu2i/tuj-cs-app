package com.example.tujapp.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.tujapp.R
import com.example.tujapp.data.Post
import com.example.tujapp.data.Reply
import com.example.tujapp.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen (
    userId: String,
    navController: NavController
) {
    val userData = remember { mutableStateOf<User?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) {
        val databaseRef = Firebase.database.reference

        // fetch the author (user who posted it)
        val userRef = databaseRef.child("users").child(userId)

        userRef.get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.getValue(User::class.java)
            userData.value = user

            // fetch the user profile image
            val storageRef = Firebase.storage.reference.child("users/${userId}/profile.jpg")

            storageRef.downloadUrl.addOnSuccessListener { uri ->
                imageUri = uri
            }
        }.addOnFailureListener {
            // if failed to fetch the current user
        }
    }

    Scaffold { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){
            Spacer(modifier = Modifier.height(4.dp))

            Image(
                painter = if (imageUri == null) painterResource(id = R.drawable.user_profile_icon) else rememberImagePainter(imageUri.toString()),
                contentDescription = "profile image",
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = userData.value?.name ?: "Name", style = MaterialTheme.typography.headlineSmall)
            Text(text = userData.value?.email ?: "Email", style = MaterialTheme.typography.bodySmall)
        }
    }
}
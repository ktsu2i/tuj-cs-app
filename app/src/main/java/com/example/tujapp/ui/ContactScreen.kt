package com.example.tujapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tujapp.R
import com.example.tujapp.data.Internship
import com.example.tujapp.data.User
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow



@Composable
fun ContactScreen(
    currentUser: User?,
    navController: NavController
) {
    val usersFlow = remember { MutableStateFlow<List<User>>(emptyList()) }
    val searchQuery = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        getAllUsers(usersFlow)
    }

    val users = usersFlow.collectAsState().value
        .filter { user ->
            user.name?.lowercase()?.contains(searchQuery.value.lowercase()) ?: false
        }
        .sortedBy { it.name?.lowercase() }

    Scaffold { innerPadding ->
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding)
        ) {
            // search bar
            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = { searchQuery.value = it },
                label = {
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Search, contentDescription = "search", Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Search")
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            // displays a list of posts
            LazyColumn {
                items(users) { user ->
                    UserItem(user = user, navController = navController)
                }
            }
        }
    }
}


fun getAllUsers(
    usersFlow: MutableStateFlow<List<User>>
) {
    val databaseRef = Firebase.database.reference

    databaseRef.child("users").addValueEventListener(object: ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val users = snapshot.children.mapNotNull { it.getValue(User::class.java) }
            usersFlow.value = users
        }

        override fun onCancelled(error: DatabaseError) {
            // error handling
        }
    })
}

@Composable
fun UserItem(
    user: User,
    navController: NavController,
) {
    Card (
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RectangleShape,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column (
            modifier = Modifier.padding(16.dp)
        ) {
            user.let { user ->
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(
                            id = user.profileImageId ?: R.drawable.user_profile_icon
                        ),
                        contentDescription = "user profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(30.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = user.name.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    OutlinedButton(
                        onClick = {
                            // Navigate to a screen for their profile
                            navController.navigate("contacts/${user.uid}")
                        }
                    ) {
                        Text(text = "View Profile", color = Color(164, 30, 53),)
                    }
                }
            }
        }
    }
}
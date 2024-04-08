package com.example.tujapp.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.tujapp.R
import com.example.tujapp.data.Internship
import com.example.tujapp.data.Post
import com.example.tujapp.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun InternshipScreen(
    currentUser: User?
) {
    val internshipsFlow = remember { MutableStateFlow<List<Internship>>(emptyList()) }

    LaunchedEffect(Unit) {
        getAllInternships(internshipsFlow)
    }

    val internships by internshipsFlow.collectAsState()
    var newInternshipDescription by remember { mutableStateOf("") }
    var newInternshipTitle by remember { mutableStateOf("") }
    var newInternshipLink by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold (
        floatingActionButton = {
            FloatingActionButton (
                onClick = { showDialog = true },
                containerColor = Color(164, 30, 53),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Create, contentDescription = "Internship Post")
            }
        }
    ) { innerPadding ->
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding)
        ) {
            // displays a list of posts
            LazyColumn {
                items(internships.reversed()) { internship ->
                    InternshipItem(internship = internship)
                }
            }

            // floating action button
            if (showDialog) {
                AlertDialog (
                    containerColor = Color.White,
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "New Internship Post") },
                    text = {
                        Column {
                            OutlinedTextField (
                                value = newInternshipTitle,
                                onValueChange = { newInternshipTitle = it },
                                label = { Text(text = "Title...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField (
                                value = newInternshipLink,
                                onValueChange = { newInternshipLink = it },
                                label = { Text(text = "Link to Internship...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField (
                                value = newInternshipDescription,
                                onValueChange = { newInternshipDescription = it },
                                label = { Text(text = "Description...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        TextButton (
                            onClick = {
                                // add a post
                                if (newInternshipDescription.isNotEmpty() && newInternshipTitle.isNotEmpty() && currentUser != null) {
                                    addInternship(currentUser.uid.toString(), newInternshipDescription, newInternshipTitle, newInternshipLink)
                                    newInternshipDescription = ""
                                    newInternshipTitle = ""
                                    newInternshipLink = ""
                                    showDialog = false
                                }
                            }
                        ) {
                            Text(
                                text = "Post",
                                color = Color(164, 30, 53)
                            )
                        }
                    },
                    dismissButton = {
                        TextButton (onClick = { showDialog = false }) {
                            Text(
                                text = "Cancel",
                                color = Color.Black
                            )
                        }
                    }
                )
            }
        }
    }
}


fun getAllInternships(
    internshipsFlow: MutableStateFlow<List<Internship>>
) {
    val databaseRef = Firebase.database.reference

    databaseRef.child("internships").addValueEventListener(object: ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val internships = snapshot.children.mapNotNull { it.getValue(Internship::class.java) }
            internshipsFlow.value = internships
        }

        override fun onCancelled(error: DatabaseError) {
            // error handling
        }
    })
}


fun addInternship(
    userId: String,
    content: String,
    title: String,
    link: String
) {
    val databaseRef = Firebase.database.reference
    val newInternshipRef = databaseRef.child("internships").push()
    val newInternship = Internship(userId, content, title, link)
    newInternshipRef.setValue(newInternship)
}


@Composable
fun InternshipItem(
    internship: Internship
) {
    val userData = remember { mutableStateOf<User?>(null) }

    LaunchedEffect (internship.userId) {
        val databaseRef = Firebase.database.reference
        val userRef = databaseRef.child("users").child(internship.userId)

        userRef.get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.getValue(User::class.java)
            userData.value = user
        }.addOnFailureListener {
            // if failed to fetch the current user
        }
    }

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
            userData.value?.let { user ->
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

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = userData.value?.name.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = internship.title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(6.dp))


            val localUriHandler = LocalUriHandler.current
            ClickableText(
                text = AnnotatedString(internship.link),
                style = MaterialTheme.typography.bodyMedium
            )
            {
                localUriHandler.openUri(internship.link)
            }
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = internship.description,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

//            Row (
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(
//                    onClick = {
//                        // todo
//                    }
//                ) {
//                    Icon(Icons.Default.Favorite, contentDescription = "Like")
//
//                    Text(text = "${post.likes} likes")
//
//                    Spacer(modifier = Modifier.width(8.dp))
//
//                    TextButton(
//                        onClick = {
//                            // todo
//                        }
//                    ) {
//                        Text("Reply")
//                    }
//                }
//
//                post.replies.forEach { reply ->
//                    Text(
//                        text = "${reply.userId}: ${reply.content}",
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                }
//            }
        }
    }
}
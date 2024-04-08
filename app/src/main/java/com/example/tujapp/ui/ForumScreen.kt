package com.example.tujapp.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tujapp.R
import com.example.tujapp.data.Post
import com.example.tujapp.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun ForumScreen(
    currentUser: User?
) {
    val postsFlow = remember { MutableStateFlow<List<Post>>(emptyList()) }

    LaunchedEffect(Unit) {
        getAllPosts(postsFlow)
    }

    val posts by postsFlow.collectAsState()
    var newPostContent by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }

    Scaffold (
        floatingActionButton = {
            FloatingActionButton (
                onClick = { showDialog = true },
                containerColor = Color(164, 30, 53),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Create, contentDescription = "Post")
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
                items(posts.reversed()) { post ->
                    PostItem(post = post)
                }
            }

            // floating action button
            if (showDialog) {
                AlertDialog (
                    containerColor = Color.White,
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "New Post") },
                    text = {
                        OutlinedTextField (
                            value = newPostContent, 
                            onValueChange = { newPostContent = it },
                            label = { Text(text = "Write something...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        TextButton (
                            onClick = {
                                // add a post
                                if (newPostContent.isNotEmpty() && currentUser != null) {
                                    addPost(currentUser.uid.toString(), newPostContent)
                                    newPostContent = ""
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


fun getAllPosts(
    postsFlow: MutableStateFlow<List<Post>>
) {
    val databaseRef = Firebase.database.reference
    
    databaseRef.child("posts").addValueEventListener(object: ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val posts = snapshot.children.mapNotNull { it.getValue(Post::class.java) }
            postsFlow.value = posts
        }

        override fun onCancelled(error: DatabaseError) {
            // error handling
        }
    })
}


fun addPost(
    userId: String,
    content: String,
) {
    val databaseRef = Firebase.database.reference
    val newPostRef = databaseRef.child("posts").push()
    val newPost = Post(userId, content)
    newPostRef.setValue(newPost)
}


@Composable
fun PostItem(
    post: Post
) {
    val userData = remember { mutableStateOf<User?>(null) }

    LaunchedEffect (post.userId) {
        val databaseRef = Firebase.database.reference
        val userRef = databaseRef.child("users").child(post.userId)

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
                text = post.content,
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
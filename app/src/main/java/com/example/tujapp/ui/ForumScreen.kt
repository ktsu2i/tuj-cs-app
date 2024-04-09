package com.example.tujapp.ui

import android.net.Uri
import android.text.format.DateUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Send
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.tujapp.R
import com.example.tujapp.data.Post
import com.example.tujapp.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun ForumScreen(
    currentUser: User?,
    navController: NavController
) {
    val postsFlow = remember { MutableStateFlow<List<Post>>(emptyList()) }

    LaunchedEffect(Unit) {
        getAllPosts(postsFlow)
    }

    val posts by postsFlow.collectAsState()
    var newPostTitle by remember { mutableStateOf("") }
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
                    PostItem(post = post, currentUserId = currentUser?.uid.toString(), navController)
                }
            }

            // floating action button
            if (showDialog) {
                AlertDialog (
                    containerColor = Color.White,
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "New Post") },
                    text = {
                        Column {
                            OutlinedTextField (
                                value = newPostTitle,
                                onValueChange = { newPostTitle = it },
                                label = { Text(text = "Post title") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField (
                                value = newPostContent,
                                onValueChange = { newPostContent = it },
                                label = { Text(text = "Write something...") },
                                modifier = Modifier.fillMaxWidth()
                            )       
                        }
                    },
                    confirmButton = {
                        TextButton (
                            onClick = {
                                // add a post
                                if (newPostContent.isNotEmpty() && currentUser != null) {
                                    addPost(currentUser.uid.toString(), newPostTitle, newPostContent)
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
    title: String,
    content: String,
) {
    val databaseRef = Firebase.database.reference
    val newPostRef = databaseRef.child("posts").push()
    val newPostId = newPostRef.key
    val newPost = Post(newPostId, userId, title, content)
    newPostRef.setValue(newPost)
}


fun toggleLike(
    postId: String,
    userId: String,
) {
    val databaseRef = Firebase.database.reference
    val likesRef = databaseRef.child("likes").child(postId).child(userId)

    likesRef.addListenerForSingleValueEvent(object: ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                likesRef.removeValue()
            } else {
                likesRef.setValue(true)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // error handling
        }
    })
}


fun formatDate(
    createdAt: Long?,
): String {
    if (createdAt == null) {
        return "N/A"
    }

    return DateUtils.getRelativeTimeSpanString(
        createdAt,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS
    ).toString()
}

@Composable
fun PostItem(
    post: Post,
    currentUserId: String,
    navController: NavController
) {
    val userData = remember { mutableStateOf<User?>(null) }
    var likedByUser by remember { mutableStateOf(false) }
    var likesCount by remember { mutableStateOf(0) }
    var repliesCount by remember { mutableStateOf(0) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect (post.userId, post.postId) {
        val databaseRef = Firebase.database.reference

        // fetch the author (user who posted it)
        val userRef = databaseRef.child("users").child(post.userId)

        userRef.get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.getValue(User::class.java)
            userData.value = user

            // fetch the user profile image
            val storageRef = Firebase.storage.reference.child("users/${userData.value?.uid.toString()}/profile.jpg")

            storageRef.downloadUrl.addOnSuccessListener { uri ->
                imageUri = uri
            }
        }.addOnFailureListener {
            // if failed to fetch the current user
        }

        // fetch the like boolean (whether the current user liked the post or not)
        val likesRef = databaseRef.child("likes").child(post.postId.toString())

        likesRef.child(currentUserId).get().addOnSuccessListener { dataSnapshot ->
            likedByUser = dataSnapshot.exists()
        }

        // fetch the likes count
        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                likesCount = snapshot.childrenCount.toInt()
            }

            override fun onCancelled(error: DatabaseError) {
                // error handling
                likesCount = 0
            }
        })

        // fetch the replies count
        val repliesRef = databaseRef.child("posts").child(post.postId.toString()).child("replies")

        repliesRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                repliesCount = snapshot.childrenCount.toInt()
            }

            override fun onCancelled(error: DatabaseError) {
                // error handling
                repliesCount = 0
            }
        })
    }

    Card (
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("post/${post.postId}")
            },
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
                        painter = if (imageUri == null) painterResource(id = R.drawable.user_profile_icon) else rememberImagePainter(imageUri.toString()),
                        contentDescription = "user profile",
                        modifier = Modifier.size(30.dp).clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Column {
                        Text(
                            text = userData.value?.name.toString(),
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = formatDate(post.createdAt),
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${post.title}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row (
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = {
                        toggleLike(post.postId.toString(), currentUserId)
                        likedByUser = !likedByUser
                    }
                ) {
                    Icon(
                        imageVector = if (likedByUser) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (likedByUser) Color.Red else Color.Gray,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Text(
                    text = "$likesCount",
                    color = Color.Gray,
                    style = TextStyle(fontSize = 16.sp)
                )

                IconButton(
                    onClick = {
                        // todo
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        tint = Color.Gray,
                        contentDescription = "Reply",
                        modifier = Modifier.size(22.dp)
                    )
                }

                Text(
                    text = "$repliesCount",
                    color = Color.Gray,
                    style = TextStyle(fontSize = 16.sp)
                )
            }
        }
    }
}
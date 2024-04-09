package com.example.tujapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen (
    currentUser: User?,
    postId: String,
    navController: NavController
) {
    var replyContent by remember { mutableStateOf("") }

    var likedByUser by remember { mutableStateOf(false) }
    var likesCount by remember { mutableStateOf(0) }

    val userData = remember { mutableStateOf<User?>(null) }
    val postData = remember { mutableStateOf<Post?>(null) }

    LaunchedEffect (postId) {
        val databaseRef = Firebase.database.reference

        // fetch post data and user data
        val postRef = databaseRef.child("posts").child(postId)
        val usersRef = databaseRef.child("users")

        postRef.get().addOnSuccessListener { dataSnapshot ->
            val post = dataSnapshot.getValue(Post::class.java)
            postData.value = post

            post?.userId?.let { userId ->
                usersRef.child(userId).get().addOnSuccessListener { userSnapshot ->
                    val user = userSnapshot.getValue(User::class.java)
                    userData.value = user
                }.addOnFailureListener {
                    // error handling
                }
            }
        }.addOnFailureListener {
            // if failed to fetch the post
        }

        // fetch the like boolean (whether the current user liked the post or not)
        val likesRef = databaseRef.child("likes").child(postData.value?.postId.toString())

        likesRef.child(currentUser?.uid.toString()).get().addOnSuccessListener { dataSnapshot ->
            likedByUser = dataSnapshot.exists()
        }

        // fetch the likes count
        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                likesCount = snapshot.childrenCount.toInt()
            }

            override fun onCancelled(error: DatabaseError) {
                // error handling
            }
        })
    }

    Scaffold (
        bottomBar = {
            BottomAppBar (
                containerColor = Color.White,
                modifier = Modifier.height(70.dp)
            ) {
                Image (
                    painter = painterResource(
                        id = currentUser?.profileImageId ?: R.drawable.user_profile_icon
                    ),
                    contentDescription = "user profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(40.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))

                OutlinedTextField (
                    value = replyContent,
                    onValueChange = { replyContent = it },
                    placeholder = { Text("Post your reply") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp),
                    singleLine = true
                )

                Button (
                    onClick = {
                        if (replyContent.isNotEmpty()) {
                            val newReply = Reply(currentUser?.uid.toString(), replyContent)
                            addReply(postId, newReply)
                            replyContent = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(164, 30, 53)
                    ),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Reply")
                }
            }
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
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
                    currentUser?.let { user ->
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(
                                    id = user.profileImageId ?: R.drawable.user_profile_icon
                                ),
                                contentDescription = "user profile",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(25.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Column {
                                Text(
                                    text = userData.value?.name.toString(),
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Text(
                                    text = formatDate(postData.value?.createdAt),
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${postData.value?.title}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${postData.value?.content}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                toggleLike(postId, currentUser?.uid.toString())
                                likedByUser = !likedByUser
                            }
                        ) {
                            Icon(
                                imageVector = if (likedByUser) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (likedByUser) Color.Red else Color.Gray
                            )
                        }

                        Text(text = "$likesCount")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(1.dp))

            RepliesList(postId = postId)
        }
    }
}


fun addReply(
    postId: String,
    reply: Reply,
) {
    val databaseRef = Firebase.database.reference
    val repliesRef = databaseRef.child("posts").child(postId).child("replies").push()
    repliesRef.setValue(reply)
}


@Composable
fun RepliesList(
    postId: String
) {
    val replies = remember { mutableStateListOf<Reply>() }

    LaunchedEffect(postId) {
        val repliesRef = Firebase.database.reference.child("posts").child(postId).child("replies")

        repliesRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                replies.clear()
                for (replySnapshot in snapshot.children) {
                    val reply = replySnapshot.getValue(Reply::class.java)
                    reply?.let { replies.add(it) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // error handling
            }
        })
    }

    LazyColumn {
        items(replies) { reply ->
            ReplyItem(reply = reply)
        }
    }
}


@Composable
fun ReplyItem(
    reply: Reply
) {
    val userData = remember { mutableStateOf<User?>(null) }

    LaunchedEffect(reply.userId) {
        val databaseRef = com.google.firebase.Firebase.database.reference
        val userRef = databaseRef.child("users").child(reply.userId)

        userRef.get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.getValue(User::class.java)
            userData.value = user
        }.addOnFailureListener {
            // if failed to fetch the current user
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RectangleShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(
                        id = userData.value?.profileImageId ?: R.drawable.user_profile_icon
                    ),
                    contentDescription = "user profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(25.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = userData.value?.name.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))

            Text(text = reply.content, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
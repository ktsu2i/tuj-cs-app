package com.example.tujapp.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.tujapp.R
import com.example.tujapp.data.DataSource
import com.example.tujapp.data.User
import com.example.tujapp.model.profilePic
import com.example.tujapp.ui.auth.SignInActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

import com.google.firebase.storage.ktx.storage


@Composable
fun ProfileScreen(
    currentUser: User?,
    navController: NavController
) {
    val context = LocalContext.current

    var currentUserData by remember { mutableStateOf<User?>(currentUser) }
    var showDialog by remember { mutableStateOf(false) }
    var newImageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(currentUserData?.name) {
        val currentUserRef = Firebase.database.reference.child("users").child(currentUserData?.uid.toString())
        val storageRef = Firebase.storage.reference.child("users/${currentUserData?.uid.toString()}/profile.jpg")

        currentUserRef.get().addOnSuccessListener { dataSnapshot ->
            val currentUser = dataSnapshot.getValue(User::class.java)
            currentUserData = currentUser
        }.addOnFailureListener {
            // error handling
        }

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            newImageUri = uri
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        newImageUri = uri
        if (newImageUri != null)
        {
            uploadImageToFirebase(currentUserData?.uid.toString(), newImageUri, {})
            updateProfileWithImage(currentUserData?.uid.toString(), currentUserData?.name.toString(), "", currentUserData?.bio.toString(), currentUserData?.contact.toString()){}

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
            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = if (newImageUri == null) painterResource(id = R.drawable.user_profile_icon) else rememberImagePainter(newImageUri.toString()),
                contentDescription = "profile image",
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = currentUserData?.name ?: "Name", style = MaterialTheme.typography.headlineSmall)
            Text(text = currentUserData?.email ?: "Email", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = currentUserData?.contact ?: "Edit to add contact methods", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = currentUserData?.bio ?: "Edit to add Bio", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(164, 30, 53)
                    )
                ) {
                    Text(text = "Edit Profile")
                }

                Spacer(modifier = Modifier.width(4.dp))

                Button(
                    onClick = { pickImageLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(164, 30, 53)
                    )
                ) {
                    Text(text = "Update Icon")
                }
            }

            if (showDialog) {
                EditProfileDialog(
                    currentUser = currentUserData,
                    onDismiss = { showDialog = false },
                    onUpdateSuccess = { updatedUser ->
                        currentUserData = updatedUser
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    Firebase.auth.signOut()

                    val intent = Intent(context, SignInActivity::class.java)
                    context.startActivity(intent)

                    (context as? Activity)?.finish()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray
                )
            ) {
                Text(text = "Log out")
            }
        }
    }
}


@Composable
fun EditProfileDialog(
    currentUser: User?,
    onDismiss: () -> Unit,
    onUpdateSuccess: (User) -> Unit
) {
    var newUserName by remember { mutableStateOf(currentUser?.name ?: "") }
//    var newImageUri by remember { mutableStateOf<Uri?>(null) }
    var newUserBio by remember { mutableStateOf(currentUser?.bio ?: "") }
    var newUserContactMethods by remember { mutableStateOf(currentUser?.contact ?: "") }
    Scaffold { innerPadding ->
        AlertDialog (
            containerColor = Color.White,
            onDismissRequest = onDismiss,
            title = { Text(text = "Edit Profile") },
            text = {
                Column {
                    OutlinedTextField (
                        value = newUserName,
                        onValueChange = { newUserName = it },
                        label = { Text(text = "Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField (
                        value = newUserContactMethods,
                        onValueChange = { newUserContactMethods = it },
                        label = { Text(text = "Other Contact Methods") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                    OutlinedTextField (
                        value = newUserBio,
                        onValueChange = { newUserBio = it },
                        label = { Text(text = "Bio") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )

                }
            },
            confirmButton = {
                TextButton (
                    onClick = {
                        // update username
                        if (newUserName.isNotEmpty() && currentUser != null) {
                            updateProfileWithImage(currentUser.uid.toString(), newUserName, "", newUserBio, newUserContactMethods) {
                                onUpdateSuccess(User(currentUser.uid, currentUser.email, newUserName, newUserBio, newUserContactMethods))
                            }
                            onDismiss()

                            newUserName = ""
                        }
                    }
                ) {
                    Text(
                        text = "Edit",
                        color = Color(164, 30, 53)
                    )
                }
            },
            dismissButton = {
                TextButton (onClick = onDismiss) {
                    Text(
                        text = "Cancel",
                        color = Color.Black
                    )
                }
            }
        )
    }
}


fun updateProfileWithImage(
    userId: String,
    userName: String,
    imageUrl: String,
    userBio: String,
    userContacts: String,
    onSuccess: (User) -> Unit,
) {
    val userRef = Firebase.database.reference.child("users").child(userId)
    val userUpdates = mapOf(
        "name" to userName,
        "imageUrl" to imageUrl,
        "bio" to userBio,
        "contact" to userContacts,
    )

    userRef.updateChildren(userUpdates).addOnSuccessListener {
        val updatedUser = User(userId, userName, imageUrl, userBio, userContacts)
        onSuccess(updatedUser)
    }
}


fun uploadImageToFirebase(
    userId: String,
    imageUri: Uri?,
    onSuccess: (String) -> Unit
) {
    val storageRef = Firebase.storage.reference.child("users/${userId}/profile.jpg")
    val imageRef = storageRef.child("images")
    val userRef = Firebase.database.reference.child("users").child(userId)

    storageRef.putFile(imageUri!!).addOnSuccessListener { taskSnapshot ->
        taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
            onSuccess(uri.toString())
        }
    }

//    userRef.updateChildren(mapOf("imageUrl" to imageUri.toString()))
}
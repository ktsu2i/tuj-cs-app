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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.tujapp.R
import com.example.tujapp.data.DataSource
import com.example.tujapp.data.User
import com.example.tujapp.model.profilePic
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage


@Composable
fun ProfileScreen(
    currentUser: User?
) {
    val context = LocalContext.current
    //var selectedImageUri: MutableState<Uri?> = remember{ mutableStateOf(null)}
    fun uploadImageToFirebaseStorage(imageUri: Uri){
        val storage= Firebase.storage
        val storageReference = storage.reference
        val imageName = "${System.currentTimeMillis()}.jpg"
        val imageRef = storageReference.child("images/$imageName")
        imageRef.putFile(imageUri).addOnSuccessListener {
                _ -> imageRef.downloadUrl.addOnSuccessListener { Toast.makeText(context, "image upload successful",
            Toast.LENGTH_SHORT).show()
        }}
            .addOnFailureListener{ Toast.makeText(context, "image upload failed",
                Toast.LENGTH_SHORT).show()}

    }
    val getContent = rememberLauncherForActivityResult(ActivityResultContracts.GetContent())
    {
            uri: Uri? ->
                uri?.let { uploadImageToFirebaseStorage(it) }
                Toast.makeText(context, "image selected", Toast.LENGTH_SHORT).show()
        //selectedImageUri.value = uri
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        var profilePicsList:List<profilePic> = DataSource().loadProfilePic()
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "U s e r   P r o f i l e", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))
        var profile = profilePicsList[0]
        Image(
            painter = painterResource(id = profile.imageResourceId),
            contentDescription = stringResource(id = profile.stringResourceId),
            modifier = Modifier
                //.fillMaxWidth()
                .height(150.dp)
                .width(150.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Red, CircleShape),
            contentScale = ContentScale.Crop
        )

        Button(
            onClick = {
                // Open gallery to select an image
                getContent.launch("image/*")
                      //openImagePicker()
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Select & Upload Image")
        }

        /*        if (currentUser != null) {
                    OutlinedCard {
                        Text(
                            text = currentUser.name!!,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = currentUser.bio!!,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = currentUser.contact!!,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = currentUser.graduationYear!!.toString(),
                            style = MaterialTheme.typography.titleLarge
                        )

                    }
                }
                Card {
                    Row {
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text(text = "Search Other Users") },
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Button(onClick = { /*TODO*/ }) {
                            Text(text = "Go")
                        }
                    }
                }
        */
    }
}

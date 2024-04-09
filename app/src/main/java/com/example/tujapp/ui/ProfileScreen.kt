package com.example.tujapp.ui

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
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
import com.example.tujapp.data.DataSource
import com.example.tujapp.data.User
import com.example.tujapp.model.profilePic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


@Composable
fun ProfileScreen(
    currentUser: User?
) {
    val context = LocalContext.current
    var selectedImageUri: MutableState<Uri?> = remember{ mutableStateOf(null)}
    val getContent = rememberLauncherForActivityResult(ActivityResultContracts.GetContent())
    {
        uri:Uri?->
        selectedImageUri.value = uri
    }
    //Text(text = "This is a profile screen")
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
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Select Image")
        }
        Button(
            onClick = {
                // Upload image to Firebase or any other storage service
                selectedImageUri.value?.let { uri ->
                    uploadImageToFirebase(context, uri)
                        //Toast.makeText(context, uri.toString(), Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Upload Image")
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
private fun uploadImageToFirebase(thisContext: Context, imageUri: Uri) {
    // Implement your logic to upload the image to Firebase or any other storage service here
    // You can use Firebase Storage to upload the image

    var storageRef = FirebaseStorage.getInstance()
    storageRef.getReference("images").child(System.currentTimeMillis().toString())
        .putFile(imageUri).
    addOnSuccessListener { task ->
        task.metadata!!.reference!!.downloadUrl
            .addOnSuccessListener {
                Toast.makeText(thisContext, "Successful 1", Toast.LENGTH_SHORT).show()
                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                val mapImage = mapOf(
                    "url" to it.toString()
                )
                val databaseReference =
                    FirebaseDatabase.getInstance().getReference("userImages")
                databaseReference.child(userId).setValue(mapImage)
                    .addOnSuccessListener {
                        Toast.makeText(thisContext, "Successful 2", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(thisContext, it.toString(), Toast.LENGTH_SHORT).show()
                    }


            }.addOnFailureListener{
                Toast.makeText(thisContext, "failure", Toast.LENGTH_SHORT).show()
            }
    }.addOnFailureListener{
            Toast.makeText(thisContext, "failure 2", Toast.LENGTH_SHORT).show()
        }
}
package com.example.tujapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.tujapp.data.DataSource
import com.example.tujapp.data.User
import com.example.tujapp.model.profilePic

@Composable
fun ProfileScreen(
    currentUser: User?
) {
    //Text(text = "This is a profile screen")
    Column(
        modifier = Modifier.fillMaxSize(),
        //verticalArrangement = Arrangement.Center,
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
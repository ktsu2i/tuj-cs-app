package com.example.tujapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    Column(){
        var profilePicsList:List<profilePic> = DataSource().loadProfilePic()
        Text(text = "User Profile")
        var profile = profilePicsList[0]
        Image(
            painter = painterResource(id = profile.imageResourceId),
            contentDescription = stringResource(id = profile.stringResourceId),
            modifier = Modifier
                //.fillMaxWidth()
                .height(194.dp)
                .width(194.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}
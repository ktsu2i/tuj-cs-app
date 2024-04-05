package com.example.tujapp.data
import com.example.tujapp.R
import com.example.tujapp.model.profilePic
class DataSource{
    fun loadProfilePic(): List<profilePic>{
        return listOf<profilePic>(
            profilePic(R.string.q1,R.string.a1, R.drawable.img1),
            profilePic(R.string.q2,R.string.a2, R.drawable.img2)
        )
    }
}

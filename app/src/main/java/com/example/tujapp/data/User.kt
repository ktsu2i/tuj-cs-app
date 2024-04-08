package com.example.tujapp.data

import com.example.tujapp.R

data class User (
    val uid: String? = null,
    val email: String? = null,
    val name: String? = null,
    //val bio: String? = null,
    //val contact: String? = null,
//    val graduationSemester: String,
    //val graduationYear: Int,
    val profileImageId: Int = R.drawable.user_profile_icon,
)
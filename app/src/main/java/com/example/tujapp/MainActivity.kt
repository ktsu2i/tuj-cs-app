package com.example.tujapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberImagePainter
import com.example.tujapp.data.User
import com.example.tujapp.ui.ContactScreen
import com.example.tujapp.ui.ForumScreen
import com.example.tujapp.ui.InternshipScreen
import com.example.tujapp.ui.PostScreen
import com.example.tujapp.ui.ProfileScreen
import com.example.tujapp.ui.UserScreen
import com.example.tujapp.ui.auth.SignUpScreen
import com.example.tujapp.ui.theme.TujAppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // fetch the current user id (uid from firebase)
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        setContent {
            TujAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TujApp(currentUserId.toString())
                }
            }
        }
    }
}

sealed class BottomNavItem(var title: String, var unselectedIcon: Int, var selectedIcon: Int, var route: String) {
    data object Forum: BottomNavItem("Forum", R.drawable.unselected_forum_logo, R.drawable.forum_icon, "forum")
    data object Internship: BottomNavItem("Internships", R.drawable.unselected_job_logo, R.drawable.job_logo, "internships")
    data object Contact: BottomNavItem("Contact", R.drawable.unselected_contact_logo, R.drawable.contact_logo, "contact")
    data object Profile: BottomNavItem("Profile", R.drawable.unselected_user_profile_logo, R.drawable.user_profile_icon, "profile")
}


@Composable
fun BottomNavBar (navController: NavController) {
    var selectedItem by remember { mutableIntStateOf(0) }

    val items = listOf(
        BottomNavItem.Forum,
        BottomNavItem.Internship,
        BottomNavItem.Contact,
        BottomNavItem.Profile,
    )

    NavigationBar(
        containerColor = Color.White,
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                label = { Text(
                    text = item.title,
//                    style = TextStyle(fontWeight = if (selectedItem == index) FontWeight.Bold else FontWeight.Normal)
                    ) },
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                            selectedItem = index
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.White,
//                    selectedIconColor = Color(164, 30, 53),
//                    selectedTextColor = Color(164, 30, 53),
//                    unselectedIconColor = Color(164, 30, 53),
//                    unselectedTextColor = Color(164, 30, 53),
                ),
                icon = { Icon(
                    painter = painterResource(if (selectedItem == index) item.selectedIcon else item.unselectedIcon),
                    contentDescription = item.title,
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TujApp(
    currentUserId: String
) {
    val navController = rememberNavController()

    // fetch the current user
    val currentUserData = remember { mutableStateOf<User?>(null) }

    LaunchedEffect(currentUserId) {
        val databaseRef = Firebase.database.reference
        val currentUserRef = databaseRef.child("users").child(currentUserId)

        currentUserRef.get().addOnSuccessListener { dataSnapshot ->
            val currentUser = dataSnapshot.getValue(User::class.java)
            currentUserData.value = currentUser
        }.addOnFailureListener {
            // if failed to fetch the current user
        }
    }

    var showBackButton by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                    ),
                    title = {},
                    navigationIcon = {
                        if (showBackButton) {
                            IconButton(
                                onClick = {
                                    navController.navigateUp()
                                    showBackButton = false
                                }
                            ) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "back")
                            }
                        } else { null }
                    },
                    actions = {
                        Row (
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo2),
                                contentDescription = "logo",
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                )

                Divider(color = Color.Gray, thickness = 0.5.dp)
            }
        },
        bottomBar = {
            Column {
                Divider(color = Color.Gray, thickness = 0.5.dp)

                BottomNavBar(navController = navController)
            }
        }
    ) { interPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Forum.route,
            modifier = Modifier.padding(interPadding)
        ) {
            composable(BottomNavItem.Forum.route) {
                showBackButton = false
                ForumScreen(currentUserData.value, navController)
            }

            composable("post/{postId}", arguments = listOf(navArgument("postId") { type = NavType.StringType })) { navBackStackEntry ->
                showBackButton = true
                PostScreen(currentUser = currentUserData.value, postId = navBackStackEntry.arguments?.getString("postId") ?: "", navController)
            }

            composable(BottomNavItem.Internship.route) {
                showBackButton = false
                InternshipScreen(currentUserData.value)
            }

            composable(BottomNavItem.Contact.route) {
                showBackButton = false
                ContactScreen(currentUserData.value, navController)
            }

            composable("contacts/{userId}", arguments = listOf(navArgument("userId") { type = NavType.StringType })) { navBackStackEntry ->
                showBackButton = true
                UserScreen(userId = navBackStackEntry.arguments?.getString("userId") ?: "", navController = navController)
            }

            composable(BottomNavItem.Profile.route) {
                showBackButton = false
                ProfileScreen(currentUserData.value, navController)
            }
        }
    }
}


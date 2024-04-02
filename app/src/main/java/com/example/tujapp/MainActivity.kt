package com.example.tujapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tujapp.ui.ContactScreen
import com.example.tujapp.ui.ForumScreen
import com.example.tujapp.ui.InternshipScreen
import com.example.tujapp.ui.ProfileScreen
import com.example.tujapp.ui.ProjectScreen
import com.example.tujapp.ui.theme.TujAppTheme
import com.google.firebase.database.ktx.database

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TujAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TujApp()
                }
            }
        }
        val database = com.google.firebase.ktx.Firebase.database
        val myRef = database.getReference("kaito")

        myRef.setValue("just testing")
    }
}

sealed class BottomNavItem(var title: String, var icon: Int, var route: String) {
    data object Forum: BottomNavItem("Forum", R.drawable.forum_icon, "forum")
    data object Project: BottomNavItem("Projects", R.drawable.project_logo, "projects")
    data object Internship: BottomNavItem("Internship", R.drawable.internship_logo, "internship")
    data object Contact: BottomNavItem("Contact", R.drawable.contact_logo, "contact")
    data object Profile: BottomNavItem("Profile", R.drawable.user_profile_icon, "profile")
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = BottomNavItem.Forum.route) {
        composable(BottomNavItem.Forum.route) {
            ForumScreen()
        }

        composable(BottomNavItem.Project.route) {
            ProjectScreen()
        }

        composable(BottomNavItem.Internship.route) {
            InternshipScreen()
        }

        composable(BottomNavItem.Contact.route) {
            ContactScreen()
        }

        composable(BottomNavItem.Profile.route) {
            ProfileScreen()
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    var selectedItem by remember { mutableIntStateOf(0) }

    val items = listOf(
        BottomNavItem.Forum,
        BottomNavItem.Project,
        BottomNavItem.Internship,
        BottomNavItem.Contact,
        BottomNavItem.Profile,
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                label = { Text(text = item.title) },
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
                    selectedIconColor = Color(164, 30, 53),
                    selectedTextColor = Color(164, 30, 53),
                    unselectedIconColor = Color(164, 30, 53),
                    unselectedTextColor = Color(164, 30, 53),
                ),
                icon = { Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = item.title,
                ) }
            )
        }
    }
}

@Composable
fun TujApp() {
    val navController = rememberNavController()

    Text(text = "This is a tuj app")

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) }
    ) { interPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Forum.route,
            modifier = Modifier.padding(interPadding)
        ) {
            composable(BottomNavItem.Forum.route) {
                ForumScreen()
            }

            composable(BottomNavItem.Project.route) {
                ProjectScreen()
            }

            composable(BottomNavItem.Internship.route) {
                InternshipScreen()
            }

            composable(BottomNavItem.Contact.route) {
                ContactScreen()
            }

            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
            }
        }
    }
}


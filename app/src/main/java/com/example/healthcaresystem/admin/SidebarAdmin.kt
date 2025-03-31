package com.example.healthcaresystem.admin

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.healthcaresystem.R
import com.example.healthcaresystem.user.home.model.SidebarItem
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(
    sidebarItem: List<SidebarItem>, // List of sidebar items
    navController: NavController,
    modifier: Modifier = Modifier,
    closedrawer: ()-> Unit
) {
    // Track the currently selected item
    val currentDestination = navController.currentDestination?.route

    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { closedrawer() },
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.menu_icon),
                    contentDescription = "Menu Icon",
                    tint = Color.Cyan // Keeps the original icon color
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            // Optional Header
            Text(
                text = "Menu",
                fontSize = 20.sp
            )
        }
        // Dynamically create NavigationDrawerItem for each SidebarItem
        sidebarItem.forEach { item ->
            NavigationDrawerItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconField),
                        contentDescription = item.nameField,
                        modifier = Modifier.size(30.dp)
                    )
                },
                label = {
                    Text(text = item.nameField)
                },
                selected = currentDestination == item.navigationField,
                onClick = {
                    navController.navigate(item.navigationField) {
                        // Avoid multiple copies of the same destination in the back stack
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                        closedrawer()
                    }
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun DrawerMenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {

}
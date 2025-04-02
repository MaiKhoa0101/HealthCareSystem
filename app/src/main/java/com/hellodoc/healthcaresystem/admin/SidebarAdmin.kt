package com.hellodoc.healthcaresystem.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.user.home.model.SidebarItem

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
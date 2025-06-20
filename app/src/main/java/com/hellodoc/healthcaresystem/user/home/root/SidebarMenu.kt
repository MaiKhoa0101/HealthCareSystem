package com.hellodoc.healthcaresystem.user.home.root

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
fun SidebarMenu() {
    var isExpanded by remember { mutableStateOf(false) }
    val widthAnim by animateDpAsState(targetValue = if (isExpanded) 200.dp else 0.dp) // Animates width
    val alphaAnim by animateFloatAsState(targetValue = if (isExpanded) 1f else 0f) // Animates opacity

    Column(
        Modifier
            .fillMaxHeight()
            .width(widthAnim)
            .background(Color.Blue.copy(alpha = alphaAnim))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Menu Icon that toggles expansion
        Icon(
            Icons.Default.Menu,
            modifier = Modifier
                .clickable { isExpanded = !isExpanded }
                .size(45.dp),
            contentDescription = "Toggle Sidebar",
            tint = Color.Magenta
        )

        if (isExpanded) {
            Spacer(modifier = Modifier.height(20.dp))

            // Drawer items
            DrawerItem(icon = Icons.Default.Person,"Thông tin", isExpanded)
            DrawerItem(icon = Icons.Default.Settings,  "Cài đặt", isExpanded)
            DrawerItem(icon = Icons.Default.Info, "Topic", isExpanded)
            DrawerItem(icon = Icons.Default.Search,  "Khám bệnh", isExpanded)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DrawerItem(icon: ImageVector, title: String, expanded: Boolean){
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.White)
        AnimatedContent(
            targetState = expanded,
            transitionSpec = {
                fadeIn(animationSpec = tween(150, 150)) togetherWith fadeOut(
                    tween(150)
                ) using SizeTransform { initialSize, targetSize ->
                    keyframes {
                        IntSize(targetSize.width, initialSize.height) at 150
                        durationMillis = 300
                    }
                }
            }
        ) {
                targetState ->
            if (targetState){
                val context = LocalContext.current
                Row(Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        color = Color.White,
                        modifier = Modifier.clickable { Toast.makeText(context, "Clicked: ${title}", Toast.LENGTH_SHORT).show() })
                }
            }
        }

    }
}
package com.example.healthcaresystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.healthcaresystem.ui.theme.HealthCareSystemTheme
import kotlinx.serialization.Serializable

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            HealthCareSystemTheme {
//                val navController = rememberNavController()
//                NavHost(
//                    navController = navController,
//                    startDestination = ScreenA
//                ){
//                    composable<ScreenA> {
//                        //Home()
//                        Column(
//                            modifier = Modifier.fillMaxSize(),
//                            verticalArrangement = Arrangement.Center,
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            Button(onClick = {
//                                navController.navigate(ScreenB(
//                                    name = "Minh",
//                                    age =50
//                                ))
//                            }) {
//                                Text("Go to screen B")
//                            }
//                        }
//                    }
//                    composable<ScreenB> {
//                        val args = it.toRoute<ScreenB>()
//                        Column(
//                            modifier = Modifier.fillMaxSize(),
//                            verticalArrangement = Arrangement.Center,
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                                Text("${args.name}, ${args.age} age")
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//
//@Serializable
//object ScreenA
//
//@Serializable
//data class ScreenB(
//    val name: String?,
//    val age: Int
//)
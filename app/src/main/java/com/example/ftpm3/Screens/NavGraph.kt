package com.example.ftpm3.Screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ftpm3.FtpViewModel

@Composable
fun SetupNavGraph(
    ftpViewModel: FtpViewModel,
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = Screens.Login.route
    ) {
        composable(
            route = Screens.Login.route
        ) {
            LoginScreen(ftpViewModel, navHostController)
        }
        composable(
            route = Screens.Main.route
        ) {
            MainScreen(ftpViewModel, navHostController)
        }
    }
}
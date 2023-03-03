package com.example.ftpm3.Screens

sealed class Screens(val route: String) {
    object Login: Screens(route = "login_screen")
    object Main: Screens(route = "main_screen")
}
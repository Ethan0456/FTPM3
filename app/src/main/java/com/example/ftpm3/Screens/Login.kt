package com.example.ftpm3.Screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ftpm3.FtpViewModel
import com.example.ftpm3.`UI-Components`.GradientConnectingButton

@OptIn(ExperimentalMaterial3Api::class)
//@Preview
@Composable
fun LoginScreen(
    ftpViewModel: FtpViewModel,
    navHostController: NavHostController
) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val ip: String by ftpViewModel.ip.observeAsState("")
        val port: String by ftpViewModel.port.observeAsState("")
        val username: String by ftpViewModel.username.observeAsState("")
        val password: String by ftpViewModel.password.observeAsState("")
        val defaultDir: String by ftpViewModel.defaultDir.observeAsState("")

        OutlinedTextField(
            value = ip,
            label = { Text("IP") },
            singleLine = true,
            shape = RoundedCornerShape(size = 10.dp),
            onValueChange = { ftpViewModel.onValueChanged("ip",it) }
        )
        OutlinedTextField(
            value = port,
            label = { Text("Port") },
            singleLine = true,
            shape = RoundedCornerShape(size = 10.dp),
            onValueChange = { ftpViewModel.onValueChanged("port",it) }
        )
        OutlinedTextField(
            value = username,
            label = { Text("Username") },
            singleLine = true,
            shape = RoundedCornerShape(size = 10.dp),
            onValueChange = { ftpViewModel.onValueChanged("username",it) }
        )
//        OutlinedTextField(
//            value = password,
//            label = { Text("Password") },
//            singleLine = true,
//            shape = RoundedCornerShape(size = 10.dp),
//            onValueChange = { ftpViewModel.onValueChanged("password",it) },
//        )
        var passwordVisibility by remember { mutableStateOf(false)}

        var icon =
            if (passwordVisibility)
                painterResource(id = com.example.ftpm3.R.drawable.visible)
            else
                painterResource(id = com.example.ftpm3.R.drawable.invisible)

        OutlinedTextField(
            value = password,
            onValueChange = {
                    ftpViewModel.onValueChanged("password", it)
            },
            placeholder = { Text(text = "Password") },
            label = { Text(text = "Password") },
            trailingIcon = {
                IconButton(
                    {
                        passwordVisibility = !passwordVisibility
                    }
                ) {
                    Icon(
                        painter = icon,
                        modifier = Modifier.size(20.dp),
                        contentDescription = "Visibility Icon")
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation =
            if (passwordVisibility)
                VisualTransformation.None
            else
                PasswordVisualTransformation()
        )
        OutlinedTextField(
            value = defaultDir,
            label = { Text("Default Directory") },
            singleLine = true,
            shape = RoundedCornerShape(size = 10.dp),
            onValueChange = { ftpViewModel.onValueChanged("defaultDir",it) },
        )

        var clicked by remember { mutableStateOf(false) }

        Button(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(color = MaterialTheme.colorScheme.primary)
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                ),
            shape = RoundedCornerShape(10.dp),
            onClick = {
                clicked = !clicked
                if (!ftpViewModel.getClientInstance().isConnected) {
                    ftpViewModel.onConnectClicked(
                        ftpViewModel = ftpViewModel,
                        navHostController = navHostController
                    )
                }
                else {
                    navHostController.navigate(Screens.Main.route)
                }
            }

        ) {
            Text(
                text = if (clicked) "Connecting" else "Connect",
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(start=30.dp, end=30.dp)
            )
            if (clicked) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(19.dp)
                        .width(19.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.inversePrimary
                )
                Spacer(modifier = Modifier.width(30.dp))
            }
        }
    }
}
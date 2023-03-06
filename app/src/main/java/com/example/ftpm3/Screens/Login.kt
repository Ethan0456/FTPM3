package com.example.ftpm3.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ftpm3.FtpViewModel

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
        OutlinedTextField(
            value = password,
            label = { Text("Password") },
            singleLine = true,
            shape = RoundedCornerShape(size = 10.dp),
            onValueChange = { ftpViewModel.onValueChanged("password",it) },
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
                .padding(15.dp),
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
                text = if (clicked) "Connecting..." else "Connect",
                color = MaterialTheme.colorScheme.onPrimary,
            )
            if (clicked) {
                Spacer(modifier = Modifier.width(8.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(19.dp)
                        .width(19.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            }
        }
    }
}
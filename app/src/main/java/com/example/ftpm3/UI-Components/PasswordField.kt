package com.example.ftpm3.`UI-Components`

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // rememberSaveable will survive activity or
        // process recreation using saved instance state mechanism
        var password by rememberSaveable { mutableStateOf("") }
        var passwordVisibility by remember { mutableStateOf(false)}

        var icon =
            if (passwordVisibility)
                painterResource(id = com.example.ftpm3.R.drawable.visible)
            else
                painterResource(id = com.example.ftpm3.R.drawable.invisible)

        OutlinedTextField(
            value = password,
            onValueChange = {
                // Below Condition restricts Password field to 8 characters only
                if (it.length <= 8)
                    password = it
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

    }
}
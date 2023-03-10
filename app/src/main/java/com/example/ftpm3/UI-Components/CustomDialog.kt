package com.example.ftpm3.`UI-Components`

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.ftpm3.FtpViewModel

@Composable
fun CustomDialog(
    ftpViewModel: FtpViewModel,
    navHostController: NavHostController,
    showDialog: MutableState<Boolean>,
    title: String = "Title",
    composable: @Composable () -> Unit
) {
    if (showDialog.value == true) {
        Dialog(
            onDismissRequest = { showDialog.value = false }
        ) {

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .wrapContentSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = title,
                        modifier = Modifier
                            .padding(10.dp),
                        style = TextStyle(
                            fontSize = 20.sp
                        )
                    )
                    composable()
                }
            }
        }
    }
}
package com.example.ftpm3.`UI-Components`

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun PreviewGoogleButton() {
//    GradientConnectingButton(
//        textColor = Color.White,
//        shape = RoundedCornerShape(50.dp),
//    )
}

@Composable
fun GradientConnectingButton(
    text: String = "Connect",
    textColor: Color = Color.Black,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    loadingText: String = "Connecting... ",
    shape: Shape = RoundedCornerShape(10.dp),
    progressIndicatorColor: Color = Color.White,
    onClicked: () -> Unit
) {
    var clicked by remember { mutableStateOf(false) }

    Surface(
        shape = shape,
        modifier = Modifier
            .clip(shape = shape)
            .clickable { clicked = !clicked }
            .wrapContentHeight(),
        border = BorderStroke(width = 0.dp, color = Color.Transparent)
    ) {
        Row (
            modifier = Modifier
                .background(color = backgroundColor)
                .padding(
                    start = 100.dp,
                    end = 100.dp,
                    top = 12.dp,
                    bottom = 12.dp
                )
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Text(
                text = if (clicked) loadingText else text,
                color = textColor,
            )

            if (clicked) {
                Spacer(modifier = Modifier.width(8.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(19.dp)
                        .width(19.dp),
                    strokeWidth = 2.dp,
                    color = progressIndicatorColor
                )
                onClicked()
            }
        }
    }
}
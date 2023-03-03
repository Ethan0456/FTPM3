package com.example.ftpm3.`UI-Components`

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun PreviewSelectableItem() {

//    var selected by remember { mutableStateOf(false) }

//    SelectableItem(selected = false, title = "Hello There!") {
//        selected = !selected
//    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    title: String,
    titleColor: Color =
        if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
    titleSize: TextStyle = MaterialTheme.typography.titleLarge,
    titleWeight: FontWeight = FontWeight.Medium,
    subTitle: String? = null,
    subTitleColor: Color =
        if (selected) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    borderWidth: Dp = 1.dp,
    borderColor: Color =
        if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    borderShape: Shape = RoundedCornerShape(size = 10.dp),
    type: Int = 1,
    checkState: Boolean,
    onClick: () -> Unit,
    onCheckBoxChecked: (Boolean) -> Unit
) {

    var scaleA = remember { Animatable(initialValue = 1f) }
    var scaleB = remember { Animatable(initialValue = 1f) }
    
    LaunchedEffect(key1 = selected) {
        if (selected) {
            launch {
                scaleA.animateTo(
                    targetValue = 0.3f,
                    animationSpec = tween(
                        durationMillis = 50
                    )
                )
                scaleA.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                scaleB.animateTo(
                    targetValue = 0.9f,
                    animationSpec = tween(
                        durationMillis = 50
                    )
                )
                scaleB.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        }
    }


    Column (
        modifier = modifier
            .padding(
                start = 5.dp,
                top = 3.dp,
                end = 5.dp,
                bottom = 3.dp
            )
            .scale(scale = scaleB.value)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = borderShape
            )
            .clip(shape = borderShape)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer
            )
            .clickable {
                onClick()
            }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(8f),
                text = title,
                style = TextStyle(
                    color = titleColor,
                    fontSize = titleSize.fontSize,
                    fontWeight = titleWeight,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (type == 0) {
                Checkbox(
                    checked = checkState,
                    onCheckedChange = {
                        Log.i("Tag","Iamclicked!!!")
                          onCheckBoxChecked(checkState)
                    },
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = MaterialTheme.colorScheme.primary
                    ),
                )
            }
        }

        if (subTitle != null) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
                text = subTitle,
                style = TextStyle(
                    color = subTitleColor
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
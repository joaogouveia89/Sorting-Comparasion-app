package io.github.joaogouveia89.sortingcomparasion.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AlgorithmBadge(
    text: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Box(modifier = Modifier
        .clickable{ onClick() }
        .clip(RoundedCornerShape(8.dp))
        .background(
            if(isSelected){
                ButtonDefaults.buttonColors().containerColor
            }else{
                ButtonDefaults.buttonColors().disabledContainerColor
            }
        )
        .padding(4.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = if(isSelected) Color.White else Color.Black
        )
    }
}
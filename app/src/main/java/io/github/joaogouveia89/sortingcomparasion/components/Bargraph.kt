package io.github.joaogouveia89.sortingcomparasion.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.joaogouveia89.sortingcomparasion.model.ListElement

@Composable
fun Bargraph(
    boxesWidth: Dp,
    chartHeight: Dp,
    screenHeight: Dp,
    columns: List<ListElement>
) {
    val canvaSize = (columns.size * (boxesWidth.value + 2))

    Canvas(modifier = Modifier
        .size(canvaSize.dp, chartHeight)
    ) {
        columns.forEachIndexed { idx, it ->
            val offset = screenHeight - it.height
            drawRect(
                color = it.color,
                size = Size(boxesWidth.value, it.height.value * 2),
                topLeft = Offset(((idx * (boxesWidth.value + 2))), offset.value)
            )
        }
    }
}
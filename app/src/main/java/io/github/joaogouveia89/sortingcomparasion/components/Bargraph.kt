package io.github.joaogouveia89.sortingcomparasion.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import io.github.joaogouveia89.sortingcomparasion.model.ListElement

@Composable
fun Bargraph(
    boxesWidth: Float,
    bargraphHeight: Dp,
    bargraphHeightInPx: Float,
    columns: List<ListElement>,
    boxWidth: Float,
    bargraphtWidth: Dp
) {
    Canvas(modifier = Modifier
        .size(bargraphtWidth, bargraphHeight)
    ) {
        columns.forEachIndexed { idx, it ->
            val h = (bargraphHeightInPx - it.height)
            val w = (idx * (boxWidth + 2))
            drawRect(
                color = it.color,
                size = Size(boxesWidth, it.height),
                topLeft = Offset(w, h)
            )
        }
    }
}
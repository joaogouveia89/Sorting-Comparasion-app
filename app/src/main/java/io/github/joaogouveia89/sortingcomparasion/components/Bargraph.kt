package io.github.joaogouveia89.sortingcomparasion.components

import android.util.Log
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
    boxesWidth: Float,
    bargraphHeight: Dp,
    bargraphHeightInPx: Float,
    columns: List<ListElement>,
    boxWidth: Float
) {
    val canvaSize = (columns.size * boxesWidth)

    Canvas(modifier = Modifier
        .size(canvaSize.dp, bargraphHeight)
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
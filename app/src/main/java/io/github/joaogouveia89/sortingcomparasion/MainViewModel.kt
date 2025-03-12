package io.github.joaogouveia89.sortingcomparasion

import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

class MainViewModel: ViewModel() {

    data class ColumnSorting(
        val color: Color,
        val n: Int,
        val height: Dp
    )
    private val _columns = mutableListOf<ColumnSorting>()

    val columns: List<ColumnSorting>
        get() = _columns

    fun initList(colors: List<Color>, screenHeight: Dp){

        _columns.addAll(
            colors.mapIndexed { idx, color ->
                val h = (screenHeight * idx / 80)
                ColumnSorting(
                    color,
                    idx,
                    h
                )
            }
        )
        _columns.shuffle()
    }
}
package io.github.joaogouveia89.sortingcomparasion

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.joaogouveia89.sortingcomparasion.components.Bargraph
import io.github.joaogouveia89.sortingcomparasion.components.GeneralInfo
import io.github.joaogouveia89.sortingcomparasion.state.SortingState

@Composable
fun ScreenContent(
    innerPadding: PaddingValues,
    boxesWidth: Float,
    startStopSorting: () -> Unit,
    onSortingAlgorithmChange: (SortingAlgorithm) -> Unit,
    uiState: SortingState,
    totalRam: Double,
    bargraphHeightInPx: Float,
    bargraphHeight: Dp,
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        GeneralInfo(
            modifier = Modifier
                .weight(0.3f),
            uiState = uiState,
            startStopSorting = startStopSorting,
            onSortingAlgorithmChange = onSortingAlgorithmChange,
            totalRam = totalRam
        )
        if (uiState.isLoadingList) {
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center

            ) {
                Text("Loading list")
            }
        } else {
            Row(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState())
            ) {
                Bargraph(
                    boxesWidth = boxesWidth,
                    columns = uiState.elements,
                    bargraphHeight = bargraphHeight,
                    bargraphHeightInPx = bargraphHeightInPx,
                    boxWidth = boxesWidth
                )
            }
        }
    }
}
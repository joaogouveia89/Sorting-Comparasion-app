package io.github.joaogouveia89.sortingcomparasion.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.joaogouveia89.sortingcomparasion.SortingAlgorithm
import io.github.joaogouveia89.sortingcomparasion.state.SortingState
import io.github.joaogouveia89.sortingcomparasion.ui.theme.GrayBg
import java.util.Locale

@Composable
fun GeneralInfo(
    modifier: Modifier = Modifier,
    uiState: SortingState,
    totalRam: Double,
    onSortingAlgorithmChange: (SortingAlgorithm) -> Unit,
    startStopSorting: () -> Unit
) {
    Row(
        modifier = modifier
            .background(GrayBg)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AlgorithmBadge(
                    text = "Bubble Sort",
                    isSelected = uiState.algorithm == SortingAlgorithm.BUBBLE_SORT,
                    onClick = { onSortingAlgorithmChange(SortingAlgorithm.BUBBLE_SORT) }
                )
                AlgorithmBadge(
                    text = "Quick Sort",
                    isSelected = uiState.algorithm == SortingAlgorithm.QUICK_SORT,
                    onClick = { onSortingAlgorithmChange(SortingAlgorithm.QUICK_SORT) }
                )
            }
            Button(
                modifier = Modifier.padding(top = 8.dp),
                enabled = uiState.buttonState.isEnabled,
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = if (uiState.buttonState.isPrimaryColor) ButtonDefaults.buttonColors().containerColor else Color.Red
                ),
                onClick = startStopSorting
            ) {
                Text(text = uiState.buttonState.label)
            }
            Row(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                val timeSize = 40.sp
                Text(
                    text = uiState.computationTime,
                    fontSize = timeSize
                )
            }

            uiState.lastRunningTime?.let {
                Row(
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .align(Alignment.Bottom),
                        fontSize = 12.sp,
                        text = "The record is : $it"
                    )
                }
            }

            Row {
                Text("Total Ram: ${String.format(Locale.ROOT, "%.2f", totalRam)} GB")
            }
        }
    }
}
package io.github.joaogouveia89.sortingcomparasion

import android.app.ActivityManager
import android.app.ActivityManager.MemoryInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.joaogouveia89.sortingcomparasion.ui.theme.GrayBg
import io.github.joaogouveia89.sortingcomparasion.ui.theme.SortingComparasionTheme
import io.github.joaogouveia89.sortingcomparasion.ui.theme.colorsChart
import java.lang.Math.pow
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: MainViewModel by viewModels()

        val memInfo = MemoryInfo()

        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memInfo);
        val totalRam = memInfo.totalMem / 10.0.pow(9.0)

        setContent {
            SortingComparasionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->


                    val configuration = LocalConfiguration.current

                    val screenHeight = configuration.screenHeightDp.dp
                    val screenWidth = configuration.screenWidthDp.dp

                    val boxesWidth = screenWidth / 80

                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    LaunchedEffect(Unit) {
                        viewModel.initList(colorsChart, screenHeight)
                    }


                    ScreenContent(
                        innerPadding = innerPadding,
                        boxesWidth = boxesWidth,
                        startStopSorting = viewModel::startStopSorting,
                        uiState = uiState,
                        onSortingAlgorithmChange = viewModel::changeSortAlgorithm,
                        totalRam = totalRam
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenContent(
    innerPadding: PaddingValues,
    boxesWidth: Dp,
    startStopSorting: () -> Unit,
    onSortingAlgorithmChange: (SortingAlgorithm) -> Unit,
    uiState: SortingState,
    totalRam: Double
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(0.3f)
                .background(GrayBg)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AlgorithmBadge(
                        text = "Bubble Sort",
                        isSelected = uiState.algorithm == SortingAlgorithm.BUBBLE_SORT,
                        onClick = { onSortingAlgorithmChange(SortingAlgorithm.BUBBLE_SORT)}
                    )
                    AlgorithmBadge(
                        text = "Quick Sort",
                        isSelected = uiState.algorithm == SortingAlgorithm.QUICK_SORT,
                        onClick = {onSortingAlgorithmChange(SortingAlgorithm.QUICK_SORT)}
                    )
                    AlgorithmBadge(
                        text = "Merge Sort",
                        isSelected = uiState.algorithm == SortingAlgorithm.MERGE_SORT,
                        onClick = {onSortingAlgorithmChange(SortingAlgorithm.MERGE_SORT)}
                    )
                    AlgorithmBadge(
                        text = "Selection Sort",
                        isSelected = uiState.algorithm == SortingAlgorithm.SELECTION_SORT,
                        onClick = {onSortingAlgorithmChange(SortingAlgorithm.SELECTION_SORT)}
                    )
                }
                Button(
                    modifier = Modifier.padding(top = 12.dp),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = if(uiState.operationState == OperationState.SORTING) Color.Red else ButtonDefaults.buttonColors().containerColor
                    ),
                    onClick = startStopSorting
                ) {
                    Text(text = uiState.operationButtonLabel )
                }
                Row(
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    val timeSize = 40.sp
                    Text(
                        text = "%02d".format(uiState.timerSec),
                        fontSize = timeSize
                    )
                    Text(
                        text = ":",
                        fontSize = timeSize
                    )
                    Text(
                        text = "%02d".format(uiState.timerMs),
                        fontSize = timeSize
                    )
                }

                Row {
                    Text("Total Ram: ${String.format(Locale.ROOT, "%.2f", totalRam)} GB")
                }
            }
        }
        if(uiState.isLoadingList){
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center

            ) {
                Text("Loading list")
            }
        }else{
            Row(
                modifier = Modifier
                    .weight(0.7f)
                    .horizontalScroll(rememberScrollState())
            ) {
                Chart(
                    boxesWidth = boxesWidth,
                    columns = uiState.columns
                )
            }
        }
    }
}


@Composable
fun Chart(
    boxesWidth: Dp,
    columns: List<ColumnSorting>
) {
    Box(
        contentAlignment = Alignment.BottomStart
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            for (i in columns)
                Column(
                    modifier = Modifier
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom
                ) {

                    Box(
                        modifier = Modifier
                            .width(boxesWidth)
                            .height(i.height)
                            .background(i.color)
                    )
                }
        }
    }
}

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


@Preview(showBackground = true)
@Composable
fun ScreenContentPreview() {
    SortingComparasionTheme {
        val configuration = LocalConfiguration.current

        val screenWidth = configuration.screenWidthDp.dp

        val boxesWidth = screenWidth / 80

        ScreenContent(
            boxesWidth = boxesWidth, uiState = SortingState(),
            innerPadding = PaddingValues(),
            startStopSorting = {},
            onSortingAlgorithmChange = {},
            totalRam = 4.0
        )
    }
}
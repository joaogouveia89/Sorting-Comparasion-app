package io.github.joaogouveia89.sortingcomparasion

import android.app.ActivityManager
import android.app.ActivityManager.MemoryInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.joaogouveia89.sortingcomparasion.ui.theme.SortingComparasionTheme
import io.github.joaogouveia89.sortingcomparasion.ui.theme.colorsChart
import kotlin.math.pow

class MainActivity : ComponentActivity() {

    private var screenWidth = 0.dp
    private var screenHeight = 0.dp

    private val colorsChartSize = colorsChart.size

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: MainViewModel by viewModels()

        val memInfo = MemoryInfo()

        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memInfo)
        val totalRam = memInfo.totalMem / 10.0.pow(9.0)

        setContent {
            SortingComparasionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val configuration = LocalConfiguration.current
                    val density = LocalDensity.current
                    screenHeight = configuration.screenHeightDp.dp
                    screenWidth = configuration.screenWidthDp.dp

                    val boxesWidth = with(density) { screenWidth.toPx() / 30.0 }.toFloat()
                    val screenHeightInPx = with(density) { screenHeight.toPx() }

                    val bargraphtHeight = screenHeight * 7/10
                    val bargraphtHeightInPx = screenHeightInPx * 7/10
                    val bargraphtWidth = with(density) { (colorsChartSize * (boxesWidth + 2)).toDp() }

                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    LaunchedEffect(Unit) {
                        viewModel.initList(colorsChart, bargraphtHeightInPx)
                    }

                    ScreenContent(
                        innerPadding = innerPadding,
                        boxesWidth = boxesWidth,
                        startStopSorting = viewModel::startStopSorting,
                        uiState = uiState,
                        onSortingAlgorithmChange = viewModel::changeSortAlgorithm,
                        totalRam = totalRam,
                        bargraphHeight = bargraphtHeight,
                        bargraphHeightInPx = bargraphtHeightInPx,
                        bargraphtWidth = bargraphtWidth
                    )
                }
            }
        }
    }
}
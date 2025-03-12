package io.github.joaogouveia89.sortingcomparasion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.joaogouveia89.sortingcomparasion.ui.theme.SortingComparasionTheme
import io.github.joaogouveia89.sortingcomparasion.ui.theme.colorsChart
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SortingComparasionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel: MainViewModel by viewModels()

                    val configuration = LocalConfiguration.current

                    val screenHeight = configuration.screenHeightDp.dp
                    val screenWidth = configuration.screenWidthDp.dp

                    val boxesWidth = screenWidth / 80

                    viewModel.initList(colorsChart, screenHeight)
                    Chart(
                        modifier = Modifier.padding(innerPadding),
                        boxesWidth = boxesWidth,
                        columns = viewModel.columns
                    )
                }
            }
        }
    }
}

@Composable
fun Chart(
    modifier: Modifier = Modifier,
    boxesWidth: Dp,
    columns: List<MainViewModel.ColumnSorting>
) {

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            for(i in columns)
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


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SortingComparasionTheme {
        val configuration = LocalConfiguration.current

        val screenWidth = configuration.screenWidthDp.dp

        val boxesWidth = screenWidth / 80
        Chart( boxesWidth = boxesWidth, columns = listOf())
    }
}
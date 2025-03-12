package io.github.joaogouveia89.sortingcomparasion

import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class ColumnSorting(
    val color: Color,
    val n: Int,
    val height: Dp
)

data class SortingState(
    val columns: List<ColumnSorting> = listOf(),
    val timerSec: Int = 0,
    val timerMs: Int = 0,
    val isRunning: Boolean = false,
    val isSorted: Boolean = false
)

class MainViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(SortingState())

    val uiState: StateFlow<SortingState>
        get() = _uiState

    private var timerJob: Job? = null

    fun initList(colors: List<Color>, screenHeight: Dp){

        val columns = colors.mapIndexed { idx, color ->
            val h = (screenHeight * 2 / 3) * idx / 80
            ColumnSorting(
                color,
                idx,
                h
            )
        }

        shuffleList(columns)
    }

    fun startStopSorting() {
        if(_uiState.value.isRunning){
            cancelTimer()
        }else{
            startTimer()
            viewModelScope.launch(Dispatchers.IO) {
                bubbleSort()
            }
        }
    }

    fun restartSorting(){
        shuffleList()
        startStopSorting()
    }

    private fun shuffleList(list: List<ColumnSorting> = uiState.value.columns){
        _uiState.update {
            it.copy(
                columns = list.shuffled()
            )
        }
    }

    private suspend fun bubbleSort() {
        val arr = uiState.value.columns.toMutableList()
        val n = arr.size

        for (i in 0 until n - 1) {
            for (j in 0 until n - i - 1) {
                delay(1)
                if (arr[j].n > arr[j + 1].n) {
                    // Swap the elements
                    val temp = arr[j]
                    arr[j] = arr[j + 1]
                    arr[j + 1] = temp
                    _uiState.update { it.copy(columns = arr.toList()) }
                }
            }
        }
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false) }
        _uiState.update { it.copy(isSorted = true) }
    }

    private fun startTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = true, isSorted = false) }

        timerJob = viewModelScope.launch(Dispatchers.IO) {
            var timeSec = 0
            var timeMs = 0

            while (isActive) {
                delay(1) // Atualiza a cada 1ms
                timeMs += 1

                if (timeMs >= 1000) {
                    timeMs = 0
                    timeSec++
                }

                _uiState.update { it.copy(timerSec = timeSec, timerMs = timeMs) }
            }
        }
    }

    private fun cancelTimer() {
        _uiState.update { it.copy(isRunning = false) }
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        _uiState.update { it.copy(isRunning = false) }
        timerJob?.cancel()
    }
}
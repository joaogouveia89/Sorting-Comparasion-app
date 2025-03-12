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
    val isRunning: Boolean = false
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
        }.shuffled()

        _uiState.update {
            it.copy(
                columns = columns
            )
        }
    }

    fun startStopSorting() {
        if(_uiState.value.isRunning){
            cancelTimer()
        }else{
            startTimer()
        }
    }

    fun startTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = true) }

        timerJob = viewModelScope.launch(Dispatchers.IO) {
            var timeSec = 0
            var timeMs = 0

            while (isActive) {
                delay(50) // Atualiza a cada 200ms
                timeMs += 50

                if (timeMs >= 1000) {
                    timeMs = 0
                    timeSec++
                }

                _uiState.update { it.copy(timerSec = timeSec, timerMs = timeMs) }
            }
        }
    }

    fun cancelTimer() {
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
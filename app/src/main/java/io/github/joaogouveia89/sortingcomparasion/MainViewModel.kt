package io.github.joaogouveia89.sortingcomparasion

import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

enum class OperationState{
    IDLE, SORTING, INTERRUPTED, FINISHED
}
data class SortingState(
    val columns: List<ColumnSorting> = listOf(),
    val timerSec: Int = 0,
    val timerMs: Int = 0,
    val operationState: OperationState = OperationState.IDLE,
    val algorithm: SortingAlgorithm = SortingAlgorithm.BUBBLE_SORT,
    val runningTimes: MutableMap<SortingAlgorithm, String> = mutableMapOf(),
    val isLoadingList: Boolean = true
){
    val operationButtonLabel: String
        get() = when(operationState){
            OperationState.IDLE ->"Start"
            OperationState.SORTING -> "Interrupt"
            OperationState.INTERRUPTED -> "Reshuffle"
            OperationState.FINISHED -> "Resort"
        }
}

class MainViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(SortingState())

    val uiState: StateFlow<SortingState>
        get() = _uiState

    private var timerJob: Job? = null

    fun initList(colors: List<Color>, screenHeight: Dp){
        val list = viewModelScope.async(Dispatchers.IO)  {
            val listSize = colors.size
            val columns = colors.mapIndexed { idx, color ->
                val h = (screenHeight * 2 / 3) * idx / listSize
                ColumnSorting(
                    color,
                    idx,
                    h
                )
            }
            shuffleList(columns)
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingList = false,
                    columns = list.await()
                )
            }
        }
    }

    fun startStopSorting() {
        when(uiState.value.operationState){
            OperationState.SORTING -> {
                cancelTimer()
                _uiState.update { it.copy(operationState = OperationState.INTERRUPTED) }
            }

            OperationState.IDLE -> {
                startTimer()
                viewModelScope.launch(Dispatchers.IO) {
                    startSorting()
                }
            }
            else -> {
                viewModelScope.launch(Dispatchers.IO) {
                    uiState.value.runningTimes[uiState.value.algorithm] = "${uiState.value.timerSec}:${uiState.value.timerMs}"
                    startTimer()
                    startSorting()
                }
            }
        }
    }

    fun changeSortAlgorithm(algorithm: SortingAlgorithm){
        if(uiState.value.operationState != OperationState.SORTING){
            _uiState.update {
                it.copy(
                    algorithm = algorithm,
                    timerSec = 0,
                    timerMs = 0,
                    columns = shuffleList()
                )
            }
        }
    }

    private fun shuffleList(list: List<ColumnSorting> = uiState.value.columns): List<ColumnSorting>{
        return list.shuffled()
    }

    private fun startSorting(){
        if(_uiState.value.operationState == OperationState.FINISHED){
                _uiState.update { it.copy(
                    columns = shuffleList()
                )
            }
        }
        _uiState.update {
            it.copy(
                operationState = OperationState.SORTING
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            when(uiState.value.algorithm){
                SortingAlgorithm.BUBBLE_SORT -> bubbleSort()
                SortingAlgorithm.QUICK_SORT -> quickSort()
                SortingAlgorithm.MERGE_SORT -> mergeSort()
            }
        }
    }

    private fun bubbleSort() {
        val arr = uiState.value.columns.toMutableList()
        val n = arr.size

        for (i in 0 until n - 1) {
            if(uiState.value.operationState == OperationState.INTERRUPTED) break
            for (j in 0 until n - i - 1) {
                if (arr[j].n > arr[j + 1].n) {
                    if(uiState.value.operationState == OperationState.INTERRUPTED) break
                    // Swap the elements
                    val temp = arr[j]
                    arr[j] = arr[j + 1]
                    arr[j + 1] = temp
                    _uiState.update { it.copy(columns = arr.toList()) }
                }
            }
        }
        timerJob?.cancel()
        if(uiState.value.operationState != OperationState.INTERRUPTED){
            _uiState.update {
                if(!it.runningTimes.containsKey(SortingAlgorithm.BUBBLE_SORT)){
                    it.runningTimes[SortingAlgorithm.BUBBLE_SORT] = "${it.timerSec}:${it.timerMs}"
                }
                it.copy(operationState = OperationState.FINISHED)
            }
        }
    }

    private fun quickSort(left: Int = 0, right: Int = uiState.value.columns.size - 1){
        val arr = uiState.value.columns.toMutableList()
        var start = left
        var end = right
        val pivot = arr[(left + right) / 2].n

        while (start <= end) {
            while (arr[start].n < pivot) {
                start++
            }
            while (arr[end].n > pivot) {
                end--
            }
            if (start <= end) {
                val temp = arr[start]
                arr[start] = arr[end]
                arr[end] = temp
                start++
                end--
                _uiState.update { it.copy(columns = arr.toList()) }
            }
        }

        if (left < end) {
            quickSort(left, end)
        }
        if (start < right) {
            quickSort(start, right)
        }

        timerJob?.cancel()
        if(uiState.value.operationState != OperationState.INTERRUPTED){
            _uiState.update {
                if(!it.runningTimes.containsKey(SortingAlgorithm.QUICK_SORT)){
                    it.runningTimes[SortingAlgorithm.QUICK_SORT] = "${it.timerSec}:${it.timerMs}"
                }
                it.copy(
                    operationState = OperationState.FINISHED,
                )
            }
        }
    }
    private fun mergeSort(){
        val arr = uiState.value.columns.toMutableList()
        viewModelScope.launch(Dispatchers.IO) {
            val sorted = mergeSortIterative(arr)

            _uiState.update { it.copy(columns = sorted) }

            timerJob?.cancel()
            if(uiState.value.operationState != OperationState.INTERRUPTED){
                _uiState.update {
                    if(!it.runningTimes.containsKey(SortingAlgorithm.MERGE_SORT)){
                        it.runningTimes[SortingAlgorithm.MERGE_SORT] = "${it.timerSec}:${it.timerMs}"
                    }
                    it.copy(operationState = OperationState.FINISHED)
                }
            }
        }
    }

    private suspend fun mergeSortIterative(arr: List<ColumnSorting>): List<ColumnSorting> {
        if (arr.size <= 1) return arr

        var current = arr.map { listOf(it) }

        while (current.size > 1) {
            val next = mutableListOf<Deferred<List<ColumnSorting>>>()

            for (i in current.indices step 2) {
                next.add(viewModelScope.async(Dispatchers.IO) {
                    if (i + 1 < current.size)
                        merge(current[i], current[i + 1])
                    else
                        current[i]
                })
            }

            current = next.awaitAll()
            _uiState.update { it.copy(columns = current.flatten()) }
        }

        return current.first()
    }

    private fun merge(left: List<ColumnSorting>, right: List<ColumnSorting>): List<ColumnSorting> {
        var i = 0
        var j = 0
        val merged = mutableListOf<ColumnSorting>()

        while (i < left.size && j < right.size) {
            if (left[i].n <= right[j].n) {
                merged.add(left[i++])
            } else {
                merged.add(right[j++])
            }
        }

        while (i < left.size) merged.add(left[i++])
        while (j < right.size) merged.add(right[j++])

        return merged
    }

    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch(Dispatchers.IO) {
            var timeSec = 0
            var timeMs = 0

            while (isActive) {
                delay(1)
                timeMs += 1

                if (timeMs >= 1000) {
                    timeMs = 0
                    timeSec++
                }

                _uiState.value = _uiState.value.copy(timerSec = timeSec, timerMs = timeMs)
            }
        }
    }

    private fun cancelTimer() {
        _uiState.update { it.copy(operationState = OperationState.INTERRUPTED) }
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        _uiState.update { it.copy(operationState = OperationState.INTERRUPTED) }
        timerJob?.cancel()
    }
}
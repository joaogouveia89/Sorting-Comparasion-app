package io.github.joaogouveia89.sortingcomparasion

import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import io.github.joaogouveia89.sortingcomparasion.algorithms.Sorting
import io.github.joaogouveia89.sortingcomparasion.algorithms.SortingOperationStatus
import io.github.joaogouveia89.sortingcomparasion.algorithms.implementations.BubbleSorting
import io.github.joaogouveia89.sortingcomparasion.algorithms.implementations.QuickSorting
import io.github.joaogouveia89.sortingcomparasion.model.ListElement
import io.github.joaogouveia89.sortingcomparasion.state.ActionButtonInterrupt
import io.github.joaogouveia89.sortingcomparasion.state.ActionButtonResort
import io.github.joaogouveia89.sortingcomparasion.state.ActionButtonStart
import io.github.joaogouveia89.sortingcomparasion.state.SortingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SortingState())

    val uiState: StateFlow<SortingState>
        get() = _uiState

    private val algorithms: MutableMap<SortingAlgorithm, Sorting> = mutableMapOf()

    private val currentAlgorithm: Sorting?
        get() = algorithms[uiState.value.algorithm]

    fun initList(colors: List<Color>, bargraphHeightInPx: Float) {
        val list = viewModelScope.async(Dispatchers.IO) {
            val listSize = colors.size
            val columns = colors.mapIndexed { idx, color ->
                val h = (bargraphHeightInPx * idx / listSize)
                ListElement(
                    color,
                    idx,
                    h
                )
            }
            columns.shuffled()
        }
        viewModelScope.launch {
            _uiState.update {
                val finalList = list.await()
                initAlgorithms(finalList)
                changeSortAlgorithm(SortingAlgorithm.BUBBLE_SORT)
                it.copy(
                    elements = finalList,
                    buttonState = ActionButtonStart,
                    isLoadingList = false
                )
            }
        }
    }

    private fun initAlgorithms(list: List<ListElement>) {
        algorithms[SortingAlgorithm.BUBBLE_SORT] = BubbleSorting(list)
        algorithms[SortingAlgorithm.QUICK_SORT] = QuickSorting(list)
    }

    fun startStopSorting() {
        currentAlgorithm?.let { ca ->
            if (ca.isRunning) {
                ca.interrupt()
            } else {
                viewModelScope.launch(Dispatchers.IO) {
                    _uiState.update {
                        it.copy(buttonState = ActionButtonInterrupt)
                    }
                    ca.sort().collect { status ->
                        _uiState.update { state ->
                            when (status) {
                                is SortingOperationStatus.Idle -> {
                                    state
                                }
                                is SortingOperationStatus.Finished -> state.copy(
                                    elements = status.finalList,
                                    computationTime = status.finalRuntime.toTimeFormat(),
                                    buttonState = ActionButtonResort,
                                    lastRunningTime = ca.minRunningTime.toTimeFormat()
                                )

                                is SortingOperationStatus.Interrupted -> state.copy(
                                    elements = status.partialList,
                                    computationTime = status.currentRuntime.toTimeFormat(),
                                    buttonState = ActionButtonResort,
                                    lastRunningTime = ca.minRunningTime.toTimeFormat()
                                )

                                is SortingOperationStatus.Progress -> state.copy(
                                    elements = status.partialList,
                                    computationTime = status.currentRuntime.toTimeFormat(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun changeSortAlgorithm(algorithm: SortingAlgorithm) {
        if (currentAlgorithm?.isRunning == false) {
            _uiState.update {
                SortingState(
                    algorithm = algorithm,
                    isLoadingList = false,
                    elements = currentAlgorithm?.initialList ?: listOf(),
                    buttonState = ActionButtonStart,
                    computationTime = "00:000"
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        currentAlgorithm?.interrupt()
    }

    private fun Long.toTimeFormat(): String {
        val seconds = this / 1000
        val millis = this % 1000
        return "%02d:%03d".format(seconds, millis)
    }
}
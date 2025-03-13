package io.github.joaogouveia89.sortingcomparasion.algorithms

import io.github.joaogouveia89.sortingcomparasion.model.ListElement
import kotlinx.coroutines.flow.Flow


abstract class Sorting(
    open val initialList: List<ListElement>
) {
    @Volatile protected var isInterrupted = false

    val minRunningTime: Long
        get() = _minRunningTime

    protected var _minRunningTime = -1L

    abstract suspend fun sort(): Flow<SortingOperationStatus>

    abstract val name: String

    fun interrupt(){ isInterrupted = true }

    protected var _isRunning = false

    val isRunning: Boolean
        get() = _isRunning
}
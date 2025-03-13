package io.github.joaogouveia89.sortingcomparasion.algorithms.implementations

import io.github.joaogouveia89.sortingcomparasion.algorithms.Sorting
import io.github.joaogouveia89.sortingcomparasion.algorithms.SortingOperationStatus
import io.github.joaogouveia89.sortingcomparasion.model.ListElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.min

class BubbleSorting(
    override val initialList: List<ListElement>
): Sorting(initialList) {

    override val name: String
        get() = "Bubble Sorting"

    override suspend fun sort(): Flow<SortingOperationStatus> = flow{
        _isRunning = true
        isInterrupted = false
        val startTime = System.currentTimeMillis()
        val arr = initialList.toMutableList()
        val n = arr.size

        for (i in 0 until n - 1) {
            if(isInterrupted) break
            for (j in 0 until n - i - 1) {
                if (arr[j].n > arr[j + 1].n) {
                    if(isInterrupted) break
                    // Swap the elements
                    val temp = arr[j]
                    arr[j] = arr[j + 1]
                    arr[j + 1] = temp
                    val currentMs = System.currentTimeMillis() - startTime
                    emit(SortingOperationStatus.Progress(arr.toList(), currentMs))
                }
            }
        }
        val elapsed = System.currentTimeMillis() - startTime
        if(!isInterrupted){
            if(elapsed < minRunningTime)
                _minRunningTime = elapsed
            emit(
                SortingOperationStatus.Finished(
                    arr.toList(),
                    elapsed
                )
            )
        }else{
            emit(
                SortingOperationStatus.Interrupted(
                    arr.toList(),
                    elapsed
                )
            )
        }
        _isRunning = false
    }
}
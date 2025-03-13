package io.github.joaogouveia89.sortingcomparasion.algorithms.implementations

import io.github.joaogouveia89.sortingcomparasion.algorithms.Sorting
import io.github.joaogouveia89.sortingcomparasion.algorithms.SortingOperationStatus
import io.github.joaogouveia89.sortingcomparasion.model.ListElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class QuickSorting(
    override val initialList: List<ListElement>
) : Sorting(initialList) {

    override suspend fun sort(): Flow<SortingOperationStatus> = flow {
        _isRunning = true
        isInterrupted = false
        val startTime = System.currentTimeMillis()
        val arr = initialList.toMutableList()
        val stack = mutableListOf<Pair<Int, Int>>()
        stack.add(0 to arr.size - 1)

        while (stack.isNotEmpty()) {
            val (left, right) = stack.removeAt(stack.size - 1)
            if (left >= right) continue

            val pivot = arr[(left + right) / 2].n
            var start = left
            var end = right

            while (start <= end) {
                while (arr[start].n < pivot) start++
                while (arr[end].n > pivot) end--
                if (start <= end) {
                    arr[start] = arr[end].also { arr[end] = arr[start] }
                    start++
                    end--
                    val currentMs = System.currentTimeMillis() - startTime
                    emit(
                        SortingOperationStatus.Progress(
                            partialList = arr.toList(),
                            currentRuntime = currentMs
                        )
                    )
                }
            }
            if (left < end) stack.add(left to end)
            if (start < right) stack.add(start to right)
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

    override val name: String
        get() = "Quick Sorting"

}
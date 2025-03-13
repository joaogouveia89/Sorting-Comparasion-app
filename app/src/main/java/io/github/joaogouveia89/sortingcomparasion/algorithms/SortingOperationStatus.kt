package io.github.joaogouveia89.sortingcomparasion.algorithms

import io.github.joaogouveia89.sortingcomparasion.model.ListElement

sealed class SortingOperationStatus {
    data object Idle: SortingOperationStatus()
    data class Progress(val partialList: List<ListElement>, val currentRuntime: Long): SortingOperationStatus()
    data class Interrupted(val partialList: List<ListElement>, val currentRuntime: Long): SortingOperationStatus()
    data class Finished(val finalList: List<ListElement>, val finalRuntime: Long): SortingOperationStatus()
}
package io.github.joaogouveia89.sortingcomparasion.state

import io.github.joaogouveia89.sortingcomparasion.SortingAlgorithm
import io.github.joaogouveia89.sortingcomparasion.model.ListElement

data class SortingState(
    val elements: List<ListElement> = listOf(),
    val algorithm: SortingAlgorithm = SortingAlgorithm.BUBBLE_SORT,
    val isLoadingList: Boolean = true,
    val buttonState: ActionButtonState = ActionButtonState(),
    val computationTime: String = "",
    val lastRunningTime: String? = null,
)
package io.github.joaogouveia89.sortingcomparasion.state

data class ActionButtonState(
    val label: String = "Loading",
    val isEnabled: Boolean = false,
    val isPrimaryColor: Boolean = true
)

val ActionButtonStart = ActionButtonState(
    label = "Start",
    isEnabled = true,
    isPrimaryColor = true
)

val ActionButtonInterrupt = ActionButtonState(
    label = "Interrupt",
    isEnabled = true,
    isPrimaryColor = false
)

val ActionButtonResort = ActionButtonState(
    label = "Resort",
    isEnabled = true,
    isPrimaryColor = true
)
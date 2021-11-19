package com.tappli.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class StateManager {
    sealed interface State {
        object InitialState : State
        object Loading : State
        object Done : State
        data class Error(val exception: Exception) : State
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.InitialState)
    val state = _state.asStateFlow()

    suspend fun execute(
        getValue: suspend () -> Unit
    ) {
        try {
            _state.value = State.Loading
            getValue()
            _state.value = State.Done
        } catch (e: Exception) {
            _state.value = State.Error(e)
        }
    }
}

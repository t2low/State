package com.tappli.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val stateManager = StateManager()
    val state = stateManager.state

    init {
        doProgress()
    }

    fun doProgress() {
        viewModelScope.launch {
            delay(5000)
            stateManager.execute {
                delay(10000)
            }
        }
    }


}
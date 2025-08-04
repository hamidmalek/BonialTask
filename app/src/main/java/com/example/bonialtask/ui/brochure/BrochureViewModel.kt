package com.example.bonialtask.ui.brochure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bonialtask.model.ContentWrapper
import com.example.bonialtask.repo.BrochureRepository
import com.example.bonialtask.repo.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BrochureViewModel(private val repo: BrochureRepository) : ViewModel() {
    private val _state = MutableStateFlow<Result<List<ContentWrapper.Brochure>>>(Result.Loading)
    val state: StateFlow<Result<List<ContentWrapper.Brochure>>> get() = _state

    init {
        load()
    }

    fun load() = viewModelScope.launch { repo.getBrochures().collectLatest { _state.value = it } }
}
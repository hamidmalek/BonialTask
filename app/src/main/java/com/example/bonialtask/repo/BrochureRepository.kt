package com.example.bonialtask.repo

import com.example.bonialtask.model.ContentWrapper
import com.example.bonialtask.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class BrochureRepository(private val api: ApiService) {
    fun getBrochures(): Flow<Result<List<ContentWrapper.Brochure>>> = flow {
        emit(Result.Loading)
        val response = api.getShelf()
        val x = response.embedded.contents.mapNotNull {
            it as? ContentWrapper.Brochure
        }
        emit(Result.Success(x))

    }.catch { e ->
        emit(Result.Error(e))
    }.flowOn(Dispatchers.IO)
}
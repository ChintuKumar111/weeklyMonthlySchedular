package com.shyamdairyfarm.user.utils

sealed interface UiState<out T> {

    data object Idle : UiState<Nothing>

    data object Loading : UiState<Nothing>

    data class Success<T>(val data: T) : UiState<T>
    data class ExpiredToken<T>(val data: T) : UiState<T>
    data class UnauthorizedAccess(val msg : String, val e : Exception? = null) : UiState<Nothing>

    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>
}
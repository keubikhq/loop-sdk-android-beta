package com.lib.loopsdk.core.util

import com.lib.loopsdk.data.remote.dto.ErrorDto

sealed class UIState {
    object Loading: UIState()
    data class Error(val message: String): UIState()
    data class Success<out T>(val data: T): UIState()
    data class ErrorWithData(val error: ErrorDto.Status?): UIState()
}
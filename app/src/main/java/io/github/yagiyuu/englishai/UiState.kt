package io.github.yagiyuu.englishai

sealed class UiState {
    data object Loading : UiState()
    data class Success(val phrase: PhraseResponse) : UiState()
    data class Error(val message: String) : UiState()
}
package io.github.yagiyuu.englishai

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EnglishAiViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val _hiddenAnswer: MutableStateFlow<Boolean> =
        MutableStateFlow(true)
    val hiddenAnswer: StateFlow<Boolean> =
        _hiddenAnswer.asStateFlow()

    val difficultyLevel = mutableIntStateOf(1)

    private val generativeModel = GenerativeModel(
        modelName = MODEL_NAME,
        apiKey = BuildConfig.apiKey,
        generationConfig = generationConfig {
            temperature = 1f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 8192
            responseMimeType = "application/json"
        }
    )

    private val chatHistory = listOf(
        content("user") {
            text(INITIAL_PROMPT)
        }
    )
    private val chat = generativeModel.startChat(chatHistory)

    init {
        fetchNewPhrase()
    }

    fun showAnswer() {
        _hiddenAnswer.value = false
    }

    fun fetchNewPhrase() {
        viewModelScope.launch(Dispatchers.IO) {
            _hiddenAnswer.emit(true)
            _uiState.emit(UiState.Loading)
            try {
                val prompt = "Difficulty level: ${difficultyLevel.intValue}"
                val res = chat.sendMessage(prompt).text ?: throw NullPointerException()
                val phrase = Gson().fromJson(res, TypeToken.get(PhraseResponse::class.java))
                _uiState.emit(UiState.Success(phrase))
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.emit(UiState.Error(e.message ?: "An error occurred"))
            }
        }
    }

    companion object {
        private const val MODEL_NAME = "gemini-1.5-flash-8b"
        private const val INITIAL_PROMPT = """
Please generate an English phrase along with its Japanese translation.
The difficulty level of the phrase should be determined by a number from 1 to 10 that I will send. The lower the number, the simpler the words used; the higher the number, the more complex the words used.

The output schema should be as follows:
The English phrase should be assigned to 'question'
The Japanese translation of that phrase should be assigned to 'answer'
        """
    }
}
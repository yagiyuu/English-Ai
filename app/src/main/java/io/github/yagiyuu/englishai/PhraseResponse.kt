package io.github.yagiyuu.englishai

import com.google.gson.annotations.SerializedName

data class PhraseResponse(
    @SerializedName("question")
    val english: String,
    @SerializedName("answer")
    val japanese: String
)
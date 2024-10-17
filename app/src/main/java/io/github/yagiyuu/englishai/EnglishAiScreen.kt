package io.github.yagiyuu.englishai

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EnglishAiScreen(
    englishAiViewModel: EnglishAiViewModel = viewModel()
) {
    val uiState by englishAiViewModel.uiState.collectAsState()
    val hiddenAnswer by englishAiViewModel.hiddenAnswer.collectAsState()
    var difficultyLevel by englishAiViewModel.difficultyLevel

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                when (uiState) {
                    UiState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is UiState.Success -> {
                        val phrase = (uiState as UiState.Success).phrase
                        Text(
                            text = phrase.english,
                            textAlign = TextAlign.Center,
                            style = typography.titleLarge
                        )

                        Text(
                            text = if (hiddenAnswer) {
                                "＊".repeat(phrase.japanese.length)
                            } else {
                                phrase.japanese
                            },
                            textAlign = TextAlign.Center,
                            style = typography.titleMedium,
                            modifier = Modifier.clickable {
                                englishAiViewModel.showAnswer()
                            }
                        )
                    }

                    is UiState.Error -> {
                        val message = (uiState as UiState.Error).message
                        Text(
                            text = "ERROR",
                            textAlign = TextAlign.Center,
                            style = typography.titleLarge,
                            color = MaterialTheme.colorScheme.error
                        )

                        Text(
                            text = message,
                            textAlign = TextAlign.Center,
                            style = typography.titleMedium
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Button(
                    enabled = uiState !is UiState.Loading,
                    onClick = {
                        englishAiViewModel.fetchNewPhrase()
                    },
                    shape = shapes.large
                ) {
                    Text(
                        text = "Next"
                    )
                }

                Text(
                    text = if (difficultyLevel <= 3) {
                        "簡単: $difficultyLevel"
                    } else if (difficultyLevel <= 7) {
                        "普通: $difficultyLevel"
                    } else {
                        "難しい: $difficultyLevel"
                    },
                    textAlign = TextAlign.Center,
                    style = typography.labelMedium
                )

                Slider(
                    value = difficultyLevel.toFloat(),
                    onValueChange = {
                        difficultyLevel = it.toInt()
                    },
                    valueRange = 1f..10f
                )
            }
        }
    }
}
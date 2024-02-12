package com.example.footballquiz

data class Question(
    val id: String = "",
    val text: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: String = ""
)
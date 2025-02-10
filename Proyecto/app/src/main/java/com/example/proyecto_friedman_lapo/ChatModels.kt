package com.example.proyecto_friedman_lapo

data class Message(val role: String, val content: String)

data class ChatRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>
)

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

package com.pocketclinic.app.domain.model

data class Session(
    val id: Long,
    val startedAt: Long,
    val finishedAt: Long,
    val notes: String,
)

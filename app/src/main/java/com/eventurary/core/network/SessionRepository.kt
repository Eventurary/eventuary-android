package com.eventurary.core.network

interface SessionRepository {
    suspend fun saveSessionToken(token: String)
    suspend fun getSessionToken(): String?
    suspend fun clearSessionToken()
}
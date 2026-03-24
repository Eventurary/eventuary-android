package com.eventurary.core.data

interface CryptoManager {
    fun encrypt(plainText: String): String
    fun decrypt(cipherText: String): String
}

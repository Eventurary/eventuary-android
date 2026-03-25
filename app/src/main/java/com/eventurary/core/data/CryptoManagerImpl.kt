package com.eventurary.core.data

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class CryptoManagerImpl : CryptoManager {

    companion object {
        private const val KEY_ALIAS = "encryption_key"
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"

        private const val GCM_IV_LENGTH_BYTES = 12
        private const val GCM_TAG_LENGTH_BITS = 128
    }

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    private fun getSecretKey(): SecretKey {
        val secretKeyEntry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return secretKeyEntry?.secretKey ?: generateSecretKey()
    }

    private fun generateSecretKey(): SecretKey = KeyGenerator
        .getInstance(ALGORITHM)
        .apply {
            init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    PURPOSE_ENCRYPT or PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }
        .generateKey()

    private fun createCipher(): Cipher = Cipher.getInstance(TRANSFORMATION)

    /**
     * Encrypts the given plaintext using AES-GCM and returns a Base64-encoded string.
     *
     * @param plainText The raw string to encrypt
     * @return Base64-encoded ciphertext including IV
     *
     * @throws CryptoException if encryption fails due to:
     *   - NoSuchAlgorithmException – if the transformation is invalid or unsupported
     *   - NoSuchPaddingException – if the requested padding scheme is not available
     *   - UnsupportedOperationException – if opmode is WRAP/UNWRAP but not implemented
     *   - InvalidKeyException – if the key is invalid
     *   - IllegalStateException – if cipher is in a wrong state (e.g., not initialized)
     *   - IllegalBlockSizeException – if input length is invalid for block cipher
     *   - BadPaddingException – if padding is incorrect in decryption
     *   - AEADBadTagException – if GCM/CCM authentication fails
     *   - AssertionError – if Base64 encoding fails
     */
    override fun encrypt(plainText: String): String {
        runCatching {
            val cipher = createCipher()
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

            val iv = cipher.iv
            val encrypted = cipher.doFinal(plainText.toByteArray())

            return Base64.encodeToString(iv + encrypted, Base64.DEFAULT)
        }.getOrElse { e ->
            throw CryptoException("Failed to decrypt data", e)
        }
    }

    /**
     * Decrypts a Base64-encoded ciphertext produced by [encrypt].
     *
     * @param cipherText The Base64-encoded encrypted string
     * @return The original plaintext
     *
     * @throws CryptoException if decryption fails due to:
     *   - NoSuchAlgorithmException – if the transformation is null, empty, invalid,
     *     or unsupported by any provider.
     *   - NoSuchPaddingException – if the requested padding scheme is not available.
     *   - UnsupportedOperationException – if opmode is WRAP_MODE or UNWRAP_MODE
     *     but not implemented by the CipherSpi.
     *   - InvalidKeyException – if the key used is invalid.
     *   - IllegalStateException – if the cipher is in the wrong state (e.g., not initialized).
     *   - IllegalBlockSizeException – if input length is invalid for block cipher,
     *     or encryption cannot process the data.
     *   - BadPaddingException – if padding is incorrect in decryption mode.
     *   - AEADBadTagException – if GCM/CCM authentication fails.
     *   - AssertionError – if Base64 encoding/decoding fails.
     *   - IndexOutOfBoundsException – if array ranges are invalid (fromIndex < 0 or toIndex > size).
     *   - IllegalArgumentException – if fromIndex is greater than toIndex.
     */
    override fun decrypt(cipherText: String): String {
        runCatching {
            val cipher = createCipher()
            val bytes = Base64.decode(cipherText, Base64.DEFAULT)

            val iv = bytes.copyOfRange(0, GCM_IV_LENGTH_BYTES)
            val encrypted = bytes.copyOfRange(GCM_IV_LENGTH_BYTES, bytes.size)

            val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)

            return String(cipher.doFinal(encrypted))
        }.getOrElse { e ->
            throw CryptoException("Failed to decrypt data", e)
        }
    }
}

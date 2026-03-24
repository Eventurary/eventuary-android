package com.eventurary.core.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eventurary.core.mocks.keystore.FakeAndroidKeyStoreProvider
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CryptoManagerImplTest {

    private lateinit var cut: CryptoManagerImpl

    @Before
    fun setUp() {
        FakeAndroidKeyStoreProvider.setup()
        cut = CryptoManagerImpl()
    }

    @Test
    fun encryptAndDecrypt_returnOriginalString() {
        // GIVEN
        val original = "my secret token"

        // WHEN
        val encrypted = cut.encrypt(original)
        val decrypted = cut.decrypt(encrypted)

        // THEN
        TestCase.assertEquals(original, decrypted)
    }

    @Test(expected = IllegalArgumentException::class)
    fun decryptInvalidCipher_Throws() {
        // GIVEN
        val invalid = "not_base64"

        // WHEN
        cut.decrypt(invalid)
    }
}

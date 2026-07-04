package com.ark.base.common

import org.springframework.stereotype.Component
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.spec.MGF1ParameterSpec
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

@Component
class PasswordTransportCrypto {
    private val keyPair: KeyPair =
        KeyPairGenerator
            .getInstance(RSA_ALGORITHM)
            .apply { initialize(KEY_SIZE) }
            .generateKeyPair()

    fun publicKeySpkiBase64(): String = Base64.getEncoder().encodeToString(keyPair.public.encoded)

    fun decrypt(cipherTextBase64: String): String {
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, keyPair.private, oaepParameterSpec())
        val plainBytes = cipher.doFinal(Base64.getDecoder().decode(cipherTextBase64))
        return String(plainBytes, Charsets.UTF_8)
    }

    private fun oaepParameterSpec(): OAEPParameterSpec =
        OAEPParameterSpec(
            "SHA-256",
            "MGF1",
            MGF1ParameterSpec.SHA256,
            PSource.PSpecified.DEFAULT,
        )

    companion object {
        private const val RSA_ALGORITHM = "RSA"
        private const val KEY_SIZE = 2048
        private const val CIPHER_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
    }
}

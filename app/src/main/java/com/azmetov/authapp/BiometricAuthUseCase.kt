package com.azmetov.authapp

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity

class BiometricAuthUseCase(
    private val activity: FragmentActivity,
    private val biometricManager: BiometricManager,
) : Observable<BiometricAuthUseCase.Listener>() {

    sealed class AuthResult {
        object NotEnrolled : AuthResult()
        object NotSupported : AuthResult()
        data class Failed(val errorCode: Int, val errorMessage: String) : AuthResult()
        object Cancelled : AuthResult()
        object Success : AuthResult()
    }

    fun interface Listener {
        fun onBiometricAuthResult(result: AuthResult)
    }

    private val biometricPrompt = BiometricPrompt(activity,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                val cancelled = errorCode in arrayListOf<Int>(
                    BiometricPrompt.ERROR_CANCELED,
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON
                )
                if (cancelled) {
                    listeners.map { it.onBiometricAuthResult(AuthResult.Cancelled) }
                } else {
                    listeners.map {
                        it.onBiometricAuthResult(
                            AuthResult.Failed(
                                errorCode,
                                errString.toString()
                            )
                        )
                    }
                }
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                listeners.map { it.onBiometricAuthResult(AuthResult.Success) }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
            }
        }
    )

    fun authenticate(title: String, description: String, negativeButtonText: String) {
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> { /* proceed */
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                listeners.map { it.onBiometricAuthResult(AuthResult.NotEnrolled) }
                return
            }

            else -> {
                listeners.map { it.onBiometricAuthResult(AuthResult.NotSupported) }
                return
            }
        }
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle("")
            .setDescription(description)
            .setNegativeButtonText(negativeButtonText)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()
        biometricPrompt.authenticate(promptInfo)
    }
}


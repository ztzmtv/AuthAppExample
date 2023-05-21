package com.azmetov.authapp

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class BiometricAuthUseCase(
    private val activity: FragmentActivity,
    private val biometricManager: BiometricManager,
) {
    private val mutableLiveData = MutableLiveData<AuthResult>()
    val liveData: LiveData<AuthResult> = mutableLiveData


    sealed class AuthResult {
        object NotEnrolled : AuthResult()
        object NotSupported : AuthResult()
        data class Failed(val errorCode: Int, val errorMessage: String) : AuthResult()
        object Cancelled : AuthResult()
        object Success : AuthResult()
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
                    mutableLiveData.postValue(AuthResult.Cancelled)
                } else {
                    mutableLiveData.postValue(
                        AuthResult.Failed(errorCode, errString.toString())
                    )
                }
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                mutableLiveData.postValue(AuthResult.Success)
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
                mutableLiveData.postValue(AuthResult.NotEnrolled)
                return
            }

            else -> {
                mutableLiveData.postValue(AuthResult.NotSupported)
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


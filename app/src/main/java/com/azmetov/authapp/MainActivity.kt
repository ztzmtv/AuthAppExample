package com.azmetov.authapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import com.azmetov.authapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), BiometricAuthUseCase.Listener {
    private lateinit var biometricAuthUseCase: BiometricAuthUseCase
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        biometricAuthUseCase = BiometricAuthUseCase(this, BiometricManager.from(this))
        binding.button.setOnClickListener {
            authenticate()
        }
    }

    override fun onStart() {
        super.onStart()
        biometricAuthUseCase.registerListener(this)
    }

    override fun onStop() {
        super.onStop()
        biometricAuthUseCase.unregisterListener(this)
    }

    private fun authenticate() {
        biometricAuthUseCase.authenticate(
            "getString(R.string.biometric_auth_title)",
            "getString(R.string.biometric_auth_description)",
            "getString(R.string.cancel)",
        )
    }

    override fun onBiometricAuthResult(result: BiometricAuthUseCase.AuthResult) {
        when (result) {
            is BiometricAuthUseCase.AuthResult.Cancelled -> {}
            is BiometricAuthUseCase.AuthResult.Failed -> {}
            is BiometricAuthUseCase.AuthResult.NotEnrolled -> {}
            is BiometricAuthUseCase.AuthResult.NotSupported -> {}
            is BiometricAuthUseCase.AuthResult.Success -> {}
        }
        //show result
        Toast.makeText(this, result.javaClass.simpleName, Toast.LENGTH_LONG).show()
    }

}
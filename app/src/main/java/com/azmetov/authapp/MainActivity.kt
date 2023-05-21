package com.azmetov.authapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import com.azmetov.authapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var biometricAuthUseCase: BiometricAuthUseCase
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        binding.button.setOnClickListener {
            authenticate()
        }
        biometricAuthUseCase = BiometricAuthUseCase(this, BiometricManager.from(this))
        biometricAuthUseCase.liveData.observe(this) { result ->
            when (result) {
                is BiometricAuthUseCase.AuthResult.Cancelled -> {}
                is BiometricAuthUseCase.AuthResult.Failed -> {}
                is BiometricAuthUseCase.AuthResult.NotEnrolled -> {}
                is BiometricAuthUseCase.AuthResult.NotSupported -> {}
                is BiometricAuthUseCase.AuthResult.Success -> {}
            }
            Toast.makeText(this, result.javaClass.simpleName, Toast.LENGTH_LONG).show()
        }
    }

    private fun authenticate() {
        biometricAuthUseCase.authenticate(
            "getString(R.string.biometric_auth_title)",
            "getString(R.string.biometric_auth_description)",
            "getString(R.string.cancel)",
        )
    }
}
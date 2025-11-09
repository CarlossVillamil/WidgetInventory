package com.example.widgetinventory.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.widgetinventory.databinding.ActivityLoginBinding
import com.example.widgetinventory.home.HomeActivity
import androidx.core.content.edit

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBiometricAuth()
    }

    private fun setupBiometricAuth() {
        val biometricManager = BiometricManager.from(this)

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {}
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Toast.makeText(this, "No hay sensor de huellas disponible", Toast.LENGTH_SHORT).show()
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Toast.makeText(this, "Sensor no disponible temporalmente", Toast.LENGTH_SHORT).show()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                Toast.makeText(this, "Registra una huella en el dispositivo", Toast.LENGTH_SHORT).show()
        }

        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    val prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                    prefs.edit { putBoolean("isLoggedIn", true) }

                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Huella no reconocida", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación con Biometría")
            .setSubtitle("Ingrese su huella digital")
            .setNegativeButtonText("Cancelar")
            .build()

        binding.fingerprint.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }
}

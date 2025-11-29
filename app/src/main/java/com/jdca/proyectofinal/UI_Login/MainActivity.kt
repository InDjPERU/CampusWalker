package com.jdca.proyectofinal.UI_Login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.jdca.proyectofinal.R

class MainActivity : AppCompatActivity() {

    private lateinit var btnIngresar: Button

    // ===== SOLICITADOR DE PERMISOS =====
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val denied = permissions.filter { !it.value }.keys

            if (denied.isNotEmpty()) {
                Toast.makeText(this, "Por favor otorga los permisos necesarios.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permisos concedidos.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnIngresar = findViewById(R.id.btnIngresar)

        // Al iniciar, verificamos permisos
        requestNeededPermissions()

        btnIngresar.setOnClickListener {
            startActivity(Intent(this, MenuPrincipalActivity::class.java))
        }
    }

    // ============================
    // 🔹 Pide permisos requeridos
    // ============================
    private fun requestNeededPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // ===== ANDROID 12+ =====
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED
            ) permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED
            ) permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        // ===== Android <= 11 requiere ubicación =====
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}

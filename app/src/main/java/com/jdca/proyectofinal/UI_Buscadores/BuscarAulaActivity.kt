package com.jdca.proyectofinal.UI_Buscadores

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.jdca.proyectofinal.DB.DBHelper
import com.jdca.proyectofinal.R

class BuscarAulaActivity : AppCompatActivity() {

    private lateinit var txtCodAula: TextInputEditText
    private lateinit var btnBuscarAula: Button
    private lateinit var btnVolver: Button

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscaraula)

        txtCodAula = findViewById(R.id.txtCodAula)
        btnBuscarAula = findViewById(R.id.btnBuscarAula)
        btnVolver = findViewById(R.id.btnVolverBuscarAula)

        dbHelper = DBHelper(this)

        btnBuscarAula.setOnClickListener {
            val codigo = txtCodAula.text?.toString()?.trim()?.uppercase()

            if (codigo.isNullOrEmpty()) {
                Toast.makeText(this, "Ingresa un código de aula", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val aula = dbHelper.buscarAulaPorCodigo(codigo)

            if (aula == null) {
                Toast.makeText(this, "Aula no encontrada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Aula $codigo - Pabellón ${aula.pabellon} Piso ${aula.piso}",
                    Toast.LENGTH_LONG
                ).show()

            }
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }
}

package com.jdca.proyectofinal.UI_Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.jdca.proyectofinal.R
import com.jdca.proyectofinal.UI_Buscadores.BuscarAulaActivity
import com.jdca.proyectofinal.UI_Buscadores.EscanearAulaCercanaActivity
import com.jdca.proyectofinal.UI_Buscadores.ValidacionTecnicaActivity

class MenuPrincipalActivity : AppCompatActivity() {

    private lateinit var btnEscanearAula: Button
    private lateinit var btnBuscarAula: Button

    private lateinit var btnModoValidacion: Button
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_principal)

        btnEscanearAula = findViewById(R.id.btnEscanearAula)
        btnBuscarAula = findViewById(R.id.btnBuscarAula)
        btnVolver = findViewById(R.id.btnVolverBuscarAula)
        btnModoValidacion = findViewById(R.id.btnModoValidacion)

        btnEscanearAula.setOnClickListener {
            startActivity(Intent(this, EscanearAulaCercanaActivity::class.java))
        }


        btnBuscarAula.setOnClickListener {
            startActivity(Intent(this, BuscarAulaActivity::class.java))
        }
        btnVolver.setOnClickListener {
            finish()
        }
        btnModoValidacion.setOnClickListener {
            val intent = Intent(this, ValidacionTecnicaActivity::class.java)
            startActivity(intent)
        }
    }

}

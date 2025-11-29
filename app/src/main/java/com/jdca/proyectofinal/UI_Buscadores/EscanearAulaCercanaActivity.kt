package com.jdca.proyectofinal.UI_Buscadores

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jdca.proyectofinal.R
import com.jdca.proyectofinal.ble.BleScanner
import kotlin.math.pow

class EscanearAulaCercanaActivity : AppCompatActivity() {

    private lateinit var txtUbicacionActual: TextView
    private lateinit var btnScan: Button
    private lateinit var btnStop: Button
    private lateinit var btnVolver: Button
    private lateinit var btnVerMapa: Button   // NUEVO

    private lateinit var scanner: BleScanner

    private val lastRssi = mutableMapOf<String, Int>()
    private val lastPiso = mutableMapOf<String, Int>()

    private val RSSI_UMBRAL_CERCA = -65
    private val MARGEN_GANADOR = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_escanearaula)

        txtUbicacionActual = findViewById(R.id.txtUbicacionActual)
        btnScan = findViewById(R.id.btnScan)
        btnStop = findViewById(R.id.btnStop)
        btnVolver = findViewById(R.id.btnVolverBuscarAula)
        btnVerMapa = findViewById(R.id.btnVerMapa)   // NUEVO

        scanner = BleScanner(this)

        btnScan.setOnClickListener { iniciarEscaneo() }
        btnStop.setOnClickListener {
            scanner.stop()
            txtUbicacionActual.text = "Escaneo detenido"
        }
        btnVolver.setOnClickListener {
            finish()
        }

        btnVerMapa.setOnClickListener {
            startActivity(Intent(this, MapaActivity::class.java))
        }
    }

    private fun iniciarEscaneo() {
        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        if (btAdapter == null || !btAdapter.isEnabled) {
            txtUbicacionActual.text = "Bluetooth apagado"
            return
        }

        val locationOn = try {
            Settings.Secure.getInt(
                contentResolver,
                Settings.Secure.LOCATION_MODE
            ) != Settings.Secure.LOCATION_MODE_OFF
        } catch (e: Exception) { false }

        if (!locationOn) {
            txtUbicacionActual.text = "Activa Ubicación/GPS para detectar"
            Toast.makeText(this, "Activa Ubicación para detectar los beacons", Toast.LENGTH_LONG).show()
            return
        }

        txtUbicacionActual.text = "Detectando aula cercana..."

        scanner.start { codigo, piso, rssiAvg ->

            lastRssi[codigo] = rssiAvg
            lastPiso[codigo] = piso

            val cercanos = lastRssi.filterValues { it >= RSSI_UMBRAL_CERCA }
            if (cercanos.isEmpty()) {
                runOnUiThread {
                    txtUbicacionActual.text = "No hay beacons cercanos aún..."
                }
                return@start
            }

            val ordenados = cercanos.toList().sortedByDescending { it.second }
            val (ganadorCod, ganadorRssi) = ordenados.first()

            val segundoRssi = ordenados.getOrNull(1)?.second
            if (segundoRssi != null && (ganadorRssi - segundoRssi) < MARGEN_GANADOR) {
                runOnUiThread {
                    txtUbicacionActual.text =
                        "Cerca de varias aulas... ajustando señal"
                }
                return@start
            }

            val ganadorPiso = lastPiso[ganadorCod] ?: piso
            val distancia = estimarDistancia(ganadorRssi)

            runOnUiThread {
                txtUbicacionActual.text =
                    "Aula cercana: $ganadorCod (piso $ganadorPiso)\nRSSI $ganadorRssi dBm ~ ${"%.1f".format(distancia)} m"
            }
        }
    }

    private fun estimarDistancia(rssi: Int, txPower: Int = -59, n: Double = 2.0): Double {
        return 10.0.pow((txPower - rssi) / (10.0 * n))
    }
}

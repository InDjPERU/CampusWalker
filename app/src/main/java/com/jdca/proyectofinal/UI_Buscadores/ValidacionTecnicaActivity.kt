package com.jdca.proyectofinal.UI_Buscadores

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.jdca.proyectofinal.R
import com.jdca.proyectofinal.ble.BleScanner
import kotlin.math.abs
import kotlin.math.pow

class ValidacionTecnicaActivity : AppCompatActivity() {

    // UI
    private lateinit var spAulaReal: Spinner
    private lateinit var edtDistanciaReal: EditText
    private lateinit var btnIniciar: Button
    private lateinit var btnDetener: Button
    private lateinit var txtEstadoLecturas: TextView
    private lateinit var txtResumenErrores: TextView
    private lateinit var txtReporteCompleto: TextView

    private lateinit var btnVolverInfo: Button

    private lateinit var scanner: BleScanner


    private val lastRssi: MutableMap<String, Int> = mutableMapOf()
    private val RSSI_UMBRAL_CERCA = -90  // Para distancia

    // Config de muestras
    private val MAX_MUESTRAS = 100
    private var muestrasTomadas = 0

    // Muestras de error (en metros)
    private val errores: MutableList<Float> = mutableListOf()
    private var countMenos3 = 0
    private var count3a5 = 0
    private var count6a10 = 0
    private var countMas10 = 0

    // Distancia REAL introducida por el usuario
    private var distanciaRealUsuario: Float = 0f

    // Batería
    private var nivelInicio: Int? = null
    private var nivelFin: Int? = null
    private var tiempoInicioMs: Long = 0L
    private var tiempoFinMs: Long = 0L

    //
    private val handler = Handler(Looper.getMainLooper())
    private val timeoutRunnable = object : Runnable {
        override fun run() {

            handler.postDelayed(this, 5000L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validacion_tecnica)

        // Referencias UI
        spAulaReal = findViewById(R.id.spAulaReal)
        edtDistanciaReal = findViewById(R.id.edtDistanciaReal)
        btnIniciar = findViewById(R.id.btnIniciarPrueba)
        btnDetener = findViewById(R.id.btnDetenerPrueba)
        btnVolverInfo = findViewById(R.id.btnVolverAtras)
        txtEstadoLecturas = findViewById(R.id.txtEstadoLecturas)
        txtResumenErrores = findViewById(R.id.txtResumenErrores)
        txtReporteCompleto = findViewById(R.id.txtReporteCompleto)


        // Llenar spinner de aulas
        val aulas = listOf("Selecciona aula real", "D601", "D602", "D603", "D604", "D605")
        spAulaReal.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            aulas
        )

        scanner = BleScanner(this)

        btnIniciar.setOnClickListener { iniciarPrueba() }
        btnDetener.setOnClickListener { detenerYGnerarReporte() }
        btnVolverInfo.setOnClickListener { finish() }
    }


    private fun iniciarPrueba() {
        val aulaSeleccionada = spAulaReal.selectedItem as String
        if (aulaSeleccionada == "Selecciona aula real") {
            Toast.makeText(this, "Selecciona el aula REAL antes de iniciar.", Toast.LENGTH_LONG).show()
            return
        }

        // Leer distancia REAL del EditText
        val textoDist = edtDistanciaReal.text.toString().replace(",", ".")
        val distReal = textoDist.toFloatOrNull()
        if (distReal == null || distReal <= 0f) {
            Toast.makeText(this, "Ingresa una distancia REAL válida (en metros).", Toast.LENGTH_LONG).show()
            return
        }
        distanciaRealUsuario = distReal

        // Reset estado
        errores.clear()
        muestrasTomadas = 0
        countMenos3 = 0
        count3a5 = 0
        count6a10 = 0
        countMas10 = 0
        lastRssi.clear()

        txtResumenErrores.text = "Resumen de errores: recopilando datos..."
        txtReporteCompleto.text = "Recopilando muestras, espera algunos segundos..."

        // Requisitos de Bluetooth + ubicación
        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        if (btAdapter == null || !btAdapter.isEnabled) {
            Toast.makeText(this, "Enciende Bluetooth para la prueba.", Toast.LENGTH_LONG).show()
            return
        }
        val locationOn = try {
            Settings.Secure.getInt(
                contentResolver,
                Settings.Secure.LOCATION_MODE
            ) != Settings.Secure.LOCATION_MODE_OFF
        } catch (_: Exception) {
            false
        }
        if (!locationOn) {
            Toast.makeText(this, "Activa Ubicación/GPS para la prueba.", Toast.LENGTH_LONG).show()
            return
        }

        // Batería inicio
        val bm = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        nivelInicio = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        tiempoInicioMs = System.currentTimeMillis()

        txtEstadoLecturas.text =
            "Prueba en curso... Aula REAL: $aulaSeleccionada | Distancia real: ${"%.2f".format(distanciaRealUsuario)} m"

        handler.postDelayed(timeoutRunnable, 5000L)

        // Empezamos a escanear: solo nos interesa el RSSI del beacon del aula REAL
        scanner.start { codigo, piso, rssiAvg ->
            onBeaconReadingEnPrueba(aulaSeleccionada, codigo, rssiAvg)
        }
    }

    /** Detiene el escaneo y genera el resumen numérico y textual */
    private fun detenerYGnerarReporte() {
        scanner.stop()
        handler.removeCallbacks(timeoutRunnable)

        val bm = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        nivelFin = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        tiempoFinMs = System.currentTimeMillis()

        if (errores.isEmpty()) {
            txtEstadoLecturas.text = "No se tomaron muestras suficientes."
            txtResumenErrores.text = "Resumen de errores: sin datos."
            return
        }

        val total = errores.size
        val promedio = errores.sum() / total
        val maxError = errores.maxOrNull() ?: 0f

        val porcentajeMenos3 = countMenos3 * 100f / total
        val porcentaje3a5 = count3a5 * 100f / total
        val porcentaje6a10 = count6a10 * 100f / total
        val porcentajeMas10 = countMas10 * 100f / total

        val tiempoMin = (tiempoFinMs - tiempoInicioMs) / 60000f
        val consumoTexto = if (nivelInicio != null && nivelFin != null) {
            val diff = nivelInicio!! - nivelFin!!
            val porHora = if (tiempoMin > 0f) diff / (tiempoMin / 60f) else 0f
            "Consumo de batería durante la prueba: $diff% en ${"%.1f".format(tiempoMin)} min (≈ ${"%.2f".format(porHora)} %/hora)."
        } else {
            "No se pudo estimar el consumo de batería (datos no disponibles)."
        }

        txtResumenErrores.text =
            "Muestras: $total | Error prom.: ${"%.2f".format(promedio)} m | Máx: ${"%.2f".format(maxError)} m"

        val reporte = buildString {
            appendLine("Validación técnica de distancia usuario–beacon en el sistema IPS.")
            appendLine()
            appendLine("Aula REAL utilizada en la prueba: ${spAulaReal.selectedItem}.")
            appendLine("Distancia REAL ingresada por el usuario: ${"%.2f".format(distanciaRealUsuario)} metros.")
            appendLine("Total de muestras registradas: $total.")
            appendLine("Error promedio de estimación de distancia: ${"%.2f".format(promedio)} metros.")
            appendLine("Error máximo observado: ${"%.2f".format(maxError)} metros.")
            appendLine()
            appendLine("Distribución del margen de error (en metros):")
            appendLine("  • Menos de 3 m: $countMenos3 casos (${String.format("%.1f", porcentajeMenos3)}%).")
            appendLine("  • Entre 3 y 5 m: $count3a5 casos (${String.format("%.1f", porcentaje3a5)}%).")
            appendLine("  • Entre 6 y 10 m: $count6a10 casos (${String.format("%.1f", porcentaje6a10)}%).")
            appendLine("  • Más de 10 m: $countMas10 casos (${String.format("%.1f", porcentajeMas10)}%).")
            appendLine()
            appendLine("En conjunto, el ${String.format("%.1f", porcentajeMenos3 + porcentaje3a5)}% de las muestras")
            appendLine("presentó un error menor a 5 metros, lo que permite comparar directamente")
            appendLine("con las preferencias de margen de error declaradas por los usuarios en el cuestionario.")
            appendLine()
            appendLine(consumoTexto)
        }

        txtEstadoLecturas.text = "Prueba finalizada."
        txtReporteCompleto.text = reporte
    }

    /**
     * Se llama cada vez que se recibe un beacon en el escaneo.
     * Solo usamos el RSSI del aula REAL para estimar distancia.
     */
    private fun onBeaconReadingEnPrueba(
        aulaReal: String,
        codigoDetectado: String,
        rssiAvg: Int
    ) {
        // Guardamos el último RSSI de cada beacon
        lastRssi[codigoDetectado] = rssiAvg

        // Si ya completamos las muestras, no seguimos acumulando
        if (muestrasTomadas >= MAX_MUESTRAS) return

        // Tomar el RSSI del beacon del aula REAL
        val rssiAulaReal = lastRssi[aulaReal] ?: return
        if (rssiAulaReal < RSSI_UMBRAL_CERCA) return

        // Distancia estimada a partir de RSSI
        val distanciaEstimada = rssiToDistance(rssiAulaReal)

        // Error absoluto en metros
        val error = abs(distanciaRealUsuario - distanciaEstimada)

        synchronized(errores) {
            errores.add(error)
            muestrasTomadas++

            when {
                error < 3f  -> countMenos3++
                error < 5f  -> count3a5++
                error < 10f -> count6a10++
                else        -> countMas10++
            }
        }

        runOnUiThread {
            txtEstadoLecturas.text =
                "Lecturas: $muestrasTomadas/$MAX_MUESTRAS | RSSI: $rssiAulaReal dBm | " +
                        "Dist. est.: ${"%.2f".format(distanciaEstimada)} m | Error: ${"%.2f".format(error)} m"
        }

        // Si llegamos al máximo, paramos y generamos el reporte
        if (muestrasTomadas >= MAX_MUESTRAS) {
            runOnUiThread {
                detenerYGnerarReporte()
                Toast.makeText(
                    this,
                    "Prueba completada con $MAX_MUESTRAS lecturas.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun rssiToDistance(rssi: Int, txPower: Int = -59, n: Double = 2.0): Float {
        // d = 10^((TxPower - RSSI)/(10 * n))
        val exponent = (txPower - rssi) / (10.0 * n)
        return 10.0.pow(exponent).toFloat()
    }

    override fun onStop() {
        super.onStop()
        scanner.stop()
        handler.removeCallbacks(timeoutRunnable)
    }
}

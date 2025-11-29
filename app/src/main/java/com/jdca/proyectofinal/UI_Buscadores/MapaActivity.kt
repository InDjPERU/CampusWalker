package com.jdca.proyectofinal.UI_Buscadores

import android.bluetooth.BluetoothAdapter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jdca.proyectofinal.R
import com.jdca.proyectofinal.ble.BleScanner

class MapaActivity : AppCompatActivity() {

    private lateinit var zoomableMap: ZoomImageView
    private lateinit var mapOverlayView: MapOverlayView
    private lateinit var scanner: BleScanner
    private lateinit var smoother: PositionSmoother

    private lateinit var txtAulaActualMapa: TextView
    private lateinit var txtEstadoIPS: TextView
    private lateinit var btnVolverInfo: Button
    private lateinit var btnResetMapa: Button   // 🔹 botón reset

    // Desplazamiento desde la pared hacia el interior del pasillo
    private val DOOR_OFFSET_METERS = 1.5f

    // Posiciones reales (m) de cada puerta / beacon segun imagen
    private val beaconPositionsMeters = mapOf(
        // Pared derecha = +1.75m → desplazamos hacia adentro: 1.75 - offset
        "D603" to PointF(+1.75f - DOOR_OFFSET_METERS, 12.030f),  // B3
        "D602" to PointF(+1.75f - DOOR_OFFSET_METERS, 18.835f),  // B2
        "D601" to PointF(+1.75f - DOOR_OFFSET_METERS, 22.760f),  // B1

        // Pared izquierda = -1.75m → desplazamos hacia adentro: -1.75 + offset
        "D604" to PointF(-1.75f + DOOR_OFFSET_METERS, 12.030f),  // B4
        "D605" to PointF(-1.75f + DOOR_OFFSET_METERS, 21.645f)   // B5
    )

    // Etiquetas de aulas (usamos mismas coords de puerta)
    private val rooms = listOf(
        Room("D603", PointF(+1.75f - DOOR_OFFSET_METERS, 12.030f)),
        Room("D604", PointF(-1.75f + DOOR_OFFSET_METERS, 12.030f)),
        Room("D602", PointF(+1.75f - DOOR_OFFSET_METERS, 18.835f)),
        Room("D601", PointF(+1.75f - DOOR_OFFSET_METERS, 22.760f)),
        Room("D605", PointF(-1.75f + DOOR_OFFSET_METERS, 21.645f))
    )

    // Lógica RSSI
    private val lastRssi = mutableMapOf<String, Int>()
    private val RSSI_UMBRAL_CERCA = -80
    private val MARGEN_GANADOR = 4

    // Timeout de posicion
    private val POSITION_TIMEOUT_MS = 4000L
    private var lastUpdateTime: Long = 0L
    private val timeoutHandler = Handler(Looper.getMainLooper())
    private val timeoutRunnable = object : Runnable {
        override fun run() {
            checkPositionTimeout()
            timeoutHandler.postDelayed(this, 1000L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        // Views
        zoomableMap = findViewById(R.id.zoomableMap)
        mapOverlayView = findViewById(R.id.mapOverlay)
        txtAulaActualMapa = findViewById(R.id.txtAulaActualMapa)
        txtEstadoIPS = findViewById(R.id.txtEstadoIPS)
        btnVolverInfo = findViewById(R.id.btnVolverAtras)
        btnResetMapa = findViewById(R.id.btnResetMapa)   // 🔹 enlazar botón reset

        btnVolverInfo.setOnClickListener { finish() }

        btnResetMapa.setOnClickListener {
            // Restaurar zoom y posición inicial del mapa
            zoomableMap.resetZoom()
            // Reaplicar la matriz al overlay
            mapOverlayView.setImageMatrix(zoomableMap.imageMatrix)
        }

        smoother = PositionSmoother(alpha = 0.4f)
        scanner = BleScanner(this)

        txtAulaActualMapa.text = "Aula actual: ---"
        txtEstadoIPS.text = "Estado IPS: buscando señal..."

        // Configurar overlay
        mapOverlayView.beaconsMeters = beaconPositionsMeters
        mapOverlayView.rooms = rooms
        mapOverlayView.setColors(
            corridorColor = Color.BLACK,
            beaconColor = Color.RED,
            userColor = Color.BLUE,
            textColor = Color.BLACK
        )

        // Tamaño real de la imagen del plano
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.pabellon_d_piso6)
        mapOverlayView.setImageSize(bitmap.width, bitmap.height)

        // Sincronizar matriz (zoom/pan) del mapa con el overlay
        zoomableMap.matrixChangeListener = { matrix ->
            mapOverlayView.setImageMatrix(matrix)
        }

        // Forzar sincronización inicial (para que los puntos se vean sin tocar el mapa)
        zoomableMap.post {
            mapOverlayView.setImageMatrix(zoomableMap.imageMatrix)
        }
    }

    override fun onStart() {
        super.onStart()

        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        if (btAdapter == null || !btAdapter.isEnabled) {
            Toast.makeText(this, "Enciende Bluetooth para ver el mapa en tiempo real", Toast.LENGTH_LONG).show()
            txtEstadoIPS.text = "Estado IPS: Bluetooth apagado"
            return
        }

        val locationOn = try {
            Settings.Secure.getInt(
                contentResolver,
                Settings.Secure.LOCATION_MODE
            ) != Settings.Secure.LOCATION_MODE_OFF
        } catch (e: Exception) {
            false
        }

        if (!locationOn) {
            Toast.makeText(this, "Activa Ubicación/GPS para detectar beacons", Toast.LENGTH_LONG).show()
            txtEstadoIPS.text = "Estado IPS: ubicación desactivada"
            return
        }

        // Iniciar escaneo BLE
        scanner.start { codigo, piso, rssiAvg ->
            onBeaconReading(codigo, rssiAvg)
        }

        lastUpdateTime = 0L
        timeoutHandler.postDelayed(timeoutRunnable, 1000L)
    }

    override fun onStop() {
        super.onStop()
        scanner.stop()
        timeoutHandler.removeCallbacks(timeoutRunnable)
    }

    private fun onBeaconReading(codigo: String, rssiAvg: Int) {
        lastRssi[codigo] = rssiAvg
        lastUpdateTime = System.currentTimeMillis()

        // Beacons válidos y cercanos
        val cercanos = lastRssi.filter { (cod, rssi) ->
            beaconPositionsMeters.containsKey(cod) && rssi >= RSSI_UMBRAL_CERCA
        }

        if (cercanos.isEmpty()) {
            runOnUiThread {
                mapOverlayView.setUserPositionMeters(null)
                txtAulaActualMapa.text = "Aula actual: ---"
                txtEstadoIPS.text = "Estado IPS: sin señal de beacons"
            }
            return
        }

        val ordenados = cercanos.toList().sortedByDescending { it.second }
        val (primerCod, primerRssi) = ordenados.first()
        val segundoRssi = ordenados.getOrNull(1)?.second

        // Zona intermedia (pasillo)
        if (segundoRssi != null && kotlin.math.abs(primerRssi - segundoRssi) < MARGEN_GANADOR) {
            runOnUiThread {
                txtAulaActualMapa.text = "Aula actual: Entre aulas"
                txtEstadoIPS.text = "Estado IPS: señal inestable (zona intermedia)"
            }
            return
        }

        // Aula dominante
        val coords = beaconPositionsMeters[primerCod]
        val posSuavizada = smoother.smooth(coords)

        runOnUiThread {
            mapOverlayView.setUserPositionMeters(posSuavizada)
            txtAulaActualMapa.text = "Aula actual: $primerCod"
            txtEstadoIPS.text = "Estado IPS: Ubicación precisa"
        }
    }

    // Limpia la posición si se deja de recibir señal
    private fun checkPositionTimeout() {
        val last = lastUpdateTime
        if (last == 0L) return

        val now = System.currentTimeMillis()
        if (now - last > POSITION_TIMEOUT_MS) {
            lastUpdateTime = 0L
            lastRssi.clear()
            runOnUiThread {
                mapOverlayView.setUserPositionMeters(null)
                txtAulaActualMapa.text = "Aula actual: ---"
                txtEstadoIPS.text = "Estado IPS: Sin señal (tiempo excedido)"
            }
        }
    }
}

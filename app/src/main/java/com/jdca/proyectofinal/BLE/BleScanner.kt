package com.jdca.proyectofinal.ble  // ideal en minúscula

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import java.nio.charset.Charset
import kotlin.math.roundToInt

class BleScanner(private val context: Context) {

    private val btManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val adapter = btManager.adapter
    private val scanner: BluetoothLeScanner? get() = adapter?.bluetoothLeScanner

    private var callback: ScanCallback? = null
    private var scanning = false

    // Promedio corto por beacon
    private val rssiBuffer = mutableMapOf<String, MutableList<Int>>()

    fun start(onFound: (codigo: String, piso: Int, rssiAvg: Int) -> Unit) {
        val leScanner = scanner ?: run {
            Log.e("BleScanner", "BluetoothLeScanner es null")
            return
        }
        if (!adapter.isEnabled) {
            Log.e("BleScanner", "Bluetooth apagado")
            return
        }
        if (!tienePermisoScan()) {
            Log.e("BleScanner", "Sin permisos BLE/Location")
            return
        }
        if (scanning) return  // evita doble start

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        val filter = ScanFilter.Builder()
            .setManufacturerData(0xC1A5, byteArrayOf(), byteArrayOf())
            .build()

        callback = object : ScanCallback() {

            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val record = result.scanRecord ?: return
                val mData = record.manufacturerSpecificData ?: return
                val data = mData.get(0xC1A5) ?: return

                // data: [tipo][piso][len][codigo...]
                if (data.size < 3) return
                val tipo = data[0].toInt() and 0xFF
                if (tipo != 0x01) return

                val piso = data[1].toInt() and 0xFF
                val len = data[2].toInt() and 0xFF
                if (len <= 0 || data.size < 3 + len) return

                val codigoBytes = data.copyOfRange(3, 3 + len)
                val codigo = String(codigoBytes, Charset.forName("UTF-8")).trim().uppercase()

                // promedio RSSI
                val list = rssiBuffer.getOrPut(codigo) { mutableListOf() }
                list.add(result.rssi)
                if (list.size > 6) list.removeAt(0)

                val avg = list.average().roundToInt()
                onFound(codigo, piso, avg)
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e("BleScanner", "Scan failed: $errorCode")
            }
        }

        try {
            leScanner.startScan(listOf(filter), settings, callback)
            scanning = true
            Log.d("BleScanner", "Escaneo iniciado")
        } catch (se: SecurityException) {
            Log.e("BleScanner", "SecurityException startScan", se)
        }
    }

    fun stop() {
        val leScanner = scanner ?: return
        val cb = callback ?: return

        if (!tienePermisoScan()) return

        try {
            leScanner.stopScan(cb)
        } catch (se: SecurityException) {
            Log.e("BleScanner", "SecurityException stopScan", se)
        } finally {
            callback = null
            scanning = false
            rssiBuffer.clear()
        }
    }

    private fun tienePermisoScan(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}

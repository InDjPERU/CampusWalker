
# *Campus Walker*

### *Aplicación móvil de localización en interiores con Bluetooth Low Energy (BLE)*

Desarrollada para orientación en el *Pabellón D – Piso 6* de un campus universitario.

---

## 📱 **Descripción del Proyecto**

Este proyecto implementa un **sistema de posicionamiento en interiores (IPS)** basado en tecnología **Bluetooth Low Energy (BLE)**.
La aplicación detecta señales emitidas por beacons configurados con dispositivos **ESP32-WROOM**, calcula la intensidad de señal (**RSSI**) y determina la ubicación aproximada del usuario dentro de un pabellón.

Incluye:

* Detección del aula más cercana.
* Modo mapa con visualización de posición en tiempo real.
* Overlay interactivo con coordenadas reales.
* Suavizado de trayectoria para evitar saltos.
* Escaneo BLE optimizado con filtros por Manufacturer ID.
* Estimación de distancia basada en propagación logarítmica.

---

## 🧠 **Tecnologías Utilizadas**

### **Frontend móvil**

* Kotlin (Android Studio)
* ConstraintLayout / FrameLayout
* Canvas para renderizar posiciones
* ZoomImageView (custom)
* MapOverlayView (custom)

### **Backend BLE**

* ESP32-WROOM actuando como beacon BLE
* Manufacturer Data estructurado:
  `TIPO | PISO | LEN | CÓDIGO`

### **Lógica Matemática**

* Promedio móvil de RSSI (6 muestras)
* Modelo logarítmico de estimación de distancia:

```
d = 10 ^ ((TxPower - RSSI) / (10 * n))
```

* Selección del beacon dominante con margen de comparación
* Suavizado exponencial (EMA):

```
Psuave = α * Pnueva + (1 - α) * Panterior
```

---

## 🗺️ **Características principales**

### ✔️ Búsqueda de aula cercana

* Detecta beacons cercanos
* Filtra por umbral RSSI
* Selecciona el aula más probable

### ✔️ Mapa interactivo

* Zoom y desplazamiento
* Overlay sincronizado con coordenadas
* Detección en tiempo real

### ✔️ Sistema IPS completo

* Gestión de señales débiles
* Timeout automático si no hay beacons
* Indicador de estabilidad de señal

### ✔️ Gestión visual del usuario

* Posición en el plano
* Aula detectada
* Estado del escaneo BLE

---

## 🗂️ **Estructura del Proyecto**

```
/app
 ├── java/com.jdca.proyectofinal
 │     ├── ble/
 │     │     └── BleScanner.kt
 │     ├── UI_Buscadores/
 │     │     ├── EscanearAulaCercanaActivity.kt
 │     │     ├── MapaActivity.kt
 │     │     ├── ZoomImageView.kt
 │     │     └── MapOverlayView.kt
 │     └── utils/
 │
 └── res/
       ├── layout/
       │     └── activity_mapa.xml
       ├── drawable/
       │     └── pabellon_d_piso6.png
       └── values/
```

---

## 🛠️ **Requisitos del Sistema**

### **Dispositivo móvil**

* Android 8.0 (API 26) o superior
* Bluetooth 4.2 o superior
* GPS / Ubicación habilitada
* 2GB RAM mínimo
* Permiso `BLUETOOTH_SCAN` o `ACCESS_FINE_LOCATION` (según API)

### **Beacons**

* ESP32-WROOM
* Emisión de manufacturer data ID: `0xC1A5`
* Intervalo de advertising recomendado: 300–500 ms

---

## 🚀 **Instalación del APK**

1. Descargar la versión más reciente desde **Releases**.
2. Activar instalación desde orígenes desconocidos.
3. Abrir el APK e instalar.
4. Aceptar permisos solicitados:
   ✔ Bluetooth
   ✔ Ubicación

---

## 📦 **Última versión publicada**

👉 **Versión Final**
Incluye IPS, mapa en tiempo real, suavizado y optimización del escaneo BLE.

---

## 👥 **Equipo de Desarrollo**

* **Carlos Enrique Chapilliquen Vela**
* **Leonardo Manuel Sihuay Jiménez**
* **Gerson Pastor Lozano**

---

## 📄 **Licencia**

Este proyecto se publica solo con fines académicos.
No está destinado para distribución comercial sin autorización.

---

Si deseas también puedo prepararte:

✅ **CHANGELOG**
✅ **Manual de usuario en PDF**
✅ **Manual técnico**
✅ **README en inglés**
Solo pídelo.

package com.jdca.proyectofinal.UI_Buscadores

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

// Dimensiones reales del piso 6
const val WORLD_WIDTH_METERS  = 3.50f
const val WORLD_HEIGHT_METERS = 29.416f

// Conversión metros → píxeles en el espacio de la IMAGEN
fun PointF.toImagePixels(imageWidth: Int, imageHeight: Int): PointF {
    val xPx = (this.x + WORLD_WIDTH_METERS / 2f) / WORLD_WIDTH_METERS * imageWidth
    val yPx = this.y / WORLD_HEIGHT_METERS * imageHeight
    return PointF(xPx, yPx)
}

data class Room(
    val name: String,
    val doorPositionMeters: PointF
)

class MapOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val corridorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val wallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }

    private val beaconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val beaconBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = Color.WHITE
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 46f   // ← prueba con 42, 46, 50, etc.
        color = Color.BLACK
        typeface = Typeface.DEFAULT_BOLD // opcional: más legible
    }


    // Pinceles para el usuario (punto azul + halo)
    private val userCenterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLUE
    }

    private val userHaloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = 4f
    }

    // Tamaño REAL de la imagen base (px)
    private var imageWidthPx: Int = 0
    private var imageHeightPx: Int = 0

    // Matriz del ZoomImageView
    private val drawMatrix = Matrix()

    var beaconsMeters: Map<String, PointF> = emptyMap()
        set(value) {
            field = value
            invalidate()
        }

    var rooms: List<Room> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    private var userPositionMeters: PointF? = null

    // Animación de pulso del usuario
    private var pulseScale: Float = 1f
    private val pulseAnimator: ValueAnimator = ValueAnimator.ofFloat(1f, 1.6f).apply {
        duration = 1100L
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener { anim ->
            pulseScale = anim.animatedValue as Float
            invalidate()
        }
    }

    fun setUserPositionMeters(position: PointF?) {
        userPositionMeters = position
        invalidate()
    }

    fun setColors(
        corridorColor: Int,
        beaconColor: Int,
        userColor: Int,
        textColor: Int
    ) {
        corridorPaint.color = corridorColor
        wallPaint.color = corridorColor
        beaconPaint.color = beaconColor
        userCenterPaint.color = userColor
        textPaint.color = textColor
        invalidate()
    }

    fun setImageSize(widthPx: Int, heightPx: Int) {
        imageWidthPx = widthPx
        imageHeightPx = heightPx
        invalidate()
    }

    fun setImageMatrix(matrix: Matrix) {
        drawMatrix.set(matrix)
        invalidate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!pulseAnimator.isStarted) {
            pulseAnimator.start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        pulseAnimator.cancel()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (imageWidthPx == 0 || imageHeightPx == 0) return

        // Alinea el overlay con la imagen del ZoomImageView
        canvas.save()
        canvas.concat(drawMatrix)

        drawCorridor(canvas)
        drawFinalWall(canvas)
        drawBeacons(canvas)
        drawUser(canvas)
        drawRoomLabels(canvas)

        canvas.restore()
    }

    private fun drawCorridor(canvas: Canvas) {
        val rect = RectF(0f, 0f, imageWidthPx.toFloat(), imageHeightPx.toFloat())
        canvas.drawRect(rect, corridorPaint)
    }

    private fun drawFinalWall(canvas: Canvas) {
        val yMeters = WORLD_HEIGHT_METERS
        val yPx = yMeters / WORLD_HEIGHT_METERS * imageHeightPx
        canvas.drawLine(
            0f, yPx,
            imageWidthPx.toFloat(), yPx,
            wallPaint
        )
    }

    private fun drawBeacons(canvas: Canvas) {
        for ((codigo, posMeters) in beaconsMeters) {
            val posPx = posMeters.toImagePixels(imageWidthPx, imageHeightPx)

            val beaconRadius = 40f

            // círculo rojo del beacon
            canvas.drawCircle(
                posPx.x,
                posPx.y,
                beaconRadius,
                beaconPaint
            )

            // borde blanco del beacon
            canvas.drawCircle(
                posPx.x,
                posPx.y,
                beaconRadius,
                beaconBorderPaint
            )

        }
    }

    private fun drawUser(canvas: Canvas) {
        val userMeters = userPositionMeters ?: return
        val posPx = userMeters.toImagePixels(imageWidthPx, imageHeightPx)

        // radio base en coordenadas de imagen (se escalará con el zoom)
        val centerRadius = 18f
        val haloRadius = centerRadius * pulseScale

        // halo pulsante
        userHaloPaint.alpha = 80   // un poquito transparente
        canvas.drawCircle(posPx.x, posPx.y, haloRadius, userHaloPaint)

        // punto azul fijo en el centro
        userCenterPaint.alpha = 255
        canvas.drawCircle(posPx.x, posPx.y, centerRadius, userCenterPaint)
    }

    private fun drawRoomLabels(canvas: Canvas) {
        for (room in rooms) {
            val posPx = room.doorPositionMeters.toImagePixels(imageWidthPx, imageHeightPx)

            // Offset distinto según el aula
            val (dx, dy) = when (room.name) {
                // Estas dos van a la IZQUIERDA del punto
                "D604", "D605" -> Pair(-140f, +10f)
                // El resto a la DERECHA, como antes
                else           -> Pair(+50f, +10f)
            }

            canvas.drawText(
                room.name,
                posPx.x + dx,
                posPx.y + dy,
                textPaint
            )
        }
    }


}

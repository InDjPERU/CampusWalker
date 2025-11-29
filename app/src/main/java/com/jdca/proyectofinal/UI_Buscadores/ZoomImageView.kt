package com.jdca.proyectofinal.UI_Buscadores

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.max
import kotlin.math.min

class ZoomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private val matrixValues = FloatArray(9)
    private var scaleDetector: ScaleGestureDetector
    private var gestureDetector: GestureDetector

    private var minScale = 1f
    private var maxScale = 4f

    // Listener para notificar cambios de matriz (para el MapOverlayView)
    var matrixChangeListener: ((Matrix) -> Unit)? = null

    init {
        scaleType = ScaleType.MATRIX

        // Zoom con gesto de pinza
        scaleDetector = ScaleGestureDetector(
            context,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val scaleFactor = detector.scaleFactor
                    val currentScale = getCurrentScale()

                    var newScale = currentScale * scaleFactor
                    newScale = max(minScale, min(newScale, maxScale))

                    val factor = newScale / currentScale
                    imageMatrix.postScale(
                        factor,
                        factor,
                        detector.focusX,
                        detector.focusY
                    )
                    notifyMatrixChanged()
                    return true
                }
            }
        )

        // Arrastrar
        gestureDetector = GestureDetector(
            context,
            object : GestureDetector.SimpleOnGestureListener() {

                override fun onDown(e: MotionEvent): Boolean {
                    // Necesario para que onScroll se dispare correctamente
                    return true
                }

                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    imageMatrix.postTranslate(-distanceX, -distanceY)
                    notifyMatrixChanged()
                    return true
                }
            }
        )
    }

    private fun getCurrentScale(): Float {
        imageMatrix.getValues(matrixValues)
        return matrixValues[Matrix.MSCALE_X]
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val handledScale = scaleDetector.onTouchEvent(event)
        val handledScroll = gestureDetector.onTouchEvent(event)
        return handledScale || handledScroll || super.onTouchEvent(event)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        fitImageToView()
    }

    private fun fitImageToView() {
        val d = drawable ?: return

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val imageWidth = d.intrinsicWidth.toFloat()
        val imageHeight = d.intrinsicHeight.toFloat()

        if (viewWidth <= 0 || viewHeight <= 0 || imageWidth <= 0 || imageHeight <= 0) return

        val scale = min(viewWidth / imageWidth, viewHeight / imageHeight)

        val dx = (viewWidth - imageWidth * scale) / 2f
        val dy = (viewHeight - imageHeight * scale) / 2f

        val m = Matrix()
        m.postScale(scale, scale)
        m.postTranslate(dx, dy)

        imageMatrix = m
        minScale = scale
        notifyMatrixChanged()
    }

    private fun notifyMatrixChanged() {
        matrixChangeListener?.invoke(Matrix(imageMatrix))
        invalidate()
    }

    // Metodo público para restaurar zoom y posición a su estado original
    fun resetZoom() {
        fitImageToView()
    }
}

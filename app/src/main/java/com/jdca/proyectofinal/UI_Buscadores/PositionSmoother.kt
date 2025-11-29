package com.jdca.proyectofinal.UI_Buscadores

import android.graphics.PointF

class PositionSmoother(
    private val alpha: Float = 0.4f  // 0 = muy suave, 1 = sin suavizado
) {
    private var lastPosition: PointF? = null

    fun smooth(newPosition: PointF?): PointF? {
        if (newPosition == null) return lastPosition

        val last = lastPosition
        if (last == null) {
            lastPosition = newPosition
            return newPosition
        }

        val x = alpha * newPosition.x + (1 - alpha) * last.x
        val y = alpha * newPosition.y + (1 - alpha) * last.y
        val result = PointF(x, y)
        lastPosition = result
        return result
    }
}

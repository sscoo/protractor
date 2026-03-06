package com.example.protractoroverlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class ProtractorOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val baselinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeWidth = 8f
        style = Paint.Style.STROKE
    }

    private val armPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        strokeWidth = 10f
        style = Paint.Style.STROKE
    }

    private val jointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private var armAngleDeg = 45f
    private var listener: ((Float) -> Unit)? = null

    fun setOnAngleChangeListener(onAngleChange: (Float) -> Unit) {
        listener = onAngleChange
        onAngleChange(armAngleDeg)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desired = 420
        val width = resolveSize(desired, widthMeasureSpec)
        val height = resolveSize(desired, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width * 0.20f
        val cy = height * 0.80f
        val length = (width.coerceAtMost(height) * 0.65f)

        val baselineEndX = cx + length
        val baselineEndY = cy
        canvas.drawLine(cx, cy, baselineEndX, baselineEndY, baselinePaint)

        val radians = Math.toRadians(armAngleDeg.toDouble())
        val armEndX = cx + (length * cos(radians)).toFloat()
        val armEndY = cy - (length * sin(radians)).toFloat()
        canvas.drawLine(cx, cy, armEndX, armEndY, armPaint)

        canvas.drawCircle(cx, cy, 11f, jointPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                val cx = width * 0.20f
                val cy = height * 0.80f
                val dx = event.x - cx
                val dy = cy - event.y

                if (dx >= 0f || dy > 0f) {
                    var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                    if (angle < 0f) angle += 360f
                    armAngleDeg = angle.coerceIn(0f, 180f)
                    listener?.invoke(armAngleDeg)
                    invalidate()
                }
                true
            }

            else -> super.onTouchEvent(event)
        }
    }
}

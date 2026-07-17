package de.peptidrechner.app.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

/**
 * Stilisierte, horizontale Insulinspritze mit sauberer Zahlenskala (IE):
 * jede Zahl hat einen Strich darunter, dazwischen feine Zwischenstriche.
 * Der Farbverlauf zeigt die berechnete Füllmenge, ein dezenter Zeiger
 * markiert den genauen Wert.
 */
@Composable
fun SyringeView(
    fillUnits: Double,
    maxUnits: Int,
    barrelColor: Color,
    liquidColor: Color,
    tickColor: Color,
    modifier: Modifier = Modifier,
    liquidColorEnd: Color = liquidColor,
    labelColor: Color = tickColor,
) {
    val fraction = (fillUnits / maxUnits).coerceIn(0.0, 1.0).toFloat()
    val animated by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(durationMillis = 550),
        label = "syringeFill",
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp),
    ) {
        val h = size.height
        val w = size.width

        val topBand = h * 0.34f
        val syTop = topBand
        val syH = h - topBand
        val cy = syTop + syH / 2f

        val plungerEnd = w * 0.12f
        val needleLen = w * 0.08f
        val hubW = w * 0.03f
        val barrelStart = plungerEnd
        val barrelEnd = w - needleLen - hubW
        val barrelW = barrelEnd - barrelStart
        val barrelH = syH * 0.5f
        val barrelTop = cy - barrelH / 2f
        val radius = CornerRadius(barrelH * 0.42f, barrelH * 0.42f)

        // Mess-Achse INNERHALB des geraden Zylinderbereichs (0 und Max sauber innen)
        val inset = barrelH * 0.55f
        val axisStart = barrelStart + inset
        val axisEnd = barrelEnd - inset
        val axisW = axisEnd - axisStart
        fun unitX(u: Float) = axisStart + axisW * (u / maxUnits)

        val fillX = axisStart + axisW * animated

        // --- Nadel ---
        drawLine(
            color = barrelColor,
            start = Offset(barrelEnd + hubW, cy),
            end = Offset(w, cy),
            strokeWidth = syH * 0.05f,
            cap = StrokeCap.Round,
        )
        drawPath(
            Path().apply {
                moveTo(barrelEnd, cy - barrelH * 0.30f)
                lineTo(barrelEnd + hubW, cy - barrelH * 0.16f)
                lineTo(barrelEnd + hubW, cy + barrelH * 0.16f)
                lineTo(barrelEnd, cy + barrelH * 0.30f)
                close()
            },
            color = barrelColor,
        )

        // --- Kolbenstange + Daumenstütze ---
        drawLine(
            color = barrelColor,
            start = Offset(w * 0.02f, cy),
            end = Offset(barrelStart + barrelW * 0.04f, cy),
            strokeWidth = syH * 0.12f,
            cap = StrokeCap.Round,
        )
        drawRoundRect(
            color = barrelColor,
            topLeft = Offset(0f, cy - barrelH * 0.55f),
            size = Size(w * 0.028f, barrelH * 1.1f),
            cornerRadius = CornerRadius(w * 0.014f, w * 0.014f),
        )

        // --- Zylinder-Hintergrund ---
        drawRoundRect(
            color = tickColor.copy(alpha = 0.16f),
            topLeft = Offset(barrelStart, barrelTop),
            size = Size(barrelW, barrelH),
            cornerRadius = radius,
        )

        // --- Flüssigkeit (Farbverlauf), am Zylinder geclippt ---
        val barrelRound = Path().apply {
            addRoundRect(RoundRect(Rect(Offset(barrelStart, barrelTop), Size(barrelW, barrelH)), radius))
        }
        clipPath(barrelRound) {
            // Bei ~Vollfüllung bis zum Zylinderende, sonst bis zum Skalenwert.
            val liquidRight = if (animated >= 0.985f) barrelEnd else fillX
            val liquidW = liquidRight - barrelStart
            if (liquidW > 0.5f) {
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(liquidColor, liquidColorEnd),
                        startX = barrelStart,
                        endX = liquidRight,
                    ),
                    topLeft = Offset(barrelStart, barrelTop),
                    size = Size(liquidW, barrelH),
                )
            }
            // dezentes Glanzlicht
            drawRoundRect(
                color = Color.White.copy(alpha = 0.22f),
                topLeft = Offset(barrelStart + barrelH * 0.25f, barrelTop + barrelH * 0.17f),
                size = Size(barrelW - barrelH * 0.5f, barrelH * 0.16f),
                cornerRadius = CornerRadius(barrelH * 0.08f, barrelH * 0.08f),
            )
        }

        // --- Skala: Zwischenstriche + Hauptstriche (unter jeder Zahl) ---
        val step = if (maxUnits > 50) 20 else 10
        val minor = if (maxUnits > 50) 10 else 5
        val tickTop = barrelTop + barrelH * 0.14f
        // Zwischenstriche
        run {
            var u = 0
            while (u <= maxUnits) {
                if (u % step != 0) {
                    val x = unitX(u.toFloat())
                    drawLine(
                        color = tickColor.copy(alpha = 0.4f),
                        start = Offset(x, tickTop),
                        end = Offset(x, tickTop + barrelH * 0.22f),
                        strokeWidth = syH * 0.016f,
                        cap = StrokeCap.Round,
                    )
                }
                u += minor
            }
        }

        // Zahlen + Hauptstrich direkt darunter
        val scalePaint = Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            color = labelColor.toArgb()
            textSize = 9.5f.sp.toPx()
            typeface = Typeface.MONOSPACE
        }
        val numberBaseline = barrelTop - syH * 0.10f
        val hideNear = 15.sp.toPx()
        run {
            var m = 0
            while (m <= maxUnits) {
                val x = unitX(m.toFloat())
                val nearPointer = abs(x - fillX) <= hideNear
                // Hauptstrich (länger) – der Strich unter der Zahl
                drawLine(
                    color = labelColor.copy(alpha = if (nearPointer) 0f else 0.85f),
                    start = Offset(x, tickTop),
                    end = Offset(x, tickTop + barrelH * 0.42f),
                    strokeWidth = syH * 0.022f,
                    cap = StrokeCap.Round,
                )
                if (!nearPointer) {
                    drawContext.canvas.nativeCanvas.drawText(m.toString(), x, numberBaseline, scalePaint)
                }
                m += step
            }
        }

        // --- Zylinder-Umriss (oben drüber, damit alles sauber begrenzt ist) ---
        drawRoundRect(
            color = barrelColor,
            topLeft = Offset(barrelStart, barrelTop),
            size = Size(barrelW, barrelH),
            cornerRadius = radius,
            style = Stroke(width = syH * 0.045f),
        )

        // --- Füllstands-Zeiger: dezente Linie + Dreieck + Wert ---
        if (animated > 0.005f) {
            // dünne Trennlinie am Füllstand
            if (animated < 0.985f) {
                drawLine(
                    color = Color.White.copy(alpha = 0.9f),
                    start = Offset(fillX, barrelTop + barrelH * 0.12f),
                    end = Offset(fillX, barrelTop + barrelH * 0.88f),
                    strokeWidth = syH * 0.02f,
                    cap = StrokeCap.Round,
                )
            }
            // Dreieck über dem Zylinder
            val triH = syH * 0.10f
            val triW = syH * 0.085f
            val triTip = barrelTop - syH * 0.01f
            drawPath(
                Path().apply {
                    moveTo(fillX, triTip)
                    lineTo(fillX - triW, triTip - triH)
                    lineTo(fillX + triW, triTip - triH)
                    close()
                },
                color = liquidColorEnd,
            )
            // Wert über dem Dreieck (in der Zahlenreihe)
            val valuePaint = Paint().apply {
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
                color = Color.White.toArgb()
                textSize = 12.sp.toPx()
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            }
            val valueText = if (fillUnits % 1.0 == 0.0) fillUnits.toInt().toString()
            else String.format(java.util.Locale.GERMANY, "%.1f", fillUnits)
            drawContext.canvas.nativeCanvas.drawText(
                valueText,
                fillX.coerceIn(axisStart, axisEnd),
                numberBaseline,
                valuePaint,
            )
        }
    }
}

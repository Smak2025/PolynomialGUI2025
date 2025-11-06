package ru.gr05307.painting

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import ru.gr05307.ui.convertation.Converter
import ru.gr05307.ui.convertation.Plain
import java.math.MathContext

class CartesianPainter(
    val plain: Plain
) {
    var measurer: TextMeasurer? = null
    var axesColor: Color = Color.Black
    var labelColor: Color = Color.Red
    var minTickColor: Color
        get() = TickType.MIN.color
        set(value){
            TickType.MIN.color = value
        }

    var midTickColor: Color
        get() = TickType.MID.color
        set(value){
            TickType.MID.color = value
        }
    var maxTickColor: Color
        get() = TickType.MAX.color
        set(value){
            TickType.MAX.color = value
        }

    fun draw(scope: DrawScope){
        drawAxes(scope)
        measurer?.let{
            drawXTicks(scope, it)
            drawYTicks(scope, it)
        }

    }

    private fun drawXTicks(scope: DrawScope, measurer: TextMeasurer) {
        var xDot = plain.xMin
        while (xDot <= plain.xMax){
            val xPos = Converter.xCrt2Scr(xDot, plain)
            drawXTick(scope, xPos, when {
                (xDot * 10).toInt() % 10 == 0 -> {
                    if (xDot != 0.0) drawXLabel(scope, measurer, xPos, xDot.toString())
                    TickType.MAX
                }
                (xDot * 10).toInt() % 5 == 0 -> TickType.MID
                else -> TickType.MIN
            })
            xDot = (xDot + 0.1).toBigDecimal().round(MathContext.DECIMAL32).setScale(1).toDouble()
        }

    }

    private fun drawXTick(scope: DrawScope, pos: Float, type: TickType) {
        Converter.yCrt2Scr(.0, plain).let { zero ->
            scope.drawLine(
                type.color,
                Offset(pos, zero+type.halfSize),
                Offset(pos, zero-type.halfSize)
            )
        }
    }


    private fun drawYTicks(scope: DrawScope, measurer: TextMeasurer, ) {
        var yDot = plain.yMin
        while (yDot <= plain.yMax){
            val yPos = Converter.yCrt2Scr(yDot, plain)
            drawYTick(scope, yPos, when {
                (yDot * 10).toInt() % 10 == 0 ->{
                    if (yDot != 0.0) drawYLabel(scope, measurer, yPos, yDot.toString())
                    TickType.MAX
                }
                (yDot * 10).toInt() % 5 == 0 -> TickType.MID
                else -> TickType.MIN
            })
            yDot = (yDot + 0.1).toBigDecimal().round(MathContext.DECIMAL32).setScale(1).toDouble()
        }
    }

    private fun drawYTick(scope: DrawScope, pos: Float, type: TickType) {
        Converter.xCrt2Scr(.0, plain).let { zero ->
            scope.drawLine(
                type.color,
                Offset(zero+type.halfSize, pos),
                Offset(zero-type.halfSize, pos)
            )
        }
    }

    private fun drawXLabel(
        scope: DrawScope,
        measurer: TextMeasurer,
        pos: Float,
        value: String
    ){
        scope.drawText(measurer, value, Offset(
            pos - measurer.measure(value).size.width/2f,
            Converter.yCrt2Scr(0.0, plain) + TickType.MAX.halfSize + 1
        ), maxLines = 1, style = TextStyle(labelColor)
        )
    }

    private fun drawYLabel(
        scope: DrawScope,
        measurer: TextMeasurer,
        pos: Float,
        value: String
        ){
            scope.drawText(measurer, value, Offset(
                Converter.xCrt2Scr(0.0, plain) + TickType.MAX.halfSize + 1,
                pos - measurer.measure(value).size.height/2f,

            ), maxLines = 1, style = TextStyle(labelColor)
            )
    }

    private fun drawAxes(scope: DrawScope) {
        Converter.yCrt2Scr(.0, plain).let { zero ->
            scope.drawLine(
                axesColor,
                Offset(0f, zero),
                Offset(plain.width, zero)
            )
        }
        Converter.xCrt2Scr(.0, plain).let { zero ->
            scope.drawLine(
                axesColor,
                Offset(zero, 0f),
                Offset(zero, plain.height)
            )
        }
    }


}

enum class TickType(var color: Color, var halfSize: Float){
    MIN(Color.Black, 3f),
    MID(Color.Blue, 5f),
    MAX(Color.Red, 8f)
}
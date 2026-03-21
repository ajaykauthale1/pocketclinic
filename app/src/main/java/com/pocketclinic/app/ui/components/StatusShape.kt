package com.pocketclinic.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketclinic.app.ui.theme.PrimaryAmber

/**
 * Terminal-style status indicator:
 *   normal  → white  "[ ● ]"
 *   flagged → amber  "[ ▲ ]"
 */
@Composable
fun StatusShape(
    flagged: Boolean,
    modifier: Modifier = Modifier,
) {
    val color = if (flagged) PrimaryAmber else Color.White
    val symbol = if (flagged) "[ ▲ ]" else "[ ● ]"
    val tag = if (flagged) "status_flagged" else "status_normal"

    val measurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .size(width = 56.dp, height = 20.dp)
            .testTag(tag),
    ) {
        drawStatusText(measurer, symbol, color)
    }
}

private fun DrawScope.drawStatusText(
    measurer: TextMeasurer,
    text: String,
    color: Color,
) {
    val result = measurer.measure(
        text = text,
        style = TextStyle(
            color      = color,
            fontSize   = 13.sp,
            fontFamily = FontFamily.Monospace,
        ),
    )
    drawText(
        textLayoutResult = result,
        topLeft          = Offset(
            x = (size.width - result.size.width) / 2f,
            y = (size.height - result.size.height) / 2f,
        ),
    )
}

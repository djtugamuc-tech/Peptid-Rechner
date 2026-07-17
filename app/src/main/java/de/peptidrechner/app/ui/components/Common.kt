package de.peptidrechner.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.peptidrechner.app.ui.theme.Brand

/** Weiße, glasige Karte mit dünnem Rand und lila-getöntem Schatten (biohk-Look). */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    radius: Int = 24,
    padding: PaddingValues = PaddingValues(18.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 18.dp,
                shape = RoundedCornerShape(radius.dp),
                ambientColor = Brand.Primary.copy(alpha = 0.18f),
                spotColor = Brand.Primary.copy(alpha = 0.18f),
            )
            .clip(RoundedCornerShape(radius.dp))
            .background(Brand.CardBg)
            .padding(padding),
        content = content,
    )
}

/** Kleines Kategorie-/Status-Label als abgerundete Pille. */
@Composable
fun Pill(
    text: String,
    modifier: Modifier = Modifier,
    bg: Color = Brand.Primary.copy(alpha = 0.10f),
    fg: Color = Brand.Primary,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 5.dp),
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelMedium,
            color = fg,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
        )
    }
}

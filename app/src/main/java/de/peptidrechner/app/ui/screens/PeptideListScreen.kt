package de.peptidrechner.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.peptidrechner.app.data.Peptide
import de.peptidrechner.app.data.PeptideCategory
import de.peptidrechner.app.data.PeptideRepository
import de.peptidrechner.app.ui.components.GlassCard
import de.peptidrechner.app.ui.components.Pill
import de.peptidrechner.app.ui.theme.Brand
import de.peptidrechner.app.ui.theme.JetMono

@Composable
fun PeptideListScreen(
    title: String,
    subtitle: String,
    onPeptideSelected: (Peptide) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val results = remember(query) { PeptideRepository.search(query) }
    val grouped = remember(results) {
        results.groupBy { it.category }.toSortedMap(compareBy { it.ordinal })
    }
    val statusBar = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(Modifier.fillMaxSize().background(Brand.Bg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                HeroHeader(title, subtitle, topInset = statusBar) {
                    SearchField(query = query, onQueryChange = { query = it })
                }
            }

            if (results.isEmpty()) {
                item {
                    Text(
                        "Kein Peptid gefunden für \"$query\".",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Brand.TextLight,
                        modifier = Modifier.padding(24.dp),
                    )
                }
            }

            grouped.forEach { (category, peptides) ->
                item(key = "h_${category.name}") { CategoryHeader(category, peptides.size) }
                items(peptides, key = { it.name }) { peptide ->
                    PeptideRow(
                        peptide = peptide,
                        onClick = { onPeptideSelected(peptide) },
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroHeader(
    title: String,
    subtitle: String,
    topInset: androidx.compose.ui.unit.Dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brand.hero)
            .padding(top = topInset + 28.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
    ) {
        Column {
            Text(
                title,
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.72f),
            )
            Spacer(Modifier.height(18.dp))
            content()
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Peptid suchen …", color = Color.White.copy(alpha = 0.6f)) },
        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White.copy(alpha = 0.8f)) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, "Löschen", tint = Color.White.copy(alpha = 0.8f))
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color.White.copy(alpha = 0.10f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
            focusedBorderColor = Color.White.copy(alpha = 0.5f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.18f),
            cursorColor = Color.White,
        ),
    )
}

@Composable
private fun CategoryHeader(category: PeptideCategory, count: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 2.dp),
    ) {
        Text(category.emoji, fontSize = 16.sp)
        Spacer(Modifier.width(8.dp))
        Text(
            category.title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = Brand.TextMuted,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.width(8.dp))
        Pill("$count")
    }
}

@Composable
private fun PeptideRow(peptide: Peptide, onClick: () -> Unit, modifier: Modifier = Modifier) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        radius = 20,
        padding = PaddingValues(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GradientAvatar(letter = peptide.name.take(1).uppercase())
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    peptide.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Brand.TextStrong,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    peptide.aka ?: peptide.category.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Brand.TextLight,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    peptide.doseRange,
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = JetMono,
                    color = Brand.Primary,
                )
            }
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Brand.Primary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Öffnen",
                    tint = Brand.Primary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
fun GradientAvatar(letter: String, size: Int = 46) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Brand.primaryToAccent),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            letter,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
    }
}

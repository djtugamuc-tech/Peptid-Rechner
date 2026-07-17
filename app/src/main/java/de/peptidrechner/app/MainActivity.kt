package de.peptidrechner.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import de.peptidrechner.app.data.Peptide
import de.peptidrechner.app.data.PeptideRepository
import de.peptidrechner.app.data.reminder.ReminderStore
import de.peptidrechner.app.data.tracking.TrackingRepository
import de.peptidrechner.app.notify.Notifications
import de.peptidrechner.app.ui.screens.CalculatorScreen
import de.peptidrechner.app.ui.screens.InfoScreen
import de.peptidrechner.app.ui.screens.PeptideListScreen
import de.peptidrechner.app.ui.screens.TrackerScreen
import de.peptidrechner.app.ui.screens.WikiScreen
import de.peptidrechner.app.ui.theme.AppC
import de.peptidrechner.app.ui.theme.Brand
import de.peptidrechner.app.ui.theme.PeptidRechnerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        TrackingRepository.init(this)
        ReminderStore.init(this)
        Notifications.ensureChannel(this)
        setContent {
            PeptidRechnerTheme {
                AppShell()
            }
        }
    }
}

private enum class Tab(val label: String, val icon: ImageVector) {
    RECHNER("Rechner", Icons.Filled.Calculate),
    PEPTIDE("Peptide", Icons.Filled.Vaccines),
    TRACKER("Tracker", Icons.Filled.Timeline),
    WIKI("Wiki", Icons.Filled.MenuBook),
    INFO("Info", Icons.Filled.Info),
}

@Composable
private fun AppShell() {
    var tab by rememberSaveable { mutableStateOf(Tab.RECHNER) }
    var selectedName by rememberSaveable { mutableStateOf(PeptideRepository.peptides.first().name) }
    val selected: Peptide = remember(selectedName) {
        PeptideRepository.peptides.firstOrNull { it.name == selectedName }
            ?: PeptideRepository.peptides.first()
    }

    Scaffold(
        containerColor = AppC.bg,
        bottomBar = {
            NavigationBar(containerColor = AppC.cardBg, tonalElevation = 0.dp) {
                Tab.entries.forEach { t ->
                    NavigationBarItem(
                        selected = tab == t,
                        onClick = { tab = t },
                        icon = { Icon(t.icon, contentDescription = t.label) },
                        label = { Text(t.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Brand.Primary,
                            indicatorColor = Brand.Primary,
                            unselectedIconColor = AppC.textMuted,
                            unselectedTextColor = AppC.textMuted,
                        ),
                    )
                }
            }
        },
    ) { padding ->
        val content = Modifier
            .fillMaxSize()
            .padding(bottom = padding.calculateBottomPadding())
        when (tab) {
            Tab.RECHNER -> androidx.compose.foundation.layout.Box(content) {
                CalculatorScreen(peptide = selected, onChangePeptide = { tab = Tab.PEPTIDE })
            }
            Tab.PEPTIDE -> androidx.compose.foundation.layout.Box(content) {
                PeptideListScreen(
                    title = "Peptid-Rechner",
                    subtitle = "Wähle ein Peptid zum Berechnen",
                    onPeptideSelected = { selectedName = it.name; tab = Tab.RECHNER },
                )
            }
            Tab.TRACKER -> androidx.compose.foundation.layout.Box(content) {
                TrackerScreen()
            }
            Tab.WIKI -> androidx.compose.foundation.layout.Box(content) {
                WikiScreen(onCalculate = { selectedName = it.name; tab = Tab.RECHNER })
            }
            Tab.INFO -> androidx.compose.foundation.layout.Box(content) {
                InfoScreen()
            }
        }
    }
}

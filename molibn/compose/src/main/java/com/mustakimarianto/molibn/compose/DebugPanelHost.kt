package com.mustakimarianto.molibn.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mustakimarianto.molibn.Molibn
import com.mustakimarianto.molibn.compose.component.Accordion
import com.mustakimarianto.molibn.compose.component.DebugButton
import com.mustakimarianto.molibn.compose.theme.MolibnTheme
import com.mustakimarianto.molibn.compose.utils.PanelButtonPosition
import com.mustakimarianto.molibn.compose.utils.ext.getAppVersionName

@Composable
fun DebugPanelHost(
    molibn: Molibn,
    buttonPosition: PanelButtonPosition = PanelButtonPosition.BOTTOM_RIGHT,
    defaultPanelContent: Boolean,
    content: @Composable () -> Unit
) {
    MolibnTheme {
        var isPanelVisible by rememberSaveable { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            // Main app content
            content()

            // Floating toggle button (only when panel is closed)
            if (!isPanelVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentAlignment = when (buttonPosition) {
                        PanelButtonPosition.BOTTOM_LEFT -> Alignment.BottomStart
                        PanelButtonPosition.BOTTOM_RIGHT -> Alignment.BottomEnd
                    }
                ) {
                    DebugButton {
                        isPanelVisible = true
                    }
                }
            }

            // Debug panel overlay
            if (isPanelVisible) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f) // take bottom half
                        .align(Alignment.BottomCenter),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Debug Panel",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.titleMedium
                            )
                            TextButton(onClick = { isPanelVisible = false }) {
                                Text("Close", color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant)
                        if (defaultPanelContent) {
                            DefaultPanelContent(molibn)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DefaultPanelContent(molibn: Molibn) {
    val context = LocalContext.current
    val appVersion = remember { context.getAppVersionName() }

    Column(Modifier.verticalScroll(rememberScrollState())) {
        val allFeatures = molibn.getAllFeatures()
        if (allFeatures.isEmpty()) {
            Text("No features available", color = MaterialTheme.colorScheme.outline)
        } else {
            allFeatures.forEach { feature ->
                var isEnabled by rememberSaveable(feature.name) {
                    mutableStateOf(feature.enabled)
                }

                val currentDeviceSupported =
                    molibn.isSupportedApiLevel(feature.name) &&
                            molibn.isSupportedAppVersion(feature.name, appVersion)

                Accordion(feature.name) {
                    Text("Is enabled: ${if (feature.enabled) "Yes" else "No"}")
                    Text("Supported API levels: ${feature.condition.supportedApiLevels}")
                    Text("Supported versions: ${feature.condition.supportedAppVersions}")
                    Text("Supported current device: ${if (currentDeviceSupported) "Yes" else "No"}")

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            isEnabled = !isEnabled
                            if (isEnabled) {
                                molibn.updateFeature(feature.copy(
                                    enabled = true
                                ))
                            } else {
                                molibn.updateFeature(feature.copy(
                                    enabled = false
                                ))
                            }
                        }
                    ) {
                        Text(if (isEnabled) "Disable Feature" else "Enable Feature")
                    }
                }
            }
        }
    }
}

package warlockfe.warlock3.app

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.jewel.window.DecoratedWindowScope
import warlockfe.warlock3.compose.AppContainer
import warlockfe.warlock3.compose.MainScreen
import warlockfe.warlock3.compose.model.GameScreen
import warlockfe.warlock3.compose.model.GameState
import warlockfe.warlock3.core.client.GameCharacter
import warlockfe.warlock3.core.sge.SgeSettings

@Composable
fun DecoratedWindowScope.WarlockApp(
    title: String,
    appContainer: AppContainer,
    backgroundGames: List<GameState>,
    activeGame: GameState,
    onGameSelected: (GameState) -> Unit,
    onGameClosed: (GameState) -> Unit,
    onNewGame: () -> Unit,
    showUpdateDialog: () -> Unit,
    sgeSettings: SgeSettings,
) {
    var showSettings by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    CompositionLocalProvider(
        LocalScrollbarStyle provides LocalScrollbarStyle.current.copy(
            hoverColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            unhoverColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    ) {
        var showAboutDialog by remember { mutableStateOf(false) }
        var sideBarVisible by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxSize()) {
            TitleBarView(
                title = title,
                sideBarVisible = sideBarVisible,
                showSideBar = {
                    sideBarVisible = it
                },
                isConnected = activeGame.screen is GameScreen.ConnectedGameState,
                openNewWindow = onNewGame,
                showSettingsDialog = {
                    showSettings = true
                },
                disconnect = {
                    val screen = activeGame.screen
                    if (screen is GameScreen.ConnectedGameState) {
                        scope.launch {
                            screen.viewModel.close()
                        }
                    }
                },
                scriptDirectory = appContainer.scriptDirRepository.getDefaultDir(),
                runScript = {
                    val screen = activeGame.screen
                    if (screen is GameScreen.ConnectedGameState) {
                        screen.viewModel.runScript(it)
                    }
                },
                showUpdateDialog = showUpdateDialog,
                showAboutDialog = {
                    showAboutDialog = !showAboutDialog
                },
            )

            ScrollableTabRow(
                selectedTabIndex = backgroundGames.indexOf(activeGame),
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                divider = {},
            ) {
                backgroundGames.forEach { game ->
                    val gameTitle by game.getTitle().collectAsState("Loading...")
                    Tab(
                        selected = game == activeGame,
                        onClick = { onGameSelected(game) },
                        text = { Text(gameTitle) },
                        close = { onGameClosed(game) }
                    )
                }
                Tab(
                    selected = false,
                    onClick = onNewGame,
                    text = { Text("+") },
                    close = {} // No close button for "New Tab" button
                )
            }

            if (showAboutDialog) {
                AboutDialog { showAboutDialog = false }
            }

            var currentCharacter: GameCharacter? by remember { mutableStateOf(null) }

            // We need to key the MainScreen to the activeGame so it recomposes entirely when switching text
            // or use key(activeGame) { ... }
            key(activeGame) {
                MainScreen(
                    sgeViewModelFactory = appContainer.sgeViewModelFactory,
                    dashboardViewModelFactory = appContainer.dashboardViewModelFactory,
                    gameState = activeGame,
                    updateCurrentCharacter = { currentCharacter = it },
                    sgeSettings = sgeSettings,
                    sideBarVisible = sideBarVisible,
                )
            }

            if (showSettings) {
                SettingsDialog(
                    currentCharacter = currentCharacter,
                    closeDialog = { showSettings = false },
                    variableRepository = appContainer.variableRepository,
                    macroRepository = appContainer.macroRepository,
                    presetRepository = appContainer.presetRepository,
                    characterRepository = appContainer.characterRepository,
                    highlightRepository = appContainer.highlightRepository,
                    nameRepository = appContainer.nameRepository,
                    characterSettingsRepository = appContainer.characterSettingsRepository,
                    aliasRepository = appContainer.aliasRepository,
                    scriptDirRepository = appContainer.scriptDirRepository,
                    alterationRepository = appContainer.alterationRepository,
                    clientSettingRepository = appContainer.clientSettings,
                    wraythImporter = appContainer.wraythImporter,
                )
            }
        }
    }
}

@Composable
fun Tab(
    selected: Boolean,
    onClick: () -> Unit,
    text: @Composable () -> Unit,
    close: () -> Unit,
) {
    androidx.compose.material3.Tab(
        selected = selected,
        onClick = onClick,
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                text()
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = close, modifier = Modifier.size(16.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        }
    )
}

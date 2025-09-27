package ru.infable.autobattlerprototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.infable.autobattlerprototype.ui.theme.AutoBattlerPrototypeTheme
import ru.infable.autobattlerprototype.game.GameLogic
import kotlinx.coroutines.launch
import ru.infable.autobattlerprototype.models.Character
import ru.infable.autobattlerprototype.models.CharacterClass
import ru.infable.autobattlerprototype.models.Monster
import ru.infable.autobattlerprototype.models.MonsterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf("CharacterCreation") }
            var player by remember { mutableStateOf(Character()) }
            var monster by remember { mutableStateOf(MonsterFactory.getRandomMonster()) }
            var battleLog by remember { mutableStateOf("") }
            val scope = rememberCoroutineScope()

            when (currentScreen) {
                "CharacterCreation" -> CharacterCreationScreen(
                    onClassSelected = { chosenClass ->
                        player.initialize(chosenClass)
                        currentScreen = "Battle"
                    }
                )
                "Battle" -> BattleScreen(
                    player = player,
                    monster = monster,
                    onBattleEnd = { win ->
                        if (win) {
                            player.winsInRow++
                            player.restoreHealth()
                            battleLog += "Победа! Выпало ${monster.reward.name}\n"
                            if (player.winsInRow == 3) currentScreen = "GameWon"
                            else {
                                monster = MonsterFactory.getRandomMonster()
                                currentScreen = "LevelUp"
                            }
                        } else {
                            player.winsInRow = 0
                            currentScreen = "CharacterCreation"
                            battleLog = "Поражение!\n"
                        }
                    },
                    log = battleLog
                )
                "LevelUp" -> LevelUpScreen(
                    player = player,
                    onLevelUp = { newClass ->
                        player.levelUp(newClass)
                        currentScreen = "Battle"
                    }
                )
                "GameWon" -> GameWonScreen(onRestart = { currentScreen = "CharacterCreation" })
            }
        }
    }
}

@Composable
fun CharacterCreationScreen(onClassSelected: (CharacterClass) -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Выберите класс:")
        CharacterClass.values().forEach { classType ->
            Button(onClick = { onClassSelected(classType) }) {
                Text(classType.name)
            }
        }
    }
}

@Composable
fun BattleScreen(player: Character, monster: Monster, onBattleEnd: (Boolean) -> Unit, log: String) {
    var localLog by remember { mutableStateOf(log) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            val win = GameLogic.simulateBattle(player, monster)
            localLog += if (win) "Победа!\n" else "Поражение!\n"
            onBattleEnd(win)
        }
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Бой: ${player.currentHealth}/${player.maxHealth} vs ${monster.currentHealth}/${monster.baseHealth}")
        Text(localLog)
    }
}

@Composable
fun LevelUpScreen(player: Character, onLevelUp: (CharacterClass) -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Повышение уровня! Выберите класс:")
        CharacterClass.values().forEach { classType ->
            Button(onClick = { onLevelUp(classType) }) {
                Text(classType.name)
            }
        }
    }
}

@Composable
fun GameWonScreen(onRestart: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Игра пройдена!")
        Button(onClick = onRestart) {
            Text("Начать заново")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AutoBattlerPrototypeTheme {
        Greeting("Android")
    }
}
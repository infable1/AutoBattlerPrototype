package ru.infable.autobattlerprototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.infable.autobattlerprototype.models.Character
import ru.infable.autobattlerprototype.models.CharacterClass
import ru.infable.autobattlerprototype.models.Monster
import ru.infable.autobattlerprototype.models.MonsterFactory
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.geometry.Offset

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            var currentScreen by remember { mutableStateOf("Welcome") }
            var player by remember { mutableStateOf(Character()) }
            var monster by remember { mutableStateOf(MonsterFactory.getRandomMonster()) }
            var battleLog by remember { mutableStateOf("") }
            val scope = rememberCoroutineScope()

            when (currentScreen) {
                "Welcome" -> WelcomeScreen(
                    onStartGame = { currentScreen = "CharacterCreation" }
                )
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
                        scope.launch {
                            delay(500) // Задержка для отображения результата
                            if (win) {
                                player.winsInRow++
                                player.restoreHealth()
                                battleLog += "Победа! Выпало ${monster.reward.name}\n"
                                if (player.winsInRow == 3) currentScreen = "GameWon"
                                else currentScreen = "LevelUp"
                            } else {
                                player.winsInRow = 0
                                currentScreen = "CharacterCreation"
                                battleLog = "Поражение!\n"
                            }
                        }
                    },
                    log = battleLog
                )
                "LevelUp" -> LevelUpScreen(
                    player = player,
                    onLevelUp = { newClass ->
                        player.levelUp(newClass)
                        currentScreen = "Battle"
                        monster = MonsterFactory.getRandomMonster() // Новый монстр
                    }
                )
                "GameWon" -> GameWonScreen(onRestart = {
                    player = Character() // Сброс персонажа
                    currentScreen = "CharacterCreation"
                    battleLog = ""
                })
            }
        }
    }
}

@Composable
fun WelcomeScreen(onStartGame: () -> Unit) {
    val OiFontFamily = FontFamily(
        Font(R.font.oi_regular, FontWeight.Normal)
    )

    val OnestFontFamily = FontFamily(
        Font(R.font.onest_extrabold, weight = FontWeight.Normal)
    )

    val gradientWelcomeText = Brush.linearGradient(
        colors = listOf(
        Color.hsl(190f, 0.36f, 0.5f, 1f),
        Color.hsl(190f, 1f, 0.94f, 1f)

        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    val gradientWelcomeButton = Brush.linearGradient(colors = listOf(
        Color.hsl(190f, 0.98f, 0.22f, 1f),
        Color.hsl(189f, 0.66f, 0.46f, 1f),
        Color.hsl(190f, 0.84f, 0.29f, 1f)
    ))

    Box(modifier = Modifier.fillMaxSize()) {
        // Фоновое изображение
        Image(
            painter = painterResource(id = R.drawable.welcome_background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Содержимое поверх фона
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier.padding(22.dp)
            ) {

                Text(
                    text = "  АВТО\n            БАТТЛЕР",
                    fontSize = 64.sp,
                    fontFamily = OiFontFamily,
                    style = TextStyle(brush = gradientWelcomeText, lineHeight = 70.sp),
                    fontWeight = FontWeight.Normal
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_shield_wlcm_text_1),
                    contentDescription = "Shield Text",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .rotate((-5.79f))
                        .size(86.dp)
                        .offset(
                            x = ((-3).dp),
                            y = ((-15).dp)
                        )
                )



                Image(
                    painter = painterResource(id = R.drawable.ic_sword_wlcm_text_1),
                    contentDescription = "Sword Text",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .size(64.dp)
                        .offset(
                            x = 18.dp,
                            y = ((-2).dp)
                        )
                        .rotate((-2.08f))
                )

            }

            Box {

                Button(
                    onClick = onStartGame,
                    modifier = Modifier
                        .width(293.dp) // Устанавливаем фиксированную ширину
                        .height(58.dp), // Устанавливаем фиксированную высоту
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.hsl(190f, 0.84f, 0.29f, 1f),
                        contentColor = Color.White // Цвет текста на кнопке
                    )
                ) {
                    Text(
                        text = "НАЧАТЬ ИГРУ",
                        fontSize = 22.sp,
                        fontFamily = OnestFontFamily,
                        fontWeight = FontWeight.Normal
                    )
                }

                Box (
                    modifier = Modifier.offset(
                        x = 245.dp,
                        y = (-2).dp
                    )
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.ic_shield_wlcm_btn),
                        contentDescription = "Shield Button",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .rotate((-15.06f))
                            .size(59.dp)
                    )

                    Image(
                        painter = painterResource(id = R.drawable.ic_sword_wlcm_btn),
                        contentDescription = "Sword Button",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .size(59.dp)
                            .offset(
                                x = 12.dp,
                                y = 8.dp
                            )
                            .rotate((-15.65f))
                    )

                }

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
            delay(1000) // Задержка для чтения лога
            onBattleEnd(player.isAlive()) // Передаём результат боя
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
package ru.infable.autobattlerprototype

import android.media.Image
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter

val OiFontFamily = FontFamily(
    Font(R.font.oi_regular, FontWeight.Normal)
)

val OnestExtraBoldFontFamily = FontFamily(
    Font(R.font.onest_extrabold, weight = FontWeight.ExtraBold)
)

val OnestRegularFontFamily = FontFamily(
    Font(R.font.onest_regular, weight = FontWeight.Normal)
)

val InterRegularFontFamily = FontFamily(
    Font(R.font.inter_regular, weight = FontWeight.Normal)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideSystemUI()

        setContent {
            var currentScreen by remember { mutableStateOf("Welcome") }
            var player by remember { mutableStateOf(Character()) }
            var monster by remember { mutableStateOf(MonsterFactory.getRandomMonster()) }
            var battleLog by remember { mutableStateOf("") }
            val scope = rememberCoroutineScope()

            when (currentScreen) {
                "Welcome" -> WelcomeScreen(
                    onStartGame = { currentScreen = "WarriorCreation" }
                )
                "WarriorCreation" -> CharacterWarriorScreen(
                    onRight = { currentScreen = "RogueCreation"},
                    onLeft = { currentScreen = "BarbarianCreation"},
                    onClassSelected = { chosenClass ->
                        player.initialize(chosenClass)
                        currentScreen = "Battle"
                    }
                )
                "RogueCreation" -> CharacterRogueScreen(
                    onLeft = { currentScreen = "WarriorCreation" },
                    onClassSelected = { chosenClass ->
                        player.initialize(chosenClass)
                        currentScreen = "Battle"
                    }
                )
                "BarbarianCreation" -> CharacterBarbarianScreen(
                    onRight = { currentScreen = "WarriorCreation"},
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
                            delay(500)
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

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

}

@Composable
fun WelcomeScreen(onStartGame: () -> Unit) {

    val gradientWelcomeText = Brush.linearGradient(
        colors = listOf(
        Color.hsl(190f, 0.36f, 0.5f, 1f),
        Color.hsl(190f, 1f, 0.94f, 1f)
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.welcome_background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

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
                        .width(293.dp)
                        .height(58.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.hsl(190f, 0.84f, 0.29f, 1f),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "НАЧАТЬ ИГРУ",
                        fontSize = 22.sp,
                        fontFamily = OnestExtraBoldFontFamily,
                        fontWeight = FontWeight.ExtraBold
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
fun CharacterWarriorScreen(
    onClassSelected: (CharacterClass) -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.choose_background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )


            Row (
                horizontalArrangement = Arrangement.Absolute.Center,
                verticalAlignment = Alignment.Top
            ) {

                Image(
                    painter = painterResource(id = R.drawable.choose_left),
                    contentDescription = "Left",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 180.dp)
                        .clickable(enabled = true, onClick = onLeft)
                )

                Column(
                    modifier = Modifier.padding(start = (4.25).dp, top = 27.dp)
                ) {

                    Text(
                        text = "ВЫБЕРИТЕ КЛАСС:",
                        fontSize = 15.sp,
                        color = Color.White,
                        fontFamily = OnestExtraBoldFontFamily,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text (
                        text = "ВОИН",
                        fontSize = 36.sp,
                        color = Color.White,
                        fontFamily = OnestExtraBoldFontFamily,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "Воин — это основа вашей команды.\nВаша задача — быть на передовой, принимать на\nсебя основной удар и создавать возможности для\nсоюзников. Вы врываетесь в самую гущу схватки,\nиспользуете умения для контроля вражеских героев\nи позволяете своим товарищам безопасно наносить\nурон.",
                        fontSize = 10.sp,
                        color = Color.White,
                        fontFamily = OnestRegularFontFamily,
                        fontWeight = FontWeight.Normal
                    )

                }

                Box(
                    modifier = Modifier.padding(top = 19.dp)
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.choose_warrior),
                        contentDescription = "Warrior",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .size(279.dp)
                            .offset(
                                x = ((-45).dp),
                                y = 0.dp
                            )
                    )

                }

                Column(
                    modifier = Modifier
                        .offset(
                            x = ((-70).dp),
                            y = 61.dp
                        )
                        .size(200.dp)
                ) {

                    Text(
                        text = "МАКСИМАЛЬНЫЕ ПОКАЗАТЕЛИ:",
                        fontSize = 14.sp,
                        color = Color.White,
                        fontFamily = OnestExtraBoldFontFamily,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Box {

                        Text(
                            text = "Урон от атак",
                            fontSize = 14.sp,
                            color = Color.White,
                            fontFamily = OnestRegularFontFamily,
                            fontWeight = FontWeight.Normal
                        )

                        Image(
                            painter = painterResource(id = R.drawable.warrior_damage),
                            contentDescription = "Rectangle",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .offset(
                                    x = (110.dp),
                                    y = ((6.5).dp)
                                )
                        )

                    }

                    Box {

                        Text(
                            text = "Сила в бою",
                            fontSize = 14.sp,
                            color = Color.White,
                            fontFamily = OnestRegularFontFamily,
                            fontWeight = FontWeight.Normal
                        )

                        Image(
                            painter = painterResource(id = R.drawable.warrior_strength),
                            contentDescription = "Rectangle",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .offset(
                                    x = (111.dp),
                                    y = ((6.5).dp)
                                )
                        )

                    }

                    Box {

                        Text(
                            text = "Ловкость",
                            fontSize = 14.sp,
                            color = Color.White,
                            fontFamily = OnestRegularFontFamily,
                            fontWeight = FontWeight.Normal
                        )

                        Image(
                            painter = painterResource(id = R.drawable.warrior_agility),
                            contentDescription = "Rectangle",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .offset(
                                    x = (111.dp),
                                    y = ((6.5).dp)
                                )
                        )

                    }

                    Box {

                        Text(
                            text = "Выносливость",
                            fontSize = 14.sp,
                            color = Color.White,
                            fontFamily = OnestRegularFontFamily,
                            fontWeight = FontWeight.Normal
                        )

                        Image(
                            painter = painterResource(id = R.drawable.warrior_endurance),
                            contentDescription = "Rectangle",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .offset(
                                    x = (111.dp),
                                    y = ((6.5).dp)
                                )
                        )

                    }

                }

                Image(
                    painter = painterResource(id = R.drawable.choose_right),
                    contentDescription = "Right",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .padding(top = 180.dp)
                        .offset(
                            x = ((-20).dp),
                            y = 0.dp
                        )
                        .clickable(enabled = true, onClick = onRight)
                )

            }

        Button(
            onClick = { onClassSelected(CharacterClass.WARRIOR) },
            modifier = Modifier
                .width(293.dp)
                .height(42.dp)
                .offset(
                    x = 260.dp,
                    y = 320.dp
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.hsl(190f, 0.84f, 0.29f, 1f),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "ВЫБРАТЬ",
                fontSize = 20.sp,
                fontFamily = OnestExtraBoldFontFamily,
                fontWeight = FontWeight.ExtraBold
            )
        }

    }
}

@Composable
fun CharacterRogueScreen(
    onClassSelected: (CharacterClass) -> Unit,
    onLeft: () -> Unit
) {

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.choose_background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Row (
            horizontalArrangement = Arrangement.Absolute.Center,
            verticalAlignment = Alignment.Top
        ) {

            Image(
                painter = painterResource(id = R.drawable.choose_left),
                contentDescription = "Left",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 180.dp)
                    .clickable(enabled = true, onClick = onLeft)
            )

            Column(
                modifier = Modifier.padding(start = (4.25).dp, top = 27.dp)
            ) {

                Text(
                    text = "ВЫБЕРИТЕ КЛАСС:",
                    fontSize = 15.sp,
                    color = Color.White,
                    fontFamily = OnestExtraBoldFontFamily,
                    fontWeight = FontWeight.ExtraBold
                )

                Text (
                    text = "РАЗБОЙНИК",
                    fontSize = 36.sp,
                    color = Color.White,
                    fontFamily = OnestExtraBoldFontFamily,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Разбойник — хищник, который выбирает свою\nжертву. Ваша задача — не вступать в честный бой,\nа терпеливо ждать момента для идеальной атаки.\nВы должны выслеживать одиноких врагов,\nослабленных после боя, или быстро уничтожать\nключевые цели (магов, лекарей) в групповом\nсражении.",
                    fontSize = 10.sp,
                    color = Color.White,
                    fontFamily = OnestRegularFontFamily,
                    fontWeight = FontWeight.Normal
                )

            }

            Box(
                modifier = Modifier.padding(top = 19.dp)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.choose_robber),
                    contentDescription = "Rogue",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .size(279.dp)
                        .offset(
                            x = ((-15).dp),
                            y = 0.dp
                        )
                )

            }

            Column(
                modifier = Modifier
                    .offset(
                        x = ((-70).dp),
                        y = 61.dp
                    )
                    .size(200.dp)
            ) {

                Text(
                    text = "МАКСИМАЛЬНЫЕ ПОКАЗАТЕЛИ:",
                    fontSize = 14.sp,
                    color = Color.White,
                    fontFamily = OnestExtraBoldFontFamily,
                    fontWeight = FontWeight.ExtraBold
                )

                Box {

                    Text(
                        text = "Урон от атак",
                        fontSize = 14.sp,
                        color = Color.White,
                        fontFamily = OnestRegularFontFamily,
                        fontWeight = FontWeight.Normal
                    )

                    Image(
                        painter = painterResource(id = R.drawable.warrior_damage),
                        contentDescription = "Rectangle",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .offset(
                                x = (110.dp),
                                y = ((6.5).dp)
                            )
                    )

                }

                Box {

                    Text(
                        text = "Сила в бою",
                        fontSize = 14.sp,
                        color = Color.White,
                        fontFamily = OnestRegularFontFamily,
                        fontWeight = FontWeight.Normal
                    )

                    Image(
                        painter = painterResource(id = R.drawable.warrior_strength),
                        contentDescription = "Rectangle",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .offset(
                                x = (111.dp),
                                y = ((6.5).dp)
                            )
                    )

                }

                Box {

                    Text(
                        text = "Ловкость",
                        fontSize = 14.sp,
                        color = Color.White,
                        fontFamily = OnestRegularFontFamily,
                        fontWeight = FontWeight.Normal
                    )

                    Image(
                        painter = painterResource(id = R.drawable.warrior_agility),
                        contentDescription = "Rectangle",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .offset(
                                x = (111.dp),
                                y = ((6.5).dp)
                            )
                    )

                }

                Box {

                    Text(
                        text = "Выносливость",
                        fontSize = 14.sp,
                        color = Color.White,
                        fontFamily = OnestRegularFontFamily,
                        fontWeight = FontWeight.Normal
                    )

                    Image(
                        painter = painterResource(id = R.drawable.warrior_endurance),
                        contentDescription = "Rectangle",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .offset(
                                x = (111.dp),
                                y = ((6.5).dp)
                            )
                    )

                }

            }

        }

        Button(
            onClick = { onClassSelected(CharacterClass.WARRIOR) },
            modifier = Modifier
                .width(293.dp)
                .height(42.dp)
                .offset(
                    x = 260.dp,
                    y = 320.dp
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.hsl(190f, 0.84f, 0.29f, 1f),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "ВЫБРАТЬ",
                fontSize = 20.sp,
                fontFamily = OnestExtraBoldFontFamily,
                fontWeight = FontWeight.ExtraBold
            )
        }

    }

}

@Composable
fun CharacterBarbarianScreen(
    onRight: () -> Unit,
    onClassSelected: (CharacterClass) -> Unit
) {

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
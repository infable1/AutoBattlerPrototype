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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
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
import ru.infable.autobattlerprototype.game.GameLogic
import kotlin.random.Random

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
                        currentScreen = "BattleFirst"
                    }
                )
                "RogueCreation" -> CharacterRogueScreen(
                    onLeft = { currentScreen = "WarriorCreation" },
                    onClassSelected = { chosenClass ->
                        player.initialize(chosenClass)
                        currentScreen = "BattleFirst"
                    }
                )
                "BarbarianCreation" -> CharacterBarbarianScreen(
                    onRight = { currentScreen = "WarriorCreation"},
                    onClassSelected = { chosenClass ->
                        player.initialize(chosenClass)
                        currentScreen = "BattleFirst"
                    }
                )
                "BattleFirst" -> BattleScreenFirst(
                    player = player,
                    monster = MonsterFactory.getRandomMonster(),
                    onBattle = {
                        if (GameLogic.simulateBattle(player, monster)) {
                            currentScreen = "WinFirst"
                        }
                    }
                )
                "WinFirst" -> WinScreenFirst(
                    player = player
                )
                "BattleSecond" -> BattleScreenSecond(

                )
                "LevelUp" -> LevelUpScreen(
                    player = player,
                    onLevelUp = { newClass ->
                        player.levelUp(newClass)
                        currentScreen = "Battle"
                        monster = MonsterFactory.getRandomMonster()
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

        val openDialog = remember { mutableStateOf(false) }
        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = { openDialog.value = false},
                text = {
                    Text(
                        text = "ВЫ ВЫБРАЛИ КЛАСС: ВОИН",
                        fontSize = 15.sp,
                        color = Color.White,
                        fontFamily = OnestExtraBoldFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.offset(
                            x = (24.dp),
                            y = 0.dp
                        )
                    )
                },
                containerColor = Color.hsl(190f, 0.84f, 0.29f, 0.8f),
                modifier = Modifier.size(
                    width = 431.dp,
                    height = 131.dp
                ),
                confirmButton = {
                    Button(
                        onClick = {
                            onClassSelected(CharacterClass.WARRIOR)
                            openDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.hsl(190f, 0.84f, 0.29f, 1f),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(
                                width = 293.dp,
                                height = 42.dp
                            )
                    ) {
                        Text(
                            text= "В БОЙ",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = OnestExtraBoldFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.offset(
                                x = 10.dp,
                                y = 0.dp
                            )
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_alert_battle),
                            contentDescription = "Battle",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(29.dp)
                                .offset(
                                    x = 13.dp,
                                    y = 0.dp
                                )
                        )
                    }
                }
            )
        }

        Button(
            onClick = { openDialog.value = true },
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
                        x = ((-59.6).dp),
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
                        painter = painterResource(id = R.drawable.rogue_damage),
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
                        painter = painterResource(id = R.drawable.rogue_strength),
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
                        painter = painterResource(id = R.drawable.rogue_agility),
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
                        painter = painterResource(id = R.drawable.rogue_endurance),
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

        val openDialog = remember { mutableStateOf(false) }
        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                text = {
                    Text(
                        text = "ВЫ ВЫБРАЛИ КЛАСС: РАЗБОЙНИК",
                        fontSize = 15.sp,
                        color = Color.White,
                        fontFamily = OnestExtraBoldFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.offset(
                            x = (4.dp),
                            y = 0.dp
                        )
                    )
                },
                containerColor = Color.hsl(190f, 0.84f, 0.29f, 0.8f),
                modifier = Modifier.size(
                    width = 431.dp,
                    height = 131.dp
                ),
                confirmButton = {
                    Button(
                        onClick = {
                            onClassSelected(CharacterClass.ROGUE)
                            openDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.hsl(190f, 0.84f, 0.29f, 1f),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(
                            width = 293.dp,
                            height = 42.dp
                        )
                    ) {
                        Text(
                            text= "В БОЙ",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = OnestExtraBoldFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.offset(
                                x = 10.dp,
                                y = 0.dp
                            )
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_alert_battle),
                            contentDescription = "Battle",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(29.dp)
                                .offset(
                                    x = 13.dp,
                                    y = 0.dp
                                )
                        )
                    }
                }
            )
        }

        Button(
            onClick = { openDialog.value = true },
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

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.choose_background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )


        Row(
            horizontalArrangement = Arrangement.Absolute.Center,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(start = (57.8).dp)
        ) {

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

                Text(
                    text = "ВАРВАР",
                    fontSize = 36.sp,
                    color = Color.White,
                    fontFamily = OnestExtraBoldFontFamily,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Варвар — это ураган из стали и гнева.\nОн не знает пощады и не ведает страха.\nВойдите в состояние берсерка, чтобы ваша атака\nи скорость достигали невероятных значений,\nа здоровье возвращалось с каждым нанесённым\nударом!",
                    fontSize = 10.sp,
                    color = Color.White,
                    fontFamily = OnestRegularFontFamily,
                    fontWeight = FontWeight.Normal
                )

            }

            Box(
                modifier = Modifier.padding(top = 10.dp)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.choose_barbarian),
                    contentDescription = "Warrior",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .size(
                            width = 232.dp,
                            height = 310.dp
                        )
                        .offset(
                            x = ((-25).dp),
                            y = ((-5).dp)
                        )
                )

            }

            Column(
                modifier = Modifier
                    .offset(
                        x = ((-8.4).dp),
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
                        painter = painterResource(id = R.drawable.barbarian_damage),
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
                        painter = painterResource(id = R.drawable.barbarian_strength),
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
                        painter = painterResource(id = R.drawable.barbarian_agility),
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
                        painter = painterResource(id = R.drawable.barbarian_endurance),
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
                        x = ((41.6).dp),
                        y = 0.dp
                    )
                    .clickable(enabled = true, onClick = onRight)
            )

        }

        val openDialog = remember { mutableStateOf(false) }
        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = { openDialog.value = false },
                text = {
                    Text(
                        text = "ВЫ ВЫБРАЛИ КЛАСС: ВАРВАР",
                        fontSize = 15.sp,
                        color = Color.White,
                        fontFamily = OnestExtraBoldFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.offset(
                            x = (24.dp),
                            y = 0.dp
                        )
                    )
                },
                containerColor = Color.hsl(190f, 0.84f, 0.29f, 0.8f),
                modifier = Modifier.size(
                    width = 431.dp,
                    height = 131.dp
                ),
                confirmButton = {
                    Button(
                        onClick = {
                            onClassSelected(CharacterClass.BARBARIAN)
                            openDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.hsl(190f, 0.84f, 0.29f, 1f),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(
                            width = 293.dp,
                            height = 42.dp
                        )
                    ) {
                        Text(
                            text = "В БОЙ",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = OnestExtraBoldFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.offset(
                                x = 10.dp,
                                y = 0.dp
                            )
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_alert_battle),
                            contentDescription = "Battle",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(29.dp)
                                .offset(
                                    x = 13.dp,
                                    y = 0.dp
                                )
                        )
                    }
                }
            )

        }

        Button(
            onClick = { openDialog.value = true },
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
fun BattleScreenFirst(
    player: Character,
    monster: Monster,
    onBattle: () -> Unit
) {

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.battle_bg),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(top = 33.dp, start = 15.dp)
            ) {

                Text(
                    text = "Уровень 1",
                    fontSize = 15.sp,
                    color = Color.White,
                    fontFamily = OnestExtraBoldFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Text(
                    text = "Урон от атак",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontFamily = OnestExtraBoldFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 5.dp)
                )

                when(player.chosen) {
                    CharacterClass.WARRIOR -> {
                        when(player.strength) {
                            1 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_damage_4_9),
                                    contentDescription = "Damage",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            2 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_damage_5_9),
                                    contentDescription = "Damage",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            3 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_damage_6_9),
                                    contentDescription = "Damage",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        }
                    }
                    CharacterClass.ROGUE -> {
                        when(player.strength) {
                            1 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_damage_3_9),
                                    contentDescription = "Damage",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            2 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_damage_4_9),
                                    contentDescription = "Damage",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            3 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_damage_5_9),
                                    contentDescription = "Damage",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        }
                    }
                    CharacterClass.BARBARIAN -> {
                        when(player.strength) {
                            1 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_damage_4_9),
                                    contentDescription = "Damage",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            2 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_damage_5_9),
                                    contentDescription = "Damage",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            3 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_damage_6_9),
                                    contentDescription = "Damage",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Сила в бою",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontFamily = OnestExtraBoldFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 5.dp)
                )

                when(player.chosen) {
                    CharacterClass.WARRIOR -> {
                        when(player.strength) {
                            1 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_strength_1_4),
                                    contentDescription = "Strength",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            2 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_strength_2_4),
                                    contentDescription = "Strength",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            3 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_strength_3_4),
                                    contentDescription = "Strength",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        }
                    }
                    CharacterClass.ROGUE -> {
                        when(player.strength) {
                            1 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_strength_1_4),
                                    contentDescription = "Strength",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            2 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_strength_2_4),
                                    contentDescription = "Strength",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            3 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_strength_3_4),
                                    contentDescription = "Strength",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        }
                    }
                    CharacterClass.BARBARIAN -> {
                        when(player.strength) {
                            1 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_strength_1_4),
                                    contentDescription = "Strength",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            2 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_strength_2_4),
                                    contentDescription = "Strength",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            3 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_strength_3_4),
                                    contentDescription = "Strength",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Ловкость",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontFamily = OnestExtraBoldFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 5.dp)
                )

                when(player.chosen) {
                    CharacterClass.WARRIOR -> {
                        when(player.dexterity) {
                            1 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_agility_1_4),
                                    contentDescription = "Agility",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            2 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_agility_2_4),
                                    contentDescription = "Agility",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            3 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_agility_3_4),
                                    contentDescription = "Agility",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        }
                    }
                    CharacterClass.ROGUE -> {
                        when(player.dexterity) {
                            1 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_agility_1_4),
                                    contentDescription = "Agility",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            2 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_agility_2_4),
                                    contentDescription = "Agility",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            3 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_agility_3_4),
                                    contentDescription = "Agility",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        }
                    }
                    CharacterClass.BARBARIAN -> {
                        when(player.dexterity) {
                            1 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_agility_1_4),
                                    contentDescription = "Agility",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            2 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_agility_2_4),
                                    contentDescription = "Agility",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            3 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_agility_3_4),
                                    contentDescription = "Agility",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Выносливость",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontFamily = OnestExtraBoldFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 5.dp)
                )

                when(player.chosen) {
                    CharacterClass.WARRIOR -> {
                        when(player.constitution) {
                            1 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_endurance_1_4),
                                    contentDescription = "Endurance",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            2 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_endurance_2_4),
                                    contentDescription = "Endurance",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            3 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_endurance_3_4),
                                    contentDescription = "Endurance",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        }
                    }
                    CharacterClass.ROGUE -> {
                        when(player.constitution) {
                            1 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_endurance_1_4),
                                    contentDescription = "Endurance",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            2 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_endurance_2_4),
                                    contentDescription = "Endurance",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            3 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_endurance_3_4),
                                    contentDescription = "Endurance",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        }
                    }
                    CharacterClass.BARBARIAN -> {
                        when(player.constitution) {
                            1 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_endurance_1_4),
                                    contentDescription = "Endurance",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            2 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_endurance_2_4),
                                    contentDescription = "Endurance",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                            3 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_endurance_3_4),
                                    contentDescription = "Endurance",
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 10.dp)
                ) {

                    Text(
                        text = "Оружие",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontFamily = OnestExtraBoldFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(end = 5.dp)
                    )

                    when(player.chosen) {
                        CharacterClass.WARRIOR -> {
                            Image(
                                painter = painterResource(id = R.drawable.battle_sword),
                                contentDescription = "Sword",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.padding(
                                    end = 5.dp
                                )
                            )
                        }
                        CharacterClass.ROGUE -> {
                            Image(
                                painter = painterResource(id = R.drawable.battle_dagger),
                                contentDescription = "Dagger",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.padding(
                                    end = 5.dp
                                )
                            )
                        }
                        CharacterClass.BARBARIAN -> {
                            Image(
                                painter = painterResource(id = R.drawable.battle_club),
                                contentDescription = "Club",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.padding(
                                    end = 5.dp
                                )
                            )
                        }
                    }

                    when(player.chosen) {
                        CharacterClass.WARRIOR -> {
                            Image(
                                painter = painterResource(id = R.drawable.battle_chopping),
                                contentDescription = "Chopping",
                                contentScale = ContentScale.FillBounds
                            )
                        }
                        CharacterClass.ROGUE -> {
                            Image(
                                painter = painterResource(id = R.drawable.battle_pricking),
                                contentDescription = "Pricking",
                                contentScale = ContentScale.FillBounds
                            )
                        }
                        CharacterClass.BARBARIAN -> {
                            Image(
                                painter = painterResource(id = R.drawable.battle_crushing),
                                contentDescription = "Cruching",
                                contentScale = ContentScale.FillBounds
                            )
                        }
                    }

                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 5.dp)
                ) {

                    Text(
                        text = "Бонусы",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontFamily = OnestExtraBoldFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(
                            end = 5.dp
                        )
                    )

                    when(player.chosen) {
                        CharacterClass.WARRIOR -> {
                            Image(
                                painter = painterResource(id = R.drawable.battle_bonus_gust),
                                contentDescription = "Gust",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.padding(
                                    end = 5.dp
                                )
                            )
                        }
                        CharacterClass.ROGUE -> {
                            Image(
                                painter = painterResource(id = R.drawable.battle_bonus_hidden),
                                contentDescription = "Hidden",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.padding(
                                    end = 5.dp
                                )
                            )
                        }
                        CharacterClass.BARBARIAN -> {
                            Image(
                                painter = painterResource(id = R.drawable.battle_bonus_rage),
                                contentDescription = "Rage",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.padding(
                                    end = 5.dp
                                )
                            )
                        }
                    }

                    when(player.chosen) {
                        CharacterClass.WARRIOR -> {
                            Image(
                                painter = painterResource(id = R.drawable.battle_bonus_question),
                                contentDescription = "Question",
                                contentScale = ContentScale.FillBounds
                            )
                        }
                        CharacterClass.ROGUE -> {
                            Image(
                                painter = painterResource(id = R.drawable.battle_bonus_question),
                                contentDescription = "Question",
                                contentScale = ContentScale.FillBounds
                            )
                        }
                        CharacterClass.BARBARIAN -> {
                            Image(
                                painter = painterResource(id = R.drawable.battle_bonus_question),
                                contentDescription = "Question",
                                contentScale = ContentScale.FillBounds
                            )
                        }
                    }
                }

            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(
                    x = ((-20).dp)
                )
            ) {

                when(player.chosen) {
                    CharacterClass.WARRIOR -> {
                        when(player.constitution) {
                            1 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_health_6_6),
                                    contentDescription = "Health",
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier.padding(
                                        top = 35.dp
                                    )
                                )
                            }
                            2 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_health_7_7),
                                    contentDescription = "Health",
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier.padding(
                                        top = 35.dp
                                    )
                                )
                            }
                            3 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_health_8_8),
                                    contentDescription = "Health",
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier.padding(
                                        top = 35.dp
                                    )
                                )
                            }
                        }
                    }
                    CharacterClass.ROGUE -> {
                        when(player.constitution) {
                            1 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_health_5_5),
                                    contentDescription = "Health",
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier.padding(
                                        top = 35.dp
                                    )
                                )
                            }
                            2 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_health_6_6),
                                    contentDescription = "Health",
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier.padding(
                                        top = 35.dp
                                    )
                                )
                            }
                            3 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_health_7_7),
                                    contentDescription = "Health",
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier.padding(
                                        top = 35.dp
                                    )
                                )
                            }
                        }
                    }
                    CharacterClass.BARBARIAN -> {
                        when(player.constitution) {
                            1 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_health_7_7),
                                    contentDescription = "Health",
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier.padding(
                                        top = 35.dp
                                    )
                                )
                            }
                            2 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_health_8_8),
                                    contentDescription = "Health",
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier.padding(
                                        top = 35.dp
                                    )
                                )
                            }
                            3 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.battle_health_9_9),
                                    contentDescription = "Health",
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier.padding(
                                        top = 35.dp
                                    )
                                )
                            }
                        }
                    }
                }

                when(player.chosen) {
                    CharacterClass.WARRIOR -> {
                        Image(
                            painter = painterResource(id = R.drawable.choose_warrior),
                            contentDescription = "Warrior",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(258.dp)
                                .padding(top = 15.dp, bottom = 15.dp)
                        )
                    }
                    CharacterClass.ROGUE -> {
                        Image(
                            painter = painterResource(id = R.drawable.choose_robber),
                            contentDescription = "Rogue",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(258.dp)
                                .padding(top = 15.dp, bottom = 15.dp)
                                .offset(
                                    x = 10.dp,
                                    y = 0.dp
                                )
                        )
                    }
                    CharacterClass.BARBARIAN -> {
                        Image(
                            painter = painterResource(id = R.drawable.choose_barbarian),
                            contentDescription = "Barbarian",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(
                                    width = 231.dp,
                                    height = 265.dp
                                )
                        )
                    }
                }

                when(player.chosen) {
                    CharacterClass.WARRIOR -> {
                        Text(
                            text = "ВОИН",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = OnestExtraBoldFontFamily,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    CharacterClass.ROGUE -> {
                        Text(
                            text = "РАЗБОЙНИК",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = OnestExtraBoldFontFamily,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    CharacterClass.BARBARIAN -> {
                        Text(
                            text = "ВАРВАР",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = OnestExtraBoldFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }

            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.offset(
                    x = ((-30).dp)
                )
            ) {

                Text(
                    text = "РАУНД 1",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontFamily = OnestExtraBoldFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(
                        top = 28.dp,
                        bottom = 90.dp
                    )
                )

                Button(
                    onClick = {  },
                    modifier = Modifier
                        .width(100.dp)
                        .height(42.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.hsl(190f, 0.84f, 0.29f, 1f),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "БОЙ",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontFamily = OnestExtraBoldFontFamily,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(
                    x = ((-30).dp)
                )
            ) {

                when(monster.name) {
                    "Гоблин" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_monster_health_6_6),
                            contentDescription = "Health",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.padding(
                                top = 35.dp
                            )
                        )
                    }
                    "Скелет" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_monster_health_11_11),
                            contentDescription = "Health",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.padding(
                                top = 35.dp
                            )
                        )
                    }
                    "Слайм" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_monster_health_10_10),
                            contentDescription = "Health",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.padding(
                                top = 35.dp
                            )
                        )
                    }
                    "Призрак" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_monster_health_7_7),
                            contentDescription = "Health",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.padding(
                                top = 35.dp
                            )
                        )
                    }
                    "Голем" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_monster_health_13_13),
                            contentDescription = "Health",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.padding(
                                top = 35.dp
                            )
                        )
                    }
                    "Дракон" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_monster_health_23_23),
                            contentDescription = "Health",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.padding(
                                top = 35.dp
                            )
                        )
                    }
                }

                when(monster.name) {
                    "Гоблин" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_goblin),
                            contentDescription = "Goblin",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(
                                    width = 188.dp,
                                    height = 251.dp
                                )
                                .padding(
                                    top = 23.dp
                                )
                        )
                    }
                    "Скелет" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_skeleton),
                            contentDescription = "Skeleton",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(
                                    width = 230.dp,
                                    height = 251.dp
                                )
                                .padding(
                                    top = 23.dp,
                                )
                        )
                    }
                    "Слайм" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_slime),
                            contentDescription = "Slime",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(
                                    width = 183.dp,
                                    height = 245.dp
                                )
                                .padding(
                                    top = 23.dp
                                )
                        )
                    }
                    "Призрак" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_ghost),
                            contentDescription = "Ghost",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(
                                    width = 177.dp,
                                    height = 237.dp
                                )
                                .padding(
                                    top = 23.dp
                                )
                        )
                    }
                    "Голем" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_golem),
                            contentDescription = "Golem",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(
                                    width = 194.dp,
                                    height = 258.dp
                                )
                                .padding(
                                    top = 13.dp
                                )
                        )
                    }
                    "Дракон" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_dragon),
                            contentDescription = "Dragon",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(
                                    width = 199.dp,
                                    height = 260.dp
                                )
                        )
                    }
                }

                when(monster.name) {
                    "Гоблин" -> {
                        Text(
                            text = "ГОБЛИН",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = OnestExtraBoldFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(
                                top = 8.dp
                            )
                        )
                    }
                    "Скелет" -> {
                        Text(
                            text = "СКЕЛЕТ",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = OnestExtraBoldFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(
                                top = 7.dp
                            )
                        )
                    }
                    "Слайм" -> {
                        Text(
                            text = "СЛАЙМ",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = OnestExtraBoldFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(
                                top = 13.dp
                            )
                        )
                    }
                    "Призрак" -> {
                        Text(
                            text = "ПРИЗРАК",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = OnestExtraBoldFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(
                                top = 23.dp
                            )
                        )
                    }
                    "Голем" -> {
                        Text(
                            text = "ГОЛЕМ",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = OnestExtraBoldFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(
                                top = 0.dp
                            )
                        )
                    }
                    "Дракон" -> {
                        Text(
                            text = "ДРАКОН",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = OnestExtraBoldFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(
                                top = 0.dp
                            )
                        )
                    }
                }

            }

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(top = 58.dp)
                    .offset(
                        x = ((-15).dp)
                    )
            ) {

                Text(
                    text = "Урон от атак",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontFamily = OnestExtraBoldFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 5.dp)
                )

                when(monster.name) {
                    "Гоблин" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_damage_3_9),
                            contentDescription = "Damage",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Скелет" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_damage_4_9),
                            contentDescription = "Damage",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Слайм" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_damage_4_9),
                            contentDescription = "Damage",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Призрак" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_damage_4_9),
                            contentDescription = "Damage",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Голем" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_damage_4_9),
                            contentDescription = "Damage",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Дракон" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_damage_7_9),
                            contentDescription = "Damage",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }

                Text(
                    text = "Сила в бою",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontFamily = OnestExtraBoldFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 5.dp)
                )

                when(monster.name) {
                    "Гоблин" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_strength_1_4),
                            contentDescription = "Strength",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Скелет" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_strength_2_4),
                            contentDescription = "Strength",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Слайм" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_strength_3_4),
                            contentDescription = "Strength",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Призрак" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_strength_1_4),
                            contentDescription = "Strength",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Голем" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_strength_3_4),
                            contentDescription = "Strength",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Дракон" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_strength_3_4),
                            contentDescription = "Strength",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }

                Text(
                    text = "Ловкость",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontFamily = OnestExtraBoldFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 5.dp)
                )

                when(monster.name) {
                    "Гоблин" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_agility_1_4),
                            contentDescription = "Agility",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Скелет" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_agility_2_4),
                            contentDescription = "Agility",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Слайм" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_agility_1_4),
                            contentDescription = "Agility",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Призрак" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_agility_3_4),
                            contentDescription = "Agility",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Голем" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_agility_1_4),
                            contentDescription = "Agility",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Дракон" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_agility_3_4),
                            contentDescription = "Agility",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }

                Text(
                    text = "Выносливость",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontFamily = OnestExtraBoldFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 5.dp)
                )

                when(monster.name) {
                    "Гоблин" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_endurance_1_4),
                            contentDescription = "Endurance",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Скелет" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_endurance_1_4),
                            contentDescription = "Endurance",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Слайм" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_endurance_2_4),
                            contentDescription = "Endurance",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Призрак" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_endurance_1_4),
                            contentDescription = "Endurance",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Голем" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_endurance_3_4),
                            contentDescription = "Endurance",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    "Дракон" -> {
                        Image(
                            painter = painterResource(id = R.drawable.battle_endurance_3_4),
                            contentDescription = "Endurance",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 10.dp)
                ) {

                    Text(
                        text = "Особенность",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontFamily = OnestExtraBoldFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(end = 5.dp)
                    )

                    when(monster.name) {
                        "Гоблин" -> {
                            Image(
                                painter = painterResource(id = R.drawable.battle_peculiarity_minus),
                                contentDescription = "Peculiarity",
                                contentScale = ContentScale.FillBounds
                            )
                        }
                        "Скелет" -> {
                            Image(
                                painter = painterResource(id = R.drawable.battle_peculiarity_skeleton),
                                contentDescription = "Peculiarity",
                                contentScale = ContentScale.FillBounds
                            )
                        }
                        "Слайм" -> {
                            Image(
                                painter = painterResource(id = R.drawable.battle_peculiarity_slime),
                                contentDescription = "Peculiarity",
                                contentScale = ContentScale.FillBounds
                            )
                        }
                        "Призрак" -> {
                            Image(
                                painter = painterResource(id = R.drawable.battle_bonus_hidden),
                                contentDescription = "Hidden",
                                contentScale = ContentScale.FillBounds
                            )
                        }
                        "Голем" -> {
                            Image(
                                painter = painterResource(id = R.drawable.battle_bonus_stone),
                                contentDescription = "Stone",
                                contentScale = ContentScale.FillBounds
                            )
                        }
                        "Дракон" -> {
                            Image(
                                painter = painterResource(id = R.drawable.battle_peculiarity_dragon),
                                contentDescription = "Fire",
                                contentScale = ContentScale.FillBounds
                            )
                        }
                    }

                }

            }

        }

    }

}

@Composable
fun WinScreenFirst(
    player: Character
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.choose_background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        val openDialog = remember { mutableStateOf(false) }
        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {  },
                title = {
                    Text(
                        text = "ПОБЕДА!",
                        fontSize = 22.sp,
                        color = Color.White,
                        fontFamily = OnestExtraBoldFontFamily,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                text = {
                    Text(
                        text = "Получена",
                        fontSize = 15.sp,
                        color = Color.White,
                        fontFamily = OnestExtraBoldFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.offset(
                            x = (24.dp),
                            y = 0.dp
                        )
                    )
                },
                containerColor = Color.hsl(190f, 0.84f, 0.29f, 0.8f),
                modifier = Modifier.size(
                    width = 431.dp,
                    height = 131.dp
                ),
                confirmButton = {
                    Button(
                        onClick = {
                            onClassSelected(CharacterClass.BARBARIAN)
                            openDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.hsl(190f, 0.84f, 0.29f, 1f),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(
                            width = 293.dp,
                            height = 42.dp
                        )
                    ) {
                        Text(
                            text = "В БОЙ",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = OnestExtraBoldFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.offset(
                                x = 10.dp,
                                y = 0.dp
                            )
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_alert_battle),
                            contentDescription = "Battle",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(29.dp)
                                .offset(
                                    x = 13.dp,
                                    y = 0.dp
                                )
                        )
                    }
                }
            )

        }

    }
}

@Composable
fun BattleScreenSecond() {

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
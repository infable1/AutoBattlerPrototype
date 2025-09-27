package ru.infable.autobattlerprototype.game

import ru.infable.autobattlerprototype.models.Character
import ru.infable.autobattlerprototype.models.CharacterClass
import ru.infable.autobattlerprototype.models.Monster
import ru.infable.autobattlerprototype.models.MonsterFactory
import kotlin.random.Random

object GameLogic {
    // Основной цикл игры (упрощенный, для консоли или UI интеграции)
    fun startGame() {
        var player = createCharacter()
        var winsInRow = 0

        while (winsInRow < 3) {
            val monster = MonsterFactory.getRandomMonster()
            println("Сражаетесь с ${monster.name}!")

            val win = simulateBattle(player, monster)
            if (win) {
                winsInRow++
                player.restoreHealth()
                println("Победа! Здоровье восстановлено. Выпало оружие: ${monster.reward}")
                if (Random.nextBoolean()) {
                    player.currentWeapon = monster.reward
                    println("Оружие заменено на ${monster.reward}")
                }
                val newClass = CharacterClass.values().random()
                player.levelUp(newClass)
                println("Уровень повышен в $newClass")
                if (winsInRow == 3) {
                    println("Игра пройдена!")
                }
            } else {
                winsInRow = 0
                println("Поражение! Создайте нового персонажа.")
                player = createCharacter()
            }
        }
    }

    // Создание персонажа
    private fun createCharacter(): Character {
        val chosenClass = CharacterClass.values().random()
        val player = Character()
        player.initialize(chosenClass)
        println("Создан персонаж: $chosenClass, S:${player.strength}, D:${player.dexterity}, C:${player.constitution}, HP:${player.maxHealth}")
        return player
    }

    // Симуляция боя
    fun simulateBattle(player: Character, monster: Monster): Boolean {
        player.restoreHealth()
        monster.restoreHealth()
        var turn = 1
        var currentAttacker: Any = if (player.dexterity > monster.dexterity || player.dexterity == monster.dexterity) player else monster
        var currentDefender: Any = if (currentAttacker == player) monster else player

        while (player.isAlive() && monster.isAlive()) {
            val hitChance = Random.nextInt(1, (if (currentAttacker is Character) currentAttacker.dexterity else (currentAttacker as Monster).dexterity) +
                    (if (currentDefender is Character) currentDefender.dexterity else (currentDefender as Monster).dexterity) + 1)
            val targetDex = if (currentDefender is Character) currentDefender.dexterity else (currentDefender as Monster).dexterity

            if (hitChance > targetDex) { // Попадание (по ТЗ: если число <= ловкости цели - промах)
                val damage = when (currentAttacker) {
                    is Character -> {
                        // Если атакующий - Character, цель должна быть Monster (player атакует monster)
                        if (currentDefender !is Monster) throw IllegalStateException("Ожидался Monster как цель для Character")
                        currentAttacker.calculateDamage(currentDefender as Monster, turn)
                    }
                    is Monster -> {
                        // Если атакующий - Monster, цель всегда player (Character)
                        if (currentDefender !is Character) throw IllegalStateException("Ожидался Character как цель для Monster")
                        currentAttacker.calculateDamage(currentDefender as Character, turn)
                    }
                    else -> 0
                }
                if (damage > 0) {
                    when (currentDefender) {
                        is Character -> currentDefender.takeDamage(damage)
                        is Monster -> currentDefender.takeDamage(damage)
                    }
                    println("${if (currentAttacker is Character) "Игрок" else (currentAttacker as Monster).name} наносит $damage урона. HP цели: ${if (currentDefender is Character) currentDefender.currentHealth else (currentDefender as Monster).currentHealth}")
                }
            } else {
                println("Промах!")
            }

            if (!player.isAlive()) return false
            if (!monster.isAlive()) return true

            // Смена хода
            currentAttacker = currentDefender.also { currentDefender = currentAttacker }
            turn++
        }
        return false // Не должно дойти сюда
    }
}

fun main() { GameLogic.startGame() }
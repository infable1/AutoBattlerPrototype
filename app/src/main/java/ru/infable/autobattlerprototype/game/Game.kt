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
        var currentAttacker = if (player.dexterity > monster.dexterity || player.dexterity == monster.dexterity) player else monster
        var currentDefender = if (currentAttacker == player) monster else player
        val log = StringBuilder()

        while (player.isAlive() && monster.isAlive()) {
            val attackerDex = if (currentAttacker is Character) currentAttacker.dexterity else (currentAttacker as Monster).dexterity
            val defenderDex = if (currentDefender is Character) currentDefender.dexterity else (currentDefender as Monster).dexterity
            val hitChance = Random.nextInt(1, attackerDex + defenderDex + 1)
            val targetDex = if (currentDefender is Character) currentDefender.dexterity else (currentDefender as Monster).dexterity

            if (hitChance > targetDex) { // По ТЗ: промах, если <= ловкости цели
                val damage = when (currentAttacker) {
                    is Character -> currentAttacker.calculateDamage(currentDefender as Monster, turn)
                    is Monster -> (currentAttacker as Monster).calculateDamage(player, turn)
                    else -> 0
                }
                if (damage > 0) {
                    if (currentDefender is Character) currentDefender.takeDamage(damage) else (currentDefender as Monster).takeDamage(damage)
                    log.append("${if (currentAttacker is Character) "Игрок" else (currentAttacker as Monster).name} наносит $damage урона. HP: ${if (currentDefender is Character) currentDefender.currentHealth else (currentDefender as Monster).currentHealth}\n")
                }
            } else {
                log.append("Промах!\n")
            }

            if (!player.isAlive()) return false
            if (!monster.isAlive()) return true

            currentAttacker = currentDefender.also { currentDefender = currentAttacker }
            turn++
        }
        return false
    }
}
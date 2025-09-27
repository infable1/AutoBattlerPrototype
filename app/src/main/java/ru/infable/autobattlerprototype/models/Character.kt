package ru.infable.autobattlerprototype.models

import ru.infable.autobattlerprototype.models.Character
import kotlin.random.Random

class Character {

    // Атрибуты персонажа (начальные значения от 1 до 3)
    var strength: Int = Random.nextInt(1, 4)
    var dexterity: Int = Random.nextInt(1, 4)
    var constitution: Int = Random.nextInt(1, 4)

    // Уровни классов для мультикласса (начальный класс + уровни)
    private val levels = mutableMapOf<CharacterClass, Int>()
    var currentWeapon: Weapon = Weapon.DAGGER // Начальное оружие по умолчанию
    var maxHealth: Int = 0 // Максимальное здоровье
    var currentHealth: Int = 0 // Текущее здоровье
    var winsInRow: Int = 0 // Подряд побед для условия завершения игры

    // Инициализация при выборе класса
    fun initialize(chosenClass: CharacterClass) {
        levels[chosenClass] = 1 // Устанавливаем начальный уровень
        currentWeapon = chosenClass.startingWeapon
        recalculateMaxHealth()
        currentHealth = maxHealth // Восстанавливаем здоровье
    }

    // Пересчет максимального здоровья
    private fun recalculateMaxHealth() {
        maxHealth = levels.entries.sumBy { it.value * it.key.healthPerLevel } + constitution
    }

    // Повышение уровня с выбором нового класса
    fun levelUp(newClass: CharacterClass) {
        levels[newClass] = (levels[newClass] ?: 0) + 1
        when (newClass) {
            CharacterClass.ROGUE -> {
                when (levels[newClass]!!) {
                    2 -> dexterity++ // Ловкость +1 на 2 уровне
                    3 -> applyPoisonEffect() // Яд на 3 уровне
                }
            }
            CharacterClass.WARRIOR -> {
                when (levels[newClass]!!) {
                    2 -> applyShieldEffect() // Щит на 2 уровне
                    3 -> strength++ // Сила +1 на 3 уровне
                }
            }
            CharacterClass.BARBARIAN -> {
                when (levels[newClass]!!) {
                    2 -> applyStoneSkinEffect() // Каменная кожа на 2 уровне
                    3 -> constitution++ // Выносливость +1 на 3 уровне
                }
            }
        }
        recalculateMaxHealth()
        currentHealth = maxHealth // Восстанавливаем здоровье
    }

    // Методы для бонусов (пока как функции, можно переделать в свойства)
    private var hiddenAttack = false // Скрытая атака для Разбойника lvl 1
    private var poisonDamage = 0 // Яд для Разбойника lvl 3
    private var shieldReduction = 0 // Щит для Воина lvl 2
    private var stoneSkinReduction = 0 // Каменная кожа для Варвара lvl 2
    private var rageTurns = 0 // Ярость для Варвара lvl 1

    // Применение эффектов
    private fun applyPoisonEffect() {
        poisonDamage = 1 // Начальный урон яда
    }

    private fun applyShieldEffect() {
        shieldReduction = 3 // -3 урона, если сила > силы врага
    }

    private fun applyStoneSkinEffect() {
        stoneSkinReduction = constitution // Урон снижается на выносливость
    }

    // Логика атаки (упрощенная, для боя)
    fun calculateDamage(target: Character, turn: Int): Int {
        var baseDamage = currentWeapon.damage + strength

        // Бонусы атакующего
        if (levels[CharacterClass.ROGUE] != null && levels[CharacterClass.ROGUE]!! >= 1 && dexterity > target.dexterity) {
            baseDamage += 1 // Скрытая атака
        }
        if (levels[CharacterClass.WARRIOR] != null && levels[CharacterClass.WARRIOR]!! >= 1 && turn == 1) {
            baseDamage *= 2 // Порыв к действию
        }
        if (levels[CharacterClass.BARBARIAN] != null && levels[CharacterClass.BARBARIAN]!! >= 1 && turn <= 3) {
            baseDamage += 2 // Ярость
            rageTurns = 3
        } else if (rageTurns > 0) {
            baseDamage -= 1 // После 3 ходов -1
            rageTurns--
        }

        // Яд (накапливается с каждым ходом после активации)
        if (levels[CharacterClass.ROGUE] != null && levels[CharacterClass.ROGUE]!! >= 3) {
            baseDamage += poisonDamage
            poisonDamage = minOf(poisonDamage + 1, 3) // Максимум +3
        }

        // Учет защиты цели
        var finalDamage = baseDamage
        if (levels[CharacterClass.WARRIOR] != null && levels[CharacterClass.WARRIOR]!! >= 2 && strength > target.strength) {
            finalDamage -= shieldReduction
        }
        if (levels[CharacterClass.BARBARIAN] != null && levels[CharacterClass.BARBARIAN]!! >= 2) {
            finalDamage -= stoneSkinReduction
        }

        return maxOf(finalDamage, 0) // Урон не может быть отрицательным
    }

    // Получение урона
    fun takeDamage(damage: Int) {
        currentHealth = maxOf(currentHealth - damage, 0)
    }

    // Восстановление здоровья
    fun restoreHealth() {
        currentHealth = maxHealth
    }

    // Проверка жив ли персонаж
    fun isAlive(): Boolean = currentHealth > 0
}

}
package ru.infable.autobattlerprototype.models

import kotlin.random.Random

class Character {

    // Атрибуты персонажа (начальные значения от 1 до 3)
    var strength: Int = Random.nextInt(1, 4)
    var dexterity: Int = Random.nextInt(1, 4)
    var constitution: Int = Random.nextInt(1, 4)

    // Уровни классов для мультикласса (начальный класс + уровни)
    val levels = mutableMapOf<CharacterClass, Int>()
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
    fun calculateDamage(target: Monster, turn: Int): Int {
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

data class Monster(
    val name: String,
    val baseHealth: Int,
    val weaponDamage: Int,
    val strength: Int,
    val dexterity: Int,
    val constitution: Int,
    val features: List<String>, // Особенности (например, "double_blunt_damage")
    val reward: Weapon
) {
    var currentHealth: Int = baseHealth // Текущее здоровье

    // Восстановление здоровья до максимума (для нового боя)
    fun restoreHealth() {
        currentHealth = baseHealth
    }

    // Логика атаки (упрощенная, для боя)
    fun calculateDamage(target: Character, turn: Int): Int {
        var baseDamage = weaponDamage + strength

        // Применение особенностей монстра
        when {
            name == "Скелет" && target.currentWeapon.type == WeaponType.BLUNT -> baseDamage *= 2 // Вдвое больше урона от дробящего
            name == "Слайм" && target.currentWeapon.type == WeaponType.SLASHING -> baseDamage = strength // Рубящее не наносит урона, только сила
            name == "Призрак" && dexterity > target.dexterity -> baseDamage += 1 // Скрытая атака
            name == "Голем" -> baseDamage -= target.constitution // Каменная кожа
            name == "Дракон" && turn % 3 == 0 -> baseDamage += 3 // Огненное дыхание каждые 3 хода
        }

        // Учет защиты цели (например, щит Воина)
        var finalDamage = baseDamage
        if (target.levels[CharacterClass.WARRIOR] != null && target.levels[CharacterClass.WARRIOR]!! >= 2 && strength < target.strength) {
            finalDamage -= 3
        }
        if (target.levels[CharacterClass.BARBARIAN] != null && target.levels[CharacterClass.BARBARIAN]!! >= 2) {
            finalDamage -= target.constitution
        }

        return maxOf(finalDamage, 0) // Урон не может быть отрицательным
    }

    // Получение урона
    fun takeDamage(damage: Int) {
        currentHealth = maxOf(currentHealth - damage, 0)
    }

    // Проверка жив ли монстр
    fun isAlive(): Boolean = currentHealth > 0
}

// Компаньон-объект для списка монстров
object MonsterFactory {
    val monsters = listOf(
        Monster("Гоблин", 5, 2, 1, 1, 1, emptyList(), Weapon.DAGGER),
        Monster("Скелет", 10, 2, 2, 2, 1, listOf("double_blunt_damage"), Weapon.CLUB),
        Monster("Слайм", 8, 1, 3, 1, 2, listOf("immune_slashing"), Weapon.SPEAR),
        Monster("Призрак", 6, 3, 1, 3, 1, listOf("hidden_attack"), Weapon.SWORD),
        Monster("Голем", 10, 1, 3, 1, 3, listOf("stone_skin"), Weapon.AXE),
        Monster("Дракон", 20, 4, 3, 3, 3, listOf("fire_breath_3"), Weapon.LEGENDARY_SWORD)
    )

    // Случайный выбор монстра
    fun getRandomMonster(): Monster = monsters.random()
}
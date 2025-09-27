package ru.infable.autobattlerprototype.models

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
package ru.infable.autobattlerprototype.models

// Перечисление для классов персонажей
enum class CharacterClass(val healthPerLevel: Int, val startingWeapon: Weapon) {
    ROGUE(4, Weapon.DAGGER),    // Разбойник
    WARRIOR(5, Weapon.SWORD),   // Воин
    BARBARIAN(6, Weapon.CLUB)   // Варвар
}

// Перечисление для типов оружия
enum class WeaponType {
    SLASHING,   // Рубящий
    BLUNT,      // Дробящий
    PIERCING    // Колющий
}

// Перечисление для оружия
enum class Weapon(val damage: Int, val type: WeaponType) {
    SWORD(3, WeaponType.SLASHING),          // Меч
    CLUB(3, WeaponType.BLUNT),              // Дубина
    DAGGER(2, WeaponType.PIERCING),         // Кинжал
    AXE(4, WeaponType.SLASHING),            // Топор
    SPEAR(3, WeaponType.PIERCING),          // Копьё
    LEGENDARY_SWORD(5, WeaponType.SLASHING) // Легендарный Меч
}
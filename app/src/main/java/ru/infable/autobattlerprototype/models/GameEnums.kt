package ru.infable.autobattlerprototype.models

enum class CharacterClass(val healthPerLevel: Int, val startingWeapon: Weapon) {
    ROGUE(4, Weapon.DAGGER),
    WARRIOR(5, Weapon.SWORD),
    BARBARIAN(6, Weapon.CLUB)
}

enum class WeaponType {
    SLASHING,
    BLUNT,
    PIERCING
}

enum class Weapon(val damage: Int, val type: WeaponType) {
    SWORD(3, WeaponType.SLASHING),
    CLUB(3, WeaponType.BLUNT),
    DAGGER(2, WeaponType.PIERCING),
    AXE(4, WeaponType.SLASHING),
    SPEAR(3, WeaponType.PIERCING),
    LEGENDARY_SWORD(5, WeaponType.SLASHING)
}
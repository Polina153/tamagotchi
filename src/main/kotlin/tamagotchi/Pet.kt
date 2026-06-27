package tamagotchi

enum class PetState { IDLE, EATING, PLAYING, SLEEPING, SAD, SICK, DEAD }

data class Pet(
    val name: String,
    var hunger: Int = 8,
    var happiness: Int = 8,
    var energy: Int = 8,
    var health: Int = 10,
    var age: Int = 0,
    var state: PetState = PetState.IDLE,
    var stateTicksLeft: Int = 0
) {
    fun bar(value: Int) = "█".repeat(value.coerceIn(0,10)) + "░".repeat(10 - value.coerceIn(0,10))

    val ageString: String get() {
        val days = age / 12; val hours = (age % 12) * 2
        return "${days}д ${hours}ч"
    }

    val mood: String get() = when {
        health <= 2           -> "Умирает   "
        health <= 5           -> "Болен     "
        hunger <= 2           -> "Голодный  "
        happiness <= 2        -> "Несчастный"
        energy <= 2           -> "Усталый   "
        happiness >= 8 && hunger >= 7 -> "Счастлив! "
        else                  -> "Доволен   "
    }
}

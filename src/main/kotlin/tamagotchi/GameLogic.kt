package tamagotchi

class GameLogic(private val pet: Pet) {

    fun tick() {
        if (pet.state == PetState.DEAD) return
        pet.age++

        if (pet.stateTicksLeft > 0) {
            pet.stateTicksLeft--
            if (pet.stateTicksLeft == 0) {
                when (pet.state) {
                    PetState.EATING   -> pet.hunger    = (pet.hunger + 3).coerceAtMost(10)
                    PetState.PLAYING  -> { pet.happiness = (pet.happiness + 3).coerceAtMost(10)
                                          pet.energy    = (pet.energy - 2).coerceAtLeast(0) }
                    PetState.SLEEPING -> pet.energy    = (pet.energy + 5).coerceAtMost(10)
                    else -> {}
                }
                pet.state = PetState.IDLE
            }
            return
        }

        pet.hunger    = (pet.hunger    - 1).coerceAtLeast(0)
        pet.happiness = (pet.happiness - 1).coerceAtLeast(0)

        if (pet.hunger <= 2 || pet.happiness <= 2) {
            pet.health = (pet.health - 1).coerceAtLeast(0)
        } else if (pet.health < 10 && pet.hunger >= 6 && pet.happiness >= 6) {
            pet.health = (pet.health + 1).coerceAtMost(10)
        }

        pet.state = when {
            pet.health == 0 -> PetState.DEAD
            pet.health <= 4 -> PetState.SICK
            pet.hunger <= 2 -> PetState.SAD
            pet.happiness <= 2 -> PetState.SAD
            else -> PetState.IDLE
        }
    }

    fun feed(): String {
        if (pet.state == PetState.DEAD)    return "Уже поздно..."
        if (pet.state == PetState.SLEEPING) return "${pet.name} спит, не буди!"
        if (pet.stateTicksLeft > 0)         return "${pet.name} сейчас занят..."
        if (pet.hunger >= 9)               return "${pet.name} не хочет есть"
        pet.state = PetState.EATING; pet.stateTicksLeft = 2
        return "Ням-ням!"
    }

    fun play(): String {
        if (pet.state == PetState.DEAD)    return "Уже поздно..."
        if (pet.state == PetState.SLEEPING) return "${pet.name} спит!"
        if (pet.stateTicksLeft > 0)         return "${pet.name} сейчас занят..."
        if (pet.energy <= 2)               return "${pet.name} слишком устал"
        if (pet.happiness >= 9)            return "${pet.name} и так счастлив!"
        pet.state = PetState.PLAYING; pet.stateTicksLeft = 3
        return "Играем!"
    }

    fun sleep(): String {
        if (pet.state == PetState.DEAD)  return "Уже поздно..."
        if (pet.stateTicksLeft > 0)       return "${pet.name} сейчас занят..."
        if (pet.energy >= 9)             return "${pet.name} не хочет спать"
        pet.state = PetState.SLEEPING; pet.stateTicksLeft = 4
        return "Сладких снов!"
    }

    fun heal(): String {
        if (pet.state == PetState.DEAD) return "Уже поздно..."
        if (pet.health >= 8)            return "${pet.name} здоров!"
        pet.health = (pet.health + 3).coerceAtMost(10)
        pet.state = PetState.IDLE
        return "Дали лекарство!"
    }
}

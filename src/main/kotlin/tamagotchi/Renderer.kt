package tamagotchi

object Ansi {
    const val RESET       = "\u001B[0m"
    const val CLEAR       = "\u001B[2J\u001B[H"
    const val HIDE_CURSOR = "\u001B[?25l"
    const val SHOW_CURSOR = "\u001B[?25h"
    const val WHITE       = "\u001B[97m"
    const val GRAY        = "\u001B[90m"
    const val CYAN        = "\u001B[96m"
    const val YELLOW      = "\u001B[93m"
    const val GREEN       = "\u001B[92m"
    const val RED         = "\u001B[91m"
    const val BLUE        = "\u001B[94m"
    const val MAGENTA     = "\u001B[95m"
    const val BG_WHITE    = "\u001B[47m"
    const val FG_BLACK    = "\u001B[30m"

    fun move(row: Int, col: Int) = "\u001B[${row};${col}H"
    fun barColor(v: Int) = if (v <= 3) RED else if (v <= 6) YELLOW else GREEN
    fun petColor(s: PetState) = when (s) {
        PetState.DEAD     -> GRAY;    PetState.SICK  -> MAGENTA
        PetState.SAD      -> BLUE;    PetState.SLEEPING -> CYAN
        PetState.PLAYING  -> YELLOW;  PetState.EATING -> GREEN
        PetState.IDLE     -> WHITE
    }
}

class Renderer {
    private val col1 = 1;  private val col2 = 24; private val col3 = 55
    private val W = 77;    private val H = 22

    fun draw(pet: Pet, frame: Int, message: String,
             menu: List<String>, selected: Int, hint: String) {
        val sb = StringBuilder()
        sb.append(Ansi.CLEAR).append(Ansi.HIDE_CURSOR)
        drawFrame(sb)
        drawTitle(sb, pet.name)
        drawPet(sb, pet, frame)
        drawStats(sb, pet)
        drawMenu(sb, menu, selected, hint)
        drawMessage(sb, message)
        print(sb)
    }

    private fun sep(n: Int) = "─".repeat(n)

    private fun drawFrame(sb: StringBuilder) {
        sb.append(Ansi.GRAY)
        sb.append(Ansi.move(2, 1))
        sb.append("┌${sep(22)}┬${sep(30)}┬${sep(21)}┐")
        for (r in 3..20) {
            sb.append(Ansi.move(r, 1));  sb.append("│")
            sb.append(Ansi.move(r, 24)); sb.append("│")
            sb.append(Ansi.move(r, 55)); sb.append("│")
            sb.append(Ansi.move(r, 77)); sb.append("│")
        }
        sb.append(Ansi.move(21, 1)); sb.append("├${sep(75)}┤")
        sb.append(Ansi.move(22, 1)); sb.append("└${sep(75)}┘")
        sb.append(Ansi.RESET)
    }

    private fun drawTitle(sb: StringBuilder, name: String) {
        sb.append(Ansi.move(1, 1)).append(Ansi.CYAN)
        sb.append(" *** Тамагочи: $name ***".padEnd(W))
        sb.append(Ansi.RESET)
    }

    private fun drawPet(sb: StringBuilder, pet: Pet, frame: Int) {
        sb.append(Ansi.move(3, 5)).append("${Ansi.YELLOW}  Питомец  ${Ansi.RESET}")
        val anim = Animations.getFrame(pet.state, frame)
        val color = Ansi.petColor(pet.state)
        anim.forEachIndexed { i, l ->
            sb.append(Ansi.move(6 + i, 7)).append(color).append(l).append(Ansi.RESET)
        }
        val label = when (pet.state) {
            PetState.IDLE     -> "   ~ idle ~  "
            PetState.EATING   -> "   ~ ест ~   "
            PetState.PLAYING  -> " ~ играет ~  "
            PetState.SLEEPING -> "  ~ спит ~   "
            PetState.SAD      -> " ~ грустит ~ "
            PetState.SICK     -> "  ~ болен ~  "
            PetState.DEAD     -> " ~ умер...   "
        }
        sb.append(Ansi.move(11, 5)).append(color).append(label).append(Ansi.RESET)
        sb.append(Ansi.move(13, 4)).append("${Ansi.GRAY}Возраст: ${pet.ageString}${Ansi.RESET}")
    }

    private fun drawStats(sb: StringBuilder, pet: Pet) {
        val c = col2 + 1
        sb.append(Ansi.move(3, c + 6)).append("${Ansi.YELLOW}Статистика${Ansi.RESET}")
        fun stat(label: String, value: Int, row: Int) {
            sb.append(Ansi.move(row,     c)).append("${Ansi.WHITE}$label${Ansi.RESET}")
            sb.append(Ansi.move(row + 1, c))
              .append(Ansi.barColor(value)).append(" ${pet.bar(value)} $value/10 ")
              .append(Ansi.RESET)
        }
        stat("Сытость:  ", pet.hunger,    5)
        stat("Счастье:  ", pet.happiness, 8)
        stat("Энергия:  ", pet.energy,    11)
        stat("Здоровье: ", pet.health,    14)
        sb.append(Ansi.move(18, c)).append("${Ansi.CYAN}Настрой: ${pet.mood}${Ansi.RESET}")
    }

    private fun drawMenu(sb: StringBuilder, items: List<String>, selected: Int, hint: String) {
        val c = col3 + 1
        sb.append(Ansi.move(3, c + 3)).append("${Ansi.YELLOW}Действия${Ansi.RESET}")
        items.forEachIndexed { i, item ->
            val row = 5 + i * 2
            sb.append(Ansi.move(row, c))
            if (i == selected)
                sb.append("${Ansi.BG_WHITE}${Ansi.FG_BLACK} $item ${Ansi.RESET}")
            else
                sb.append("${Ansi.WHITE} $item ${Ansi.RESET}")
        }
        // Подсказка динамическая — разная для Unix/Windows
        sb.append(Ansi.move(19, c)).append("${Ansi.GRAY}${hint.take(20)}${Ansi.RESET}")
        if (hint.length > 20)
            sb.append(Ansi.move(20, c)).append("${Ansi.GRAY}${hint.drop(20).take(20)}${Ansi.RESET}")
    }

    private fun drawMessage(sb: StringBuilder, message: String) {
        sb.append(Ansi.move(22, 2)).append(Ansi.CYAN)
        sb.append(" $message".padEnd(W - 1)).append(Ansi.RESET)
    }

    fun cleanup() {
        print(Ansi.SHOW_CURSOR)
        print(Ansi.move(23, 1))
    }
}

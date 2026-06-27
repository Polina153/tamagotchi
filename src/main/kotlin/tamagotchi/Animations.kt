package tamagotchi

object Animations {
    private val frames = mapOf(
        PetState.IDLE to listOf(
            listOf("  /\\_/\\  ", " ( o.o ) ", "  > ^ <  ", "         "),
            listOf("  /\\_/\\  ", " ( -.- ) ", "  > ^ <  ", "         "),
            listOf("  /\\_/\\  ", " ( o.o ) ", "  > ^ <  ", "    ~    ")
        ),
        PetState.EATING to listOf(
            listOf("  /\\_/\\  ", " ( ^U^ ) ", "  > ^ <  ", " [_nom_] "),
            listOf("  /\\_/\\  ", " ( =U= ) ", "  > ^ <  ", " [nom__] ")
        ),
        PetState.PLAYING to listOf(
            listOf("  /\\_/\\  ", " ( ^o^ ) ", "  > ^ < /", "    *    "),
            listOf("  /\\_/\\ /", " ( ^o^ ) ", "  > ^ <  ", "  *      "),
            listOf("\\ /\\_/\\  ", " ( ^o^ ) ", "  > ^ <  ", "      *  ")
        ),
        PetState.SLEEPING to listOf(
            listOf("  /\\_/\\  ", " ( -.- ) ", "  > ^ <  ", " z       "),
            listOf("  /\\_/\\  ", " ( -.- ) ", "  > ^ <  ", "   Z     "),
            listOf("  /\\_/\\  ", " ( -.- ) ", "  > ^ <  ", "    ZZz  ")
        ),
        PetState.SAD to listOf(
            listOf("  /\\_/\\  ", " ( ;-; ) ", "  > ^ <  ", "  *sob*  "),
            listOf("  /\\_/\\  ", " ( T_T ) ", "  > ^ <  ", "         ")
        ),
        PetState.SICK to listOf(
            listOf("  /\\_/\\  ", " ( x.x ) ", "  > ^ <  ", "  ~ugh~  "),
            listOf("  /\\_/\\  ", " ( @.@ ) ", "  > ^ <  ", "  ~sick~ ")
        ),
        PetState.DEAD to listOf(
            listOf("  /\\_/\\  ", " ( x_x ) ", "  > ^ <  ", "  R.I.P  ")
        )
    )

    fun getFrame(state: PetState, tick: Int): List<String> {
        val f = frames[state] ?: frames[PetState.IDLE]!!
        return f[tick % f.size]
    }
}

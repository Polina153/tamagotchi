package tamagotchi

import java.util.Scanner

object GameState {
    @Volatile var running   = true
    @Volatile var animFrame = 0
    @Volatile var selected  = 0
    @Volatile var message   = ""
    @Volatile var msgTicks  = 0
}

val isWindows = System.getProperty("os.name").toLowerCase().contains("win")

fun main() {
    val rawModeEnabled = if (!isWindows) {
        val exit = Runtime.getRuntime()
            .exec(arrayOf("sh", "-c", "stty -echo -icanon min 1 < /dev/tty"))
            .waitFor()
        exit == 0
    } else false

    print("Введите имя питомца: ")
    System.out.flush()
    val name = readLine()?.trim()?.takeIf { it.isNotEmpty() } ?: "Мурзик"

    val pet      = Pet(name = name)
    val logic    = GameLogic(pet)
    val renderer = Renderer()

    val menuItems = listOf(
        "1. Покормить    ",
        "2. Поиграть     ",
        "3. Уложить спать",
        "4. Дать лекарство",
        "0. Выход        "
    )

    GameState.message  = "Привет! Я ${pet.name}!"
    GameState.msgTicks = 8

    val lock = Any()

    val gameTicker = Thread {
        while (GameState.running) {
            Thread.sleep(4000)
            synchronized(lock) { logic.tick() }
        }
    }

    val renderLoop = Thread {
        while (GameState.running) {
            Thread.sleep(500)
            GameState.animFrame++
            synchronized(lock) {
                renderer.draw(
                    pet, GameState.animFrame, GameState.message,
                    menuItems, GameState.selected,
                    hint = if (rawModeEnabled) "Стрелки + Enter, 1-4, 0-выход"
                           else                "Введи цифру + Enter: 1-корм 2-игра 3-сон 4-лечить 0-выход"
                )
                if (GameState.msgTicks > 0) {
                    GameState.msgTicks--
                }
            }
        }
    }

    gameTicker.isDaemon = true
    renderLoop.isDaemon = true
    gameTicker.start()
    renderLoop.start()

    try {
        if (rawModeEnabled) {
            inputRaw(logic, menuItems, lock)
        } else {
            inputScanner(logic, lock)
        }
    } finally {
        if (rawModeEnabled) {
            Runtime.getRuntime()
                .exec(arrayOf("sh", "-c", "stty echo icanon < /dev/tty"))
                .waitFor()
        }
        renderer.cleanup()
        println("\nДо свидания! ${pet.name} будет скучать.")
    }
}

// Unix: raw mode — стрелки + Enter без буферизации
fun inputRaw(logic: GameLogic, menuItems: List<String>, lock: Any) {
    val input = System.`in`
    while (GameState.running) {
        val b = input.read()
        if (b == -1) break
        synchronized(lock) {
            when (b) {
                27 -> {
                    val b2 = input.read()
                    if (b2 == 91) {
                        when (input.read()) {
                            65 -> GameState.selected = (GameState.selected - 1 + menuItems.size) % menuItems.size
                            66 -> GameState.selected = (GameState.selected + 1) % menuItems.size
                        }
                    }
                }
                10, 13 -> handleMenuAction(logic, GameState.selected)
                49 -> handleAction(logic.feed())
                50 -> handleAction(logic.play())
                51 -> handleAction(logic.sleep())
                52 -> handleAction(logic.heal())
                48, 113, 81 -> GameState.running = false
            }
        }
    }
}

// Windows: обычный Scanner — вводим цифру и жмём Enter
fun inputScanner(logic: GameLogic, lock: Any) {
    val scanner = Scanner(System.`in`)
    while (GameState.running && scanner.hasNextLine()) {
        val line = scanner.nextLine().trim()
        synchronized(lock) {
            when (line) {
                "1"       -> handleAction(logic.feed())
                "2"       -> handleAction(logic.play())
                "3"       -> handleAction(logic.sleep())
                "4"       -> handleAction(logic.heal())
                "0", "q", "Q" -> GameState.running = false
            }
        }
    }
}

fun handleMenuAction(logic: GameLogic, selected: Int) {
    val result = when (selected) {
        0 -> logic.feed()
        1 -> logic.play()
        2 -> logic.sleep()
        3 -> logic.heal()
        4 -> { GameState.running = false; "До свидания!" }
        else -> return
    }
    handleAction(result)
}

fun handleAction(result: String) {
    GameState.message  = result
    GameState.msgTicks = 8
}

import java.util.Random

val rand = Random(0) // using a seed to produce same output on each run

enum class Pattern { BLINKER, GLIDER, RANDOM }

class Field(val w: Int, val h: Int) {
    val s = List(h) { BooleanArray(w) }

    operator fun set(x: Int, y: Int, b: Boolean) {
        s[y][x] = b
    }

    fun next(x: Int, y: Int): Boolean {
        var on = 0
        for (i in -1..1) {
            for (j in -1..1) {
                if (state(x + i, y + j) && !(j == 0 && i == 0)) on++
            }
        }
        return on == 3 || (on == 2 && state(x, y))
    }

    fun state(x: Int, y: Int): Boolean {
        if ((x !in 0 until w) || (y !in 0 until h)) return false
        return s[y][x]
    }
}

class Life(val pattern: Pattern) {
    val x: Int
    val y: Int
    var z: Field
    var g: Field

    init {
        when (pattern) {
            Pattern.BLINKER -> {
                x = 3
                y = 3
                z = Field(x, y)
                g = Field(x, y)
                z[0, 1] = true
                z[1, 1] = true
                z[2, 1] = true
            }

            Pattern.GLIDER -> {
                x = 4
                y = 4
                z = Field(x, y)
                g = Field(x, y)
                z[1, 0] = true
                z[2, 1] = true
                for (i in 0..2) z[i, 2] = true
            }

            Pattern.RANDOM -> {
                x = 80
                y = 15
                z = Field(x, y)
                g = Field(x, y)
                for (i in 0 until x * y / 2) {
                    z[rand.nextInt(x), rand.nextInt(y)] = true
                }
            }
        }
    }

    fun step() {
        for (y in 0 until y) {
            for (x in 0 until x) {
                g[x, y] = z.next(x, y)
            }
        }
        val t = z
        z = g
        g = t
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (y in 0 until y) {
            for (x in 0 until x) {
                val c = if (z.state(x, y)) '#' else '.'
                sb.append(c)
            }
            sb.append('\n')
        }
        return sb.toString()
    }
}

fun main() {
    val lives = listOf(
        Triple(Life(Pattern.BLINKER), 3, "BLINKER"),
        Triple(Life(Pattern.GLIDER), 4, "GLIDER"),
        Triple(Life(Pattern.RANDOM), 10, "RANDOM")
    )
    for ((game, gens, title) in lives) {
        println("$title:\n")
        repeat(gens + 1) {
            println("Generation: $it\n$game")
            Thread.sleep(30)
            game.step()
        }
        println()
    }
}
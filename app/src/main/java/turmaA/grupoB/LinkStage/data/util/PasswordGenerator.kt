package turmaA.grupoB.LinkStage.data.util

import kotlin.random.Random

private const val PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#%"

fun generateTemporaryPassword(length: Int = 12): String = buildString(length) {
    repeat(length) {
        append(PASSWORD_CHARS[Random.nextInt(PASSWORD_CHARS.length)])
    }
}

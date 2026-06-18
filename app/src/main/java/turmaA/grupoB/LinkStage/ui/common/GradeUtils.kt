package turmaA.grupoB.LinkStage.ui.common

fun validateGrade(input: String): Float? {
    val normalized = input.replace(",", ".")
    val value = normalized.toFloatOrNull() ?: return null
    if (value < 0f || value > 20f) return null
    val rounded = (value * 10).toInt() / 10f
    return rounded
}

fun formatGrade(value: Float): String =
    if (value == value.toInt().toFloat()) "${value.toInt()}"
    else "${value}".replace(".", ",")

package com.example.calculogastosmensais.util

fun formatDateInput(input: String): String {
    val digits = input.filter { it.isDigit() }.take(8)

    val day = digits.take(2)
    val month = digits.drop(2).take(2)
    val year = digits.drop(4).take(4)

    return buildString {
        append(day)
        if (month.isNotEmpty()) append('/').append(month)
        if (year.isNotEmpty()) append('/').append(year)
    }
}

fun parseMonth(date: String): String? {
    return if (date.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))) {
        date.substring(3)
    } else {
        null
    }
}
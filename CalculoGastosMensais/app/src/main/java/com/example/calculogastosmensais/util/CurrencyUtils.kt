package com.example.calculogastosmensais.util

import java.util.Locale

fun formatCurrency(value: Double): String {

    val locale = Locale.Builder()
        .setLanguage("pt")
        .setRegion("BR")
        .build()

    return String.format(locale, "R$ %.2f", value)
}
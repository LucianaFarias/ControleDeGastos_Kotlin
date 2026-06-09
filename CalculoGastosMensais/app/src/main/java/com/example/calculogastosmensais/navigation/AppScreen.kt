package com.example.calculogastosmensais.navigation

sealed class AppScreen {
    object Main : AppScreen()
    object AddExpense : AppScreen()
    data class MonthDetails(val month: String) : AppScreen()
}
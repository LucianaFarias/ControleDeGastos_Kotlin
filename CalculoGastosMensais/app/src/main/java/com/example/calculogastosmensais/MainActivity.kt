package com.example.calculogastosmensais

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.calculogastosmensais.data.database.DatabaseProvider
import com.example.calculogastosmensais.screens.HomeScreen
import com.example.calculogastosmensais.ui.theme.CalculoGastosMensaisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = DatabaseProvider.getDatabase(this)
        val dao = db.despesaDao()

        setContent {
            CalculoGastosMensaisTheme {
                HomeScreen(dao)
            }
        }
    }
}
package com.example.calculogastosmensais.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "despesas")
data class Despesa(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nome: String,

    val valor: Double,

    val data: String,

    val mes: String
)
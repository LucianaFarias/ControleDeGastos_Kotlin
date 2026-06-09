package com.example.calculogastosmensais.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.calculogastosmensais.data.dao.DespesaDao
import com.example.calculogastosmensais.model.Despesa

@Database(
    entities = [Despesa::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun despesaDao(): DespesaDao
}
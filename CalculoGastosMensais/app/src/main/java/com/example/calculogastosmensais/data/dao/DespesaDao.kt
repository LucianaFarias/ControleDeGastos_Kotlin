package com.example.calculogastosmensais.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.calculogastosmensais.model.Despesa

@Dao
interface DespesaDao {

    @Insert
    suspend fun inserir(despesa: Despesa)

    @Query("SELECT * FROM despesas")
    suspend fun listarTodas(): List<Despesa>

    @Query("SELECT * FROM despesas WHERE mes = :mes")
    suspend fun listarPorMes(mes: String): List<Despesa>

    @Query("SELECT SUM(valor) FROM despesas")
    suspend fun calcularTotal(): Double?
}
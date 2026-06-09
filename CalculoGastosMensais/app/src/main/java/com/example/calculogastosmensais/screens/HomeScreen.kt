package com.example.calculogastosmensais.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.calculogastosmensais.data.dao.DespesaDao
import com.example.calculogastosmensais.model.Despesa
import com.example.calculogastosmensais.navigation.AppScreen
import com.example.calculogastosmensais.util.formatDateInput
import com.example.calculogastosmensais.util.parseMonth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(dao: DespesaDao) {

    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Main) }
    var despesas by remember { mutableStateOf(listOf<Despesa>()) }

    val scope = rememberCoroutineScope()

    // Carrega dados do banco
    LaunchedEffect(currentScreen) {
        despesas = dao.listarTodas()
    }

    val total = despesas.sumOf { it.valor }
    val months = despesas.map { it.mes }.distinct().sortedByDescending { it }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (val screen = currentScreen) {
                            AppScreen.Main -> "Controle de Gastos"
                            AppScreen.AddExpense -> "Nova Despesa"
                            is AppScreen.MonthDetails -> "Detalhes: ${screen.month}"
                        }
                    )
                },
                navigationIcon = {
                    if (currentScreen != AppScreen.Main) {
                        IconButton(onClick = { currentScreen = AppScreen.Main }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White, // Fundo branco para o Scaffold
        floatingActionButton = {
            if (currentScreen == AppScreen.Main) {
                FloatingActionButton(
                    onClick = { currentScreen = AppScreen.AddExpense },
                    modifier = Modifier.padding(bottom = 32.dp) // Sobe o botão
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar Despesa")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val screen = currentScreen) {
                AppScreen.Main -> MainContent(total, months) { currentScreen = AppScreen.MonthDetails(it) }
                AppScreen.AddExpense -> AddExpenseContent(
                    onSave = { nome, valor, data ->
                        val valorDouble = valor.replace(",", ".").toDoubleOrNull() ?: 0.0
                        val month = parseMonth(data)
                        if (nome.isNotBlank() && valorDouble > 0 && month != null) {
                            scope.launch {
                                val despesa = Despesa(nome = nome, valor = valorDouble, data = data, mes = month)
                                dao.inserir(despesa)
                                currentScreen = AppScreen.Main
                            }
                        }
                    },
                    onCancel = { currentScreen = AppScreen.Main }
                )
                is AppScreen.MonthDetails -> MonthDetailsContent(screen.month, despesas.filter { it.mes == screen.month })
            }
        }
    }
}

@Composable
fun MainContent(total: Double, months: List<String>, onMonthClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)) // Cinza bem claro para o card
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Total Acumulado",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "R$ %.2f".format(total),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Resumo por Mês",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (months.isEmpty()) {
            Text(
                "Nenhuma despesa cadastrada ainda.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(months) { month ->
                    OutlinedButton(
                        onClick = { onMonthClick(month) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = month, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun AddExpenseContent(onSave: (String, String, String) -> Unit, onCancel: () -> Unit) {
    var nome by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var data by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome da despesa (ex: Aluguel)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor (R$)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        OutlinedTextField(
            value = data,
            onValueChange = { data = formatDateInput(it) },
            label = { Text("Data (DD/MM/AAAA)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            placeholder = { Text("01/01/2024") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onSave(nome, valor, data) },
            modifier = Modifier.fillMaxWidth(),
            enabled = nome.isNotBlank() && valor.isNotBlank() && data.length == 10
        ) {
            Text("Salvar Despesa")
        }

        TextButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun MonthDetailsContent(month: String, expenses: List<Despesa>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val totalMes = expenses.sumOf { it.valor }
        
        Text(
            text = "Total no mês: R$ %.2f".format(totalMes),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(expenses) { despesa ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = despesa.nome,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = despesa.data,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Text(
                            text = "R$ %.2f".format(despesa.valor),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32) // Verde escuro para valor
                        )
                    }
                }
            }
        }
    }
}
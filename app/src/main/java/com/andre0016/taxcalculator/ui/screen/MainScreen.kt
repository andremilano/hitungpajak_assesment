package com.andre0016.taxcalculator.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.andre0016.taxcalculator.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.app_name))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = stringResource(R.string.tentang_aplikasi),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        TaxCalculatorApp(modifier = Modifier.padding(innerPadding).padding(16.dp))
    }
}

@Composable
fun TaxCalculatorApp(modifier: Modifier = Modifier) {
    var income by remember { mutableStateOf("") }
    var selectedTaxType by remember { mutableStateOf("Pajak Penghasilan (PPH)") }
    var selectedTaxRate by remember { mutableStateOf("5%") }
    var taxResult by remember { mutableDoubleStateOf(0.0) }

    val taxTypes = listOf("Pajak Penghasilan (PPH)", "Pajak Pertambahan Nilai (PPN)")
    val taxRates = listOf("5%", "15%", "25%", "30%")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Input Penghasilan
        OutlinedTextField(
            value = income,
            onValueChange = { income = it },
            label = {
                Text(
                    text = stringResource(R.string.placeholder),
                    style = MaterialTheme.typography.headlineSmall
                )},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown Jenis Pajak
        DropdownSelector(
            label = stringResource(R.string.pajak_type),
            options = taxTypes,
            selectedOption = selectedTaxType,
            onOptionSelected = { selectedTaxType = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown Tarif Pajak (hanya jika memilih PPH)
        if (selectedTaxType == "Pajak Penghasilan (PPH)") {
            DropdownSelector(
                label = stringResource(R.string.pajak_percentage),
                options = taxRates,
                selectedOption = selectedTaxRate,
                onOptionSelected = { selectedTaxRate = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Tombol Hitung Pajak
        Button(
            onClick = {
                val incomeValue = income.toDoubleOrNull() ?: 0.0
                taxResult = when (selectedTaxType) {
                    "Pajak Penghasilan (PPH)" -> calculatePPH(incomeValue, selectedTaxRate)
                    "Pajak Pertambahan Nilai (PPN)" -> calculatePPN(incomeValue)
                    else -> 0.0
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.calculate_tax),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hasil Pajak
        Text(
            text = "Pajak yang harus dibayar: Rp ${taxResult.toInt()}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// Composable untuk Dropdown
@Composable
fun DropdownSelector(label: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(selectedOption)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


// Perhitungan Pajak Penghasilan (PPH) berdasarkan tarif yang dipilih
fun calculatePPH(income: Double, taxRate: String): Double {
    val rate = when (taxRate) {
        "5%" -> 0.05
        "15%" -> 0.15
        "25%" -> 0.25
        "30%" -> 0.30
        else -> 0.0
    }
    return income * rate
}

// Perhitungan Pajak Pertambahan Nilai (PPN) - 11% dari penghasilan
fun calculatePPN(income: Double): Double {
    return income * 0.11
}
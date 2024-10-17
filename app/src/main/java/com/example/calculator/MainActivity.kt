package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var lastResult by remember { mutableStateOf("") } // Хранение последнего результата

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Экран вывода результатов
        Text(
            text = if (input.isEmpty()) result else input,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.End,
            color = Color.Black
        )

        // Кнопки калькулятора
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val buttons = listOf(
                listOf("7", "8", "9", "/"),
                listOf("4", "5", "6", "*"),
                listOf("1", "2", "3", "-"),
                listOf("C", "0", "=", "+")
            )

            for (row in buttons) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (buttonText in row) {
                        CalculatorButton(buttonText) {
                            when (buttonText) {
                                "=" -> {
                                    result = calculateResult(input)
                                    lastResult = result  // Сохраняем результат для дальнейшего использования
                                    input = ""
                                }
                                "C" -> {
                                    input = ""
                                    result = ""
                                    lastResult = ""
                                }
                                else -> {
                                    if (input.isEmpty() && lastResult.isNotEmpty()) {
                                        input = lastResult + buttonText
                                        lastResult = "" // Очистить после использования
                                    } else {
                                        input += buttonText
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(1.dp)
            .size(90.dp)
            .background(Color.Gray)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 30.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

fun calculateResult(input: String): String {
    return try {
        val result = input.toDoubleOrNull() ?: evaluateExpression(input)
        result.toString()
    } catch (e: Exception) {
        "Error"
    }
}

fun evaluateExpression(expression: String): Double {
    val sanitizedExpression = expression.replace(Regex("[^0-9+\\-*/.]"), "")
    return try {
        val tokens = sanitizedExpression.split(Regex("(?<=[+\\-*/])|(?=[+\\-*/])"))
        var result = tokens[0].toDouble()
        var operator: String? = null

        for (i in 1 until tokens.size) {
            if (tokens[i] in listOf("+", "-", "*", "/")) {
                operator = tokens[i]
            } else {
                val number = tokens[i].toDouble()
                result = when (operator) {
                    "+" -> result + number
                    "-" -> result - number
                    "*" -> result * number
                    "/" -> result / number
                    else -> result
                }
            }
        }
        result
    } catch (e: Exception) {
        0.0
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorScreenPreview() {
    CalculatorTheme {
        CalculatorScreen()
    }
}

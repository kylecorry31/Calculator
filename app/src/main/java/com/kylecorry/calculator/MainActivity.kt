package com.kylecorry.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kylecorry.calculator.ui.theme.CalculatorTheme
import java.text.DecimalFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column {
                        var runningTotal by remember {
                            mutableStateOf("")
                        }
                        var operation by remember {
                            mutableStateOf<CalculatorKey?>(null)
                        }
                        val ENTER = 0
                        val RESULT = 1
                        var state by remember {
                            mutableStateOf(ENTER)
                        }
                        var current by remember {
                            mutableStateOf("")
                        }
                        Spacer(modifier = Modifier.weight(1f, true))
                        Text(
                            current,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            fontSize = 48.sp,
                            textAlign = TextAlign.End
                        )
                        CalculatorKeyboard(currentOperation = operation, clearAll = runningTotal.isNotEmpty()) {

                            if (it == CalculatorKey.Clear) {
                                state = ENTER
                                current = ""
                                runningTotal = ""
                                return@CalculatorKeyboard
                            }

                            if (it == CalculatorKey.Equals) {
                                if (operation != null) {
                                    runningTotal =
                                        DecimalFormat.getInstance()
                                            .format(
                                                handleOperation(
                                                    runningTotal.toDoubleOrNull() ?: 0.0,
                                                    current.toDouble(),
                                                    operation!!
                                                )
                                            )
                                    current = runningTotal
                                    operation = null
                                    state = RESULT
                                    return@CalculatorKeyboard
                                } else {
                                    current = DecimalFormat.getInstance()
                                        .format(current.toDoubleOrNull() ?: 0.0)
                                    runningTotal = current
                                    state = RESULT
                                }
                            }

                            val number = getNumber(it)

                            if ((number != null || it == CalculatorKey.Dot) && state == RESULT) {
                                state = ENTER
                                current = ""
                            }

                            if (number != null) {
                                current += number.toString()
                            }

                            if (it == CalculatorKey.Dot && !current.contains('.')) {
                                current += '.'
                            }

                            if (it == CalculatorKey.Negate && current.isNotEmpty()) {
                                state = ENTER
                                current = if (current.startsWith("-")) {
                                    current.removePrefix("-")
                                } else {
                                    "-$current"
                                }
                            }

                            if (it == CalculatorKey.Percent && current.isNotEmpty()) {
                                val total = runningTotal.toDoubleOrNull() ?: 0.0
                                val percent = (current.toDoubleOrNull() ?: 0.0) / 100.0
                                if (operation != null) {
                                    val outcome = when (operation) {
                                        CalculatorKey.Times -> total * percent
                                        CalculatorKey.Plus -> total + total * percent
                                        CalculatorKey.Minus -> total - total * percent
                                        else -> 0.0
                                    }

                                    runningTotal = DecimalFormat.getInstance().format(outcome)
                                    current = runningTotal
                                    state = RESULT
                                } else {
                                    runningTotal =
                                        DecimalFormat.getInstance().format(percent)
                                    current = runningTotal
                                    state = RESULT
                                }

                                operation = null
                            }

                            if (isOperation(it) && current.isNotEmpty() && state == ENTER) {
                                if (operation != null) {
                                    runningTotal =
                                        DecimalFormat.getInstance()
                                            .format(
                                                handleOperation(
                                                    runningTotal.toDoubleOrNull() ?: 0.0,
                                                    current.toDoubleOrNull() ?: 0.0,
                                                    operation!!
                                                )
                                            )
                                    current = runningTotal
                                    state = RESULT
                                } else {
                                    runningTotal = current
                                    state = RESULT
                                }
                            }

                            if (isOperation(it)) {
                                operation = it
                            }

                        }
                    }

                }
            }
        }
    }
}

fun isOperation(key: CalculatorKey): Boolean {
    return when (key) {
        CalculatorKey.Minus -> true
        CalculatorKey.Plus -> true
        CalculatorKey.Divide -> true
        CalculatorKey.Times -> true
        else -> false
    }
}

fun handleOperation(a: Double, b: Double, operation: CalculatorKey): Double {
    return when (operation) {
        CalculatorKey.Minus -> a - b
        CalculatorKey.Plus -> a + b
        CalculatorKey.Divide -> a / b
        else -> a * b
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
fun CalculatorKeyboard(
    currentOperation: CalculatorKey? = null,
    clearAll: Boolean = false,
    onKeyPress: (key: CalculatorKey) -> Unit
) {
    Column(Modifier.padding(8.dp)) {
        Row {
            val numberModifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .padding(8.dp)
            CalculatorButton(text = if (clearAll) "AC" else "C", modifier = numberModifier) {
                onKeyPress(CalculatorKey.Clear)
            }
            CalculatorButton(text = "±", modifier = numberModifier) {
                onKeyPress(CalculatorKey.Negate)
            }
            CalculatorButton(text = "%", modifier = numberModifier) {
                onKeyPress(CalculatorKey.Percent)
            }
            CalculatorButton(
                text = "÷",
                modifier = numberModifier,
                backgroundColor = if (currentOperation == CalculatorKey.Divide) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
                foregroundColor = if (currentOperation == CalculatorKey.Divide) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSecondary
            ) {
                onKeyPress(CalculatorKey.Divide)
            }
        }
        Row {
            val numberModifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .padding(8.dp)
            CalculatorButton(text = "7", modifier = numberModifier) {
                onKeyPress(CalculatorKey.Seven)
            }
            CalculatorButton(text = "8", modifier = numberModifier) {
                onKeyPress(CalculatorKey.Eight)
            }
            CalculatorButton(text = "9", modifier = numberModifier) {
                onKeyPress(CalculatorKey.Nine)
            }
            CalculatorButton(
                text = "×",
                modifier = numberModifier,
                backgroundColor = if (currentOperation == CalculatorKey.Times) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
                foregroundColor = if (currentOperation == CalculatorKey.Times) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSecondary
            ) {
                onKeyPress(CalculatorKey.Times)
            }
        }
        Row {
            val numberModifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .padding(8.dp)
            CalculatorButton(text = "4", modifier = numberModifier) {
                onKeyPress(CalculatorKey.Four)
            }
            CalculatorButton(text = "5", modifier = numberModifier) {
                onKeyPress(CalculatorKey.Five)
            }
            CalculatorButton(text = "6", modifier = numberModifier) {
                onKeyPress(CalculatorKey.Six)
            }
            CalculatorButton(
                text = "-",
                modifier = numberModifier,
                backgroundColor = if (currentOperation == CalculatorKey.Minus) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
                foregroundColor = if (currentOperation == CalculatorKey.Minus) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSecondary
            ) {
                onKeyPress(CalculatorKey.Minus)
            }

        }
        Row {
            val numberModifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .padding(8.dp)
            CalculatorButton(text = "1", modifier = numberModifier) {
                onKeyPress(CalculatorKey.One)
            }
            CalculatorButton(text = "2", modifier = numberModifier) {
                onKeyPress(CalculatorKey.Two)
            }
            CalculatorButton(text = "3", modifier = numberModifier) {
                onKeyPress(CalculatorKey.Three)
            }
            CalculatorButton(
                text = "+",
                modifier = numberModifier,
                backgroundColor = if (currentOperation == CalculatorKey.Plus) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
                foregroundColor = if (currentOperation == CalculatorKey.Plus) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSecondary
            ) {
                onKeyPress(CalculatorKey.Plus)
            }
        }
        Row {
            val numberModifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .padding(8.dp)
            CalculatorButton(
                text = "0",
                modifier = Modifier
                    .weight(2f)
                    .aspectRatio(2f)
                    .padding(8.dp)
            ) {
                onKeyPress(CalculatorKey.Zero)
            }
            CalculatorButton(text = ".", modifier = numberModifier) {
                onKeyPress(CalculatorKey.Dot)
            }
            CalculatorButton(
                text = "=",
                modifier = numberModifier,
                backgroundColor = MaterialTheme.colors.secondary,
                foregroundColor = MaterialTheme.colors.onSecondary
            ) {
                onKeyPress(CalculatorKey.Equals)
            }
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    backgroundColor: Color = MaterialTheme.colors.surface,
    foregroundColor: Color = MaterialTheme.colors.onSurface,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = backgroundColor,
            contentColor = foregroundColor
        )
    ) {
        Text(text, fontSize = 20.sp)
    }
}

fun getNumber(key: CalculatorKey): Int? {
    return when (key) {
        CalculatorKey.Zero -> 0
        CalculatorKey.One -> 1
        CalculatorKey.Two -> 2
        CalculatorKey.Three -> 3
        CalculatorKey.Four -> 4
        CalculatorKey.Five -> 5
        CalculatorKey.Six -> 6
        CalculatorKey.Seven -> 7
        CalculatorKey.Eight -> 8
        CalculatorKey.Nine -> 9
        else -> null
    }
}

enum class CalculatorKey {
    Zero,
    One,
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight,
    Nine,
    Times,
    Minus,
    Plus,
    Divide,
    Equals,
    Dot,
    Negate,
    Clear,
    Percent
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CalculatorTheme {
        CalculatorKeyboard {}
    }
}
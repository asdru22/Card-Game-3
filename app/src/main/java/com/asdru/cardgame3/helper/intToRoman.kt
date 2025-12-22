package com.asdru.cardgame3.helper

fun Int.toRoman(): String {
    if (this <= 0) {
        throw IllegalArgumentException("Roman numerals must be positive integers.")
    }

    val numerals = mapOf(
        1000 to "M",
        900 to "CM",
        500 to "D",
        400 to "CD",
        100 to "C",
        90 to "XC",
        50 to "L",
        40 to "XL",
        10 to "X",
        9 to "IX",
        5 to "V",
        4 to "IV",
        1 to "I"
    )

    var currentNum = this
    val result = StringBuilder()

    for ((value, symbol) in numerals) {
        while (currentNum >= value) {
            result.append(symbol)
            currentNum -= value
        }
    }

    return result.toString()
}
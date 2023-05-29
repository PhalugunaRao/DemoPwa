package com.example.pwaapp.test

fun reverseString(str: String): String {

    val reversedString = StringBuilder()
    for (i in str.length - 1 downTo 0) {
        reversedString.append(str[i])
    }
    return reversedString.toString()
}

fun main() {
    val input = "Hello, World!"
    val reversed = reverseString(input)
    println(reversed)
}

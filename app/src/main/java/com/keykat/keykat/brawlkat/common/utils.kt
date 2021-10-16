package com.keykat.keykat.brawlkat.common


val textList = ('0'..'9') + ('a'..'z') + ('A'..'Z')

fun idChecker(id: String): Boolean {
    id.forEach {
        if (it !in textList) return false
    }
    return true
}
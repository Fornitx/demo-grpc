package com.example.util

import java.net.ServerSocket

const val MSG1 = "123"
const val MSG2 = "Abc"
const val MSG3 = "Foo"

fun findFreePort(): Int = ServerSocket(0).use { it.localPort }

package com.example.util

import java.net.ServerSocket

fun findFreePort(): Int = ServerSocket(0).use { it.localPort }

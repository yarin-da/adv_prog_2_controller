package com.example.flightgearcontroller.model

enum class ConnectionStatus {
    CONNECTED, DISCONNECTED, FAILED
}

class ConnectionInfo(val status: ConnectionStatus, val description: String) {}
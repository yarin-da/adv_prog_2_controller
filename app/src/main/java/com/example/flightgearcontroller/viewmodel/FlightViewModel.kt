package com.example.flightgearcontroller.viewmodel

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FlightViewModel : ViewModel() {

    // edittext and button bindings
    var connectButtonLabel = MutableLiveData("Connect")
    var ipAddress = MutableLiveData("")

    var port = MutableLiveData("")

    // return true if the ip address' format is valid, otherwise false
    private fun validateAddress(ip: String?): Boolean {
        if (ip == null) return false
        val regex = Regex("^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$")
        return ip.matches(regex)
    }

    // return true if the port is valid, otherwise false
    private fun validatePort(port: String?): Boolean {
        // make sure it's a number
        if (port == null || !port.isDigitsOnly()) return false
        // make sure it's a valid port number
        val portInt = port.toInt()
        val maxPort = 65536
        if (portInt < 0 || portInt > maxPort) return false
        return true
    }

    fun connect() {
        
    }

    fun disconnect() {
        
    }

    fun setAttribute(type: String, value: Float) {
        
    }

    fun finish() {
        
    }
}

package com.example.flightgearcontroller.viewmodel


import androidx.core.text.isDigitsOnly
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.flightgearcontroller.model.FlightModel
import com.example.flightgearcontroller.model.ServerHandler

class FlightViewModel : ViewModel() {
    private val model = FlightModel()

    // edittext and button bindings
    var connectButtonLabel = MutableLiveData("Connect")
    var ipAddress = MutableLiveData("")

    var port = MutableLiveData("")

    val isConnected: Boolean
        get() = model.isConnected

    fun addConnectionListener(listener: ServerHandler.ConnectionListener) {
        model.addConnectionListener(listener)
    }

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
        // notify about an error if one of the values is invalid
        if (!validateAddress(ipAddress.value)) {
            model.notifyConnectionFailed("invalid IP address")
        } else if (!validatePort(port.value)) {
            model.notifyConnectionFailed("invalid port")
        } else {
            // parameters are valid, try to connect to the server
            port.value?.let { ipAddress.value?.let { it1 -> model.connect(it1, it.toInt()) } }
        }
    }

    fun disconnect() {
        model.disconnect()
    }

    fun setAttribute(type: String, value: Float) {
        if (model.isConnected) model.send(type, value)
    }

    // called the app closes
    fun finish() {
        model.finish()
    }
}

package com.example.flightgearcontroller.model


import android.util.Log
import java.io.PrintWriter
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.Socket

class ServerHandler {
    // a callback interface
    // called on every relevant update to the connection
    fun interface ConnectionListener {
        fun onConnectionChanged(info: ConnectionInfo)
    }

    // constant reasons for each status
    private val statusConnected = "Connection established"
    private val statusDisconnected = "Disconnected from the server"
    private val statusFailed = "Connection failed"

    // list of listeners for any connection update
    private var connectionListeners = ArrayList<ConnectionListener>()

    // the socket to the server, and output stream for writing to it
    private var socket = Socket()
    private var out: PrintWriter? = null

    // connection status
    private var connectionEstablished: Boolean = false
    val isConnected: Boolean
        get() = connectionEstablished

    fun addConnectionListener(listener: ConnectionListener) {
        connectionListeners.add(listener)
    }

    private fun notifyListeners(info: ConnectionInfo) {
        for (listener in connectionListeners) {
            listener.onConnectionChanged(info)
        }
    }

    // notify listeners that we're connected/disconnected from the server
    private fun setConnectionStatus(connected: Boolean) {
        // update the status
        connectionEstablished = connected
        // notify listeners
        val description = if (connected) statusConnected else statusDisconnected
        val status = if (connected) ConnectionStatus.CONNECTED else ConnectionStatus.DISCONNECTED
        notifyListeners(ConnectionInfo(status, description))
    }

    // notify all listeners that a connection error has occurred
    // e.g. wrong host, wrong ip address, wrong port
    fun notifyConnectionFailed(description: String) {
        val info = ConnectionInfo(ConnectionStatus.FAILED, "$statusFailed: $description")
        notifyListeners(info)
    }

    private fun resetSocket() {
        socket.close()
        socket = Socket()
    }

    fun connect(ip: String, port: Int) {
        // exit if already connected
        if (isConnected) return

        try {
            // connect to the server
            socket.connect(InetSocketAddress(ip, port), 10)
            val outputStream = socket.getOutputStream()
            if (outputStream != null) {
                out = PrintWriter(outputStream, true)
            }
            // set status and notify all listeners that we're connected
            setConnectionStatus(true)
            Log.d("MYINFO", "CONNECTED TO $ip:$port")
        } catch (e: Exception) {
            // reset the socket
            resetSocket()
            // notify all listeners that the connection has failed
            notifyConnectionFailed("socket.connect()")
            Log.d("ERROR", "FAILED TO CONNECT TO $ip:$port")
        }
    }

    fun disconnect() {
        // make sure we're connected
        if (isConnected) {
            resetSocket()
            setConnectionStatus(false)
        }
    }

    fun send(msgContent: String) {
        // exit if we're not connected
        if (!isConnected) return

        try {
            // send the msg to the server with a newline delimiter
            out?.let {
                it.print("$msgContent\r\n")
                it.flush()
            }
        } catch (e: Exception) {
            Log.d("ERROR", "FAILED TO SEND $msgContent")
        }
    }
}

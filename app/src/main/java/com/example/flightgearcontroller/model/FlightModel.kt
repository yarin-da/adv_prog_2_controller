package com.example.flightgearcontroller.model

import android.util.Log
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class FlightModel {
    // a flag for the task fetching loop
    private var shouldFinish = false
    // a queue of tasks
    private val queue = LinkedBlockingQueue<Runnable>()
    // the handler for the server connection
    private var serverHandler = ServerHandler()
    val isConnected
        get() = serverHandler.isConnected

    init {
        // start fetching tasks
        listenForRequests()
    }

    private fun listenForRequests() {
        thread(start=true) {
            while (!shouldFinish) {
                // fetch a task with the blocking 'take'
                val task = queue.take()
                Log.d("MYINFO", "RUNNING TASK")
                // execute the task
                task.run()
            }
            Log.d("MYINFO", "FINISHED TASKS")
        }
    }

    private fun request(task: Runnable) {
        queue.put(task)
    }

    fun finish() {
        request {
            // stop fetching tasks
            shouldFinish = true
            // close the connection to the server (if connected)
            disconnect()
        }
    }

    fun notifyConnectionFailed(description: String) {
        request { serverHandler.notifyConnectionFailed(description) }
    }

    fun addConnectionListener(listener: ServerHandler.ConnectionListener) {
        request { serverHandler.addConnectionListener(listener) }
    }

    fun connect(ip: String, port: Int) {
        request { serverHandler.connect(ip, port) }
    }

    fun disconnect() {
        request { serverHandler.disconnect() }
    }

    private fun send(msgContent: String) {
        request { serverHandler.send(msgContent) }
    }

    fun send(type: String, value: Float) {
        // build the relevant message and send it to the server
        val prefix = "set /controls"
        when(type) {
            "rudder" -> send("$prefix/flight/rudder $value")
            "throttle" -> send("$prefix/engines/current-engine/throttle $value")
            "elevator" -> send("$prefix/flight/elevator $value")
            "aileron" -> send("$prefix/flight/aileron[0] $value")
            else -> Log.d("ERROR", "WRONG ATTRIBUTE TYPE $type")
        }
    }
}

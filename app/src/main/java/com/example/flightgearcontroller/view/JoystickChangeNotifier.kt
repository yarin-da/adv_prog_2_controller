package com.example.flightgearcontroller.view

import com.example.flightgearcontroller.viewmodel.FlightViewModel

// a callback that sends the server new values everytime the joystick's position changes
class JoystickChangeNotifier(private val viewmodel: FlightViewModel) : Joystick.ChangeNotifier {
    // this interface sets a downtime of 'minUpdateOffset'
    private val minUpdateOffset: Long = 50
    private var prevUpdate: Long = 0
    override fun onChange(x: Float, y: Float) {
        val currUpdate: Long = System.currentTimeMillis()
        // check if we should update
        val delta = currUpdate - prevUpdate
        if (delta >= minUpdateOffset) {
            // send the new values
            viewmodel.setAttribute("aileron", x)
            viewmodel.setAttribute("elevator", y)
            // update the timer
            prevUpdate = currUpdate
        }
    }
}
package com.example.flightgearcontroller.view

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.example.flightgearcontroller.viewmodel.FlightViewModel
import com.example.flightgearcontroller.R
import com.example.flightgearcontroller.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val viewmodel: FlightViewModel = FlightViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = setContentView(this, R.layout.activity_main)
        binding.viewmodel = viewmodel
        binding.lifecycleOwner = this

        initSeekBars()
        initJoystick()
        initButton()
        initConnectionListeners()

        viewmodel.connectButtonLabel.observe(this, {
            binding.buttonConnect.text = it
        })
    }

    override fun onDestroy() {
        viewmodel.finish()
        super.onDestroy()
    }

    private fun initButton() {
        val connectButton = findViewById<Button>(R.id.button_connect)
        connectButton.setOnClickListener {
            // connect/disconnect everytime the button is clicked
        }
    }

    private fun initConnectionListeners() {

    }

    private fun initJoystick() {
        val joystick = findViewById<Joystick>(R.id.joystick)
        // listen to the joystick's knob position changes
        joystick.notifier = JoystickChangeNotifier(viewmodel)
    }

    private fun initSeekBars() {
        // listen to the bars' progress changes
        val rudderBar = findViewById<SeekBar>(R.id.seekBar_rudder)
        rudderBar.setOnSeekBarChangeListener(
            BarListener(viewmodel, -1, 1, "rudder")
        )

        val throttleBar = findViewById<SeekBar>(R.id.seekBar_throttle)
        throttleBar.setOnSeekBarChangeListener(
            BarListener(viewmodel, 0, 1, "throttle")
        )
    }
}

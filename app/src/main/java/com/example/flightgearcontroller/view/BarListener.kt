package com.example.flightgearcontroller.view

import android.widget.SeekBar
import com.example.flightgearcontroller.viewmodel.FlightViewModel

class BarListener (
    private val viewModel: FlightViewModel,
    private val min: Int,
    private val max: Int,
    private val attribute: String,
) : SeekBar.OnSeekBarChangeListener {
    // the higher - the more precise that values of the seekbar will be
    private val precision = 200
    // calculates the attribute's value according to the progress
    private fun parseProgress(progress: Int): Float {
        // map the progress value to the relevant attribute value using a line
        val slope: Float = (max - min).toFloat() / precision
        return slope * progress + min
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        val value: Float = parseProgress(progress)
        // send the server the new value
        viewModel.setAttribute(attribute, value)
    }
    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
}

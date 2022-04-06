package com.example.dicedemo

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    private var mMediaPlayer: MediaPlayer? = null
    private val dice = Dice(6)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rollButton: Button = findViewById(R.id.button)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(sensorManager)!!.registerListener(sensorListener, sensorManager!!
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH

       // rollDice()
        rollButton.setOnClickListener {
            rollDice()

        }

    }
    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta
            if (acceleration > 20) {
                rollDice()
                Toast.makeText(applicationContext, "Dice rolled", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }
    override fun onResume() {
        sensorManager?.registerListener(sensorListener, sensorManager!!.getDefaultSensor(
            Sensor .TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }
    override fun onPause() {
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }


    private fun rollDice() {
        val diceRoll = dice.roll()

        val numText : TextView = findViewById(R.id.numText)
        val diceImage: ImageView = findViewById(R.id.imageView)

        val drawableResource = when (diceRoll) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            6 -> R.drawable.dice_6
            else -> R.drawable.dice_6
        }

        numText.text = diceRoll.toString()
        diceImage.setImageResource(drawableResource)
        diceImage.contentDescription = diceRoll.toString()
        playSound(diceRoll)
        Toast.makeText(this, "Dice Rolled! $diceRoll", Toast.LENGTH_SHORT).show()
    }

   private fun playSound(diceNum:Int) {
            mMediaPlayer = when (diceNum) {
                1 -> MediaPlayer.create(this, R.raw.number1)
                2 -> MediaPlayer.create(this, R.raw.number2)
                3 -> MediaPlayer.create(this, R.raw.number3)
                4 -> MediaPlayer.create(this, R.raw.number4)
                5 -> MediaPlayer.create(this, R.raw.number5)
                6 -> MediaPlayer.create(this, R.raw.number6)
                else -> MediaPlayer.create(this, R.raw.number1)
            }
            mMediaPlayer!!.isLooping = false
            mMediaPlayer!!.start()
    }

    override fun onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
        super.onDestroy()
    }
}

class Dice(private val numSides: Int) {

    fun roll(): Int {
        return (1..this.numSides).random()
    }
}
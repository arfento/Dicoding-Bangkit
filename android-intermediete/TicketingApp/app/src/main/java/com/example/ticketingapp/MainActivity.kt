package com.example.ticketingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.ticketingapp.widgets.SeatView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val seatView = findViewById<SeatView>(R.id.seatsView)
        val button = findViewById<Button>(R.id.finishButton)
        button.setOnClickListener {
            seatView.seat?.let {
                Toast.makeText(this, "Kursi Anda nomor ${it.name}.", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(this, "Silakan pilih kursi terlebih dahulu.", Toast.LENGTH_SHORT).show()
            }

        }


    }
}
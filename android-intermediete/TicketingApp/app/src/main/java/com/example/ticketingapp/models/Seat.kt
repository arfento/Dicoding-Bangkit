package com.example.ticketingapp.models

data class Seat(
    val id : Int,
    var x : Float? = 0F,
    var y : Float? = 0F,
    var name : String,
    var isBooked : Boolean,
)
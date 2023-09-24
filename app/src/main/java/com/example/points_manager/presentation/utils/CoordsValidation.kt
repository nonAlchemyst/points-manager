package com.example.points_manager.presentation.utils

object CoordsValidation {

    fun checkLatitude(latitude: Double?): Boolean {
        return if(latitude != null) {
            latitude in -90.0..90.0
        } else {
            false
        }
    }

    fun checkLongitude(longitude: Double?): Boolean {
        return if(longitude != null) {
            longitude in -180.0..180.0
        } else {
            false
        }
    }

}
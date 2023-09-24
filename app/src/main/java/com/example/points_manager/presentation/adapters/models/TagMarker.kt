package com.example.points_manager.presentation.adapters.models

import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class TagMarker(mapView: MapView) : Marker(mapView) {

    var tag: String = ""
}
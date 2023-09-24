package com.example.points_manager.presentation.adapters

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap
import com.example.points_manager.domain.models.Point
import com.example.points_manager.presentation.adapters.models.TagMarker
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


class MapAdapter {

    companion object {
        private const val MARKER_TAG = "marker_tag"
    }

    private lateinit var icon: Drawable

    @SuppressLint("UseCompatLoadingForDrawables")
    fun createIcon(resources: Resources, @DrawableRes pointIcon: Int) {
        val b = resources.getDrawable(pointIcon).toBitmap(100, 100)
        icon = BitmapDrawable(resources, b)
    }

    fun tryGoToMarkerBy(mapView: MapView, pointId: Int) {
        mapView.overlays.forEach {
            if(it is TagMarker && it.id == pointId.toString()) {
                mapView.controller.animateTo(it.position)
                return@forEach
            }
        }
    }

    fun updateMap(mapView: MapView, points: List<Point>, @DrawableRes pointIcon: Int) {
        clearMap(mapView)
        createIcon(mapView.context.resources, pointIcon)
        points.forEach {  point ->
            addPointTo(mapView, point)
        }
        mapView.invalidate()
    }

    fun getCenterPoint(mapView: MapView): Point {
        val centerGeoPoint = mapView.mapCenter
        return Point(id = null, centerGeoPoint.latitude, centerGeoPoint.longitude)
    }

    private fun clearMap(mapView: MapView) {
        mapView.overlays.removeAll { it is TagMarker && it.tag == MARKER_TAG }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addPointTo(mapView: MapView, point: Point) {
        val marker = TagMarker(mapView)
        marker.position = GeoPoint(point.latitude, point.longitude)
        marker.icon = icon
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        marker.infoWindow = null
        marker.tag = MARKER_TAG
        point.id?.let { marker.id = it.toString() }

        mapView.overlays.add(marker)
    }
}
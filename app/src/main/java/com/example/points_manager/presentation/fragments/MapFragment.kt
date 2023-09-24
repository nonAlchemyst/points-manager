package com.example.points_manager.presentation.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.points_manager.R
import com.example.points_manager.databinding.FragmentMapBinding
import com.example.points_manager.domain.models.Point
import com.example.points_manager.presentation.adapters.MapAdapter
import com.example.points_manager.presentation.fragments.base.BaseFragment
import com.example.points_manager.presentation.viewmodels.MainActivityViewModel
import com.example.points_manager.presentation.viewmodels.MapViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsController
import javax.inject.Inject

class MapFragment: BaseFragment<MapViewModel, FragmentMapBinding>() {

    private val mapAdapter = MapAdapter()
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { _: Boolean ->
        checkMapPermissions()
    }
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    override var binding: FragmentMapBinding? = null
    override val viewModel: MapViewModel by viewModels {
        factory
    }

    @Inject
    lateinit var factory: MapViewModel.Factory

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMapBinding.inflate(inflater, container, false)

    override fun onAttach(context: Context) {
        getAppComponent().inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkMapPermissions()
        setupObservers()
    }

    override fun onBackArgumentsReceived(arguments: Bundle) {
        arguments.getInt("id", -1).let { id ->
            if(id != -1) {
                Log.d("TAG", "animate to point: pointId = $id")
                viewModel.setPointId(id)
                //mapAdapter.tryGoToMarkerBy(map, id)
            }
        }
    }

    private fun setupObservers() {
        viewModel.observePoints()
        viewModel.points.onChange {
            updateUI(it)
        }

        sharedViewModel.contextMenuItem.onChange { contextMenuItem ->
            contextMenuItem.getContentIfNotHandled()?.let {
                when(it) {
                    MainActivityViewModel.ContextMenuItem.SAVE_POINT -> {
                        viewModel.navigateToDialog(mapAdapter.getCenterPoint(binding!!.map))
                    }
                    MainActivityViewModel.ContextMenuItem.POINTS_LIST -> {
                        viewModel.navigateToPointsList()
                    }
                }
            }
        }
    }

    private fun setupMap() = with(binding!!) {
        Configuration.getInstance().userAgentValue = requireContext().packageName
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setUseDataConnection(true)
        map.setMultiTouchControls(true)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        map.minZoomLevel = 5.0
        map.maxZoomLevel = 10.0
        map.controller.setZoom(5.0)
    }

    private fun updateUI(points: List<Point>) {
        if(view == null) return
        mapAdapter.updateMap(binding!!.map, points, R.drawable.ic_map_point)
        viewModel.getPointId()?.let {
            mapAdapter.tryGoToMarkerBy(binding!!.map, it)
        }
    }

    private fun checkMapPermissions() {
        val context = requireContext()
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    setupMap()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    override fun onResume() {
        super.onResume()
        binding!!.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding!!.map.onPause()
    }

}
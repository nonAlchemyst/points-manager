package com.example.points_manager.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.example.points_manager.databinding.FragmentPointsBinding
import com.example.points_manager.domain.models.Point
import com.example.points_manager.presentation.adapters.PointsAdapter
import com.example.points_manager.presentation.fragments.base.BaseFragment
import com.example.points_manager.presentation.utils.PointItemDecoration
import com.example.points_manager.presentation.viewmodels.PointsListViewModel
import javax.inject.Inject

class PointsFragment: BaseFragment<PointsListViewModel, FragmentPointsBinding>() {

    private val adapter = PointsAdapter()
    override var binding: FragmentPointsBinding? = null
    override val viewModel: PointsListViewModel by viewModels {
        factory
    }

    @Inject
    lateinit var factory: PointsListViewModel.Factory

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPointsBinding.inflate(inflater, container, false)

    override fun onAttach(context: Context) {
        getAppComponent().inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        setupObservers()
    }

    private fun setupList() = with(binding!!) {
        list.adapter = adapter
        list.addItemDecoration(PointItemDecoration(4, requireContext()))
        adapter.clickListener = { point ->
            viewModel.navigateBack(bundleOf(
                "id" to point.id
            ))
        }
    }

    private fun setupObservers() {
        viewModel.observePoints()
        viewModel.points.onChange {
            updateUI(it)
        }
    }

    private fun updateUI(points: List<Point>) {
        adapter.update(points)
    }
}
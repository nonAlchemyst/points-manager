package com.example.points_manager.presentation.fragments.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.example.points_manager.di.AppComponent
import com.example.points_manager.presentation.App
import com.example.points_manager.presentation.navigation.NavigationCommand
import com.example.points_manager.presentation.viewmodels.base.BaseViewModel


abstract class BaseFragment<VM: BaseViewModel, BINDING: ViewBinding>(): Fragment() {

    companion object {
        private const val navBackKey = "nav_back_key"
    }

    protected abstract val viewModel: VM
    protected abstract var binding: BINDING?

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): BINDING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.navigateBack()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflateBinding(inflater, container)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeNavigation()
    }

    open fun onBackArgumentsReceived(arguments: Bundle) {}

    private fun observeNavigation() {
        viewModel.navigation.nonNullOnChange { event ->
            event.getContentIfNotHandled()?.let { navigationCommand ->
                handleNavigation(navigationCommand)
            }
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(navBackKey)?.observe(viewLifecycleOwner) { result ->
            onBackArgumentsReceived(result)
        }
    }

    private fun handleNavigation(navCommand: NavigationCommand) {
        when (navCommand) {
            is NavigationCommand.ToDirection -> findNavController().navigate(navCommand.directions, navCommand.arguments)
            is NavigationCommand.Back -> {
                val navController = findNavController()
                navCommand.arguments?.let {
                    navController.previousBackStackEntry?.savedStateHandle?.set(navBackKey, it)
                }
                navController.popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    fun Fragment.getAppComponent(): AppComponent {
        return (requireContext().applicationContext as App).appComponent
    }

    fun <T> LiveData<T>.onChange(block: (T) -> Unit) {
        this.observe(viewLifecycleOwner, block)
    }

    fun <T> LiveData<T>.nonNullOnChange(block: (T) -> Unit) {
        this.observe(viewLifecycleOwner) {
            it?.let(block)
        }
    }

}
package com.example.points_manager.presentation.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.points_manager.databinding.DialogCreatePointBinding
import com.example.points_manager.presentation.App
import com.example.points_manager.presentation.viewmodels.CreatePointViewModel
import javax.inject.Inject

class CreatePointDialogFragment: DialogFragment() {

    companion object {
        const val ARG_LATITUDE = "latitude"
        const val ARG_LONGITUDE = "longitude"
    }

    private val latitude get() = arguments?.getString(ARG_LATITUDE)
    private val longitude get() = arguments?.getString(ARG_LONGITUDE)
    private val viewModel: CreatePointViewModel by viewModels {
        factory.create(latitude, longitude)
    }
    private var binding: DialogCreatePointBinding? = null

    @Inject
    lateinit var factory: CreatePointViewModel.Factory.Factory1

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCreatePointBinding.inflate(inflater, container, false)
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initEditTexts(latitude.toString(), longitude.toString())
        setupObservers()
    }

    private fun initEditTexts(latitude: String, longitude: String) {
        binding?.let {
            it.latitude.setCoordinate(latitude, '°')
            it.longitude.setCoordinate(longitude, '°')
        }

    }

    private fun setupObservers() = with(binding!!) {
        latitude.addTextWatcherTo( '°', 12) {
            viewModel.latitudeOnChange(it.removeSuffix("°"))
        }
        longitude.addTextWatcherTo( '°', 12) {
            viewModel.longitudeOnChange(it.removeSuffix("°"))
        }
        saveBtn.setOnClickListener {
            viewModel.saveOnClick()
        }
        viewModel.message.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                if(message.isNotEmpty()) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.needDismiss.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { needDismiss ->
                if(needDismiss) {
                    dismiss()
                }
            }
        }
    }

    fun EditText.addTextWatcherTo(endSymbol: Char, length: Int, block: (String) -> Unit) {
        this.addTextChangedListener { text ->
            text?.let {
                if(it.isEmpty()) {
                    block(it.toString())
                    return@let
                } else if(it.length > length) {
                    val newString = StringBuilder(it.substring(0, length - 1)).apply {
                        append(endSymbol)
                    }
                    this.setText(newString.toString())
                } else if(it.length == length) {
                    if(it.last() != endSymbol) {
                        val newString = StringBuilder(it.dropLast(1)).apply {
                            append(endSymbol)
                        }
                        this.setText(newString.toString())
                    }
                } else {
                    if(it.last() != endSymbol) {
                        val newString = StringBuilder(it).apply {
                            append(endSymbol)
                        }
                        this.setText(newString.toString())
                    }
                }
                block(it.toString())
            }
        }
    }

    fun EditText.setCoordinate(value: String, lastSymbol: Char) {
        val newValue = StringBuilder(value).append(lastSymbol).toString()
        this.setText(if(value.isNotEmpty()) {
            if(value.last() != lastSymbol) {
                newValue
            } else {
                value
            }
        } else {
            newValue
        })
    }

    val Context.appComponent
        get() = (this.applicationContext as App).appComponent

}
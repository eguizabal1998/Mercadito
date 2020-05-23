package com.basicdeb.mercadito.ui.config

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.basicdeb.mercadito.R
import com.basicdeb.mercadito.data.FiredbRepoImpl
import com.basicdeb.mercadito.databinding.ConfigFragmentBinding
import com.basicdeb.mercadito.domain.FiredbUseCaseImpl
import com.basicdeb.mercadito.viewmodel.config.ConfigViewModel
import com.basicdeb.mercadito.viewmodel.perfil.DBFactory
import com.basicdeb.mercadito.vo.Resource
import com.google.android.material.snackbar.Snackbar

class ConfigFragment : Fragment() {

    private lateinit var viewModel: ConfigViewModel
    private lateinit var binding: ConfigFragmentBinding
    private lateinit var factory: DBFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        factory = DBFactory(FiredbUseCaseImpl(FiredbRepoImpl()))
        binding = DataBindingUtil.inflate(inflater,R.layout.config_fragment,container,false)
        viewModel = ViewModelProvider(this,factory).get(ConfigViewModel::class.java)

        binding.viewModel = viewModel

        listeners()
        observers()

        asignar()

        return binding.root
    }

    fun asignar(){
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val defaultValue = sharedPref.getString("departamento","vacio")
        viewModel.departamento = defaultValue.toString()

        viewModel.obtener()
    }

    private fun observers() {
        viewModel.eventoGuardar.observe(this.viewLifecycleOwner, Observer {
            when(it){
                is Resource.Loading -> {
                    binding.progressBarConfig.visibility = View.VISIBLE
                    binding.btnConfigGuardar.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBarConfig.visibility = View.GONE
                    binding.btnConfigGuardar.isEnabled = true
                    Snackbar.make(this.view!!, "Guardado", Snackbar.LENGTH_LONG).show()
                }
                is Resource.Failure -> {
                    binding.progressBarConfig.visibility = View.GONE
                    binding.btnConfigGuardar.isEnabled = true
                    Snackbar.make(this.view!!, it.exception.message.toString(), Snackbar.LENGTH_LONG).show()
                }
            }
        })

        viewModel.eventoObtener.observe(this.viewLifecycleOwner, Observer {
            when(it){
                is Resource.Loading -> {
                    binding.progressBarConfig.visibility = View.VISIBLE
                    binding.btnConfigGuardar.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBarConfig.visibility = View.GONE
                    binding.btnConfigGuardar.isEnabled = true

                    binding.switch1.isChecked = it.data.lunes
                    binding.switch2.isChecked = it.data.martes
                    binding.switch3.isChecked = it.data.miercoles
                    binding.switch4.isChecked = it.data.jueves
                    binding.switch5.isChecked = it.data.viernes
                    binding.switch6.isChecked = it.data.sabado
                    binding.switch7.isChecked = it.data.domingo

                    binding.timePickerApertura.hour = it.data.horaA
                    binding.timePickerApertura.minute = it.data.minutoA
                    binding.timePickerCierre.hour = it.data.horaC
                    binding.timePickerCierre.minute = it.data.minutoC
                }
                is Resource.Failure -> {
                    binding.progressBarConfig.visibility = View.GONE
                    binding.btnConfigGuardar.isEnabled = true
                    Snackbar.make(this.view!!, it.exception.message.toString(), Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun listeners() {
        binding.timePickerApertura.setOnTimeChangedListener { view, hourOfDay, minute ->
            viewModel.horaA.set(hourOfDay)
            viewModel.minutoA.set(minute)
        }

        binding.timePickerCierre.setOnTimeChangedListener { view, hourOfDay, minute ->
            viewModel.horaC.set(hourOfDay)
            viewModel.minutoC.set(minute)
        }
    }


}

package com.basicdeb.mercadito.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.basicdeb.mercadito.R
import com.basicdeb.mercadito.databinding.RegisterFragmentBinding
import com.basicdeb.mercadito.domain.FireAuthUseCaseImpl
import com.basicdeb.mercadito.viewmodel.login.AuthFactory
import com.basicdeb.mercadito.viewmodel.login.RegisterViewModel
import com.basicdeb.mercadito.vo.Resource
import com.basicdeb.mercadito.data.FireAuthRepoImpl
import com.google.android.material.snackbar.Snackbar

class RegisterFragment : Fragment() {
    private lateinit var viewModel: RegisterViewModel
    private lateinit var binding: RegisterFragmentBinding
    private lateinit var factory: AuthFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.register_fragment,container,false)
        factory = AuthFactory(
            FireAuthUseCaseImpl(FireAuthRepoImpl())
        )
        viewModel = ViewModelProvider(this,factory).get(RegisterViewModel::class.java)

        binding.viewModel = viewModel

        binding.btnRegisterYatengocuenta.setOnClickListener {
            this.findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }

        binding.btnRegisterBack.setOnClickListener {
            this.findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }

        observers()

        return binding.root
    }

    fun observers(){
        viewModel.eventoRegistro.observe(this.viewLifecycleOwner, Observer {result ->
            when(result) {
                is Resource.Loading ->{
                    binding.cirRegisterButtonRegister.visibility = View.VISIBLE
                    binding.btnRegisterRegistro.visibility = View.GONE
                }
                is Resource.Success ->{
                    binding.cirRegisterButtonRegister.visibility = View.GONE
                    binding.btnRegisterRegistro.visibility = View.VISIBLE
                    Snackbar.make(this.view!!,"Se ha enviado un correo para verificacion", Snackbar.LENGTH_LONG).show()
                }
                is Resource.Failure ->{
                    binding.cirRegisterButtonRegister.visibility = View.GONE
                    binding.btnRegisterRegistro.visibility = View.VISIBLE
                    Snackbar.make(this.view!!, result.exception.message.toString(), Snackbar.LENGTH_LONG).show()
                }
            }
        })

        viewModel.eventoGoogle.observe(this.viewLifecycleOwner, Observer {result ->
            when(result) {
                is Resource.Loading ->{
                    binding.cirRegisterButtonRegister.visibility = View.VISIBLE
                }
                is Resource.Success ->{
                    binding.cirRegisterButtonRegister.visibility = View.GONE
                    Snackbar.make(this.view!!,result.data.toString(), Snackbar.LENGTH_LONG).show()
                }
                is Resource.Failure ->{
                    binding.cirRegisterButtonRegister.visibility = View.GONE
                    Snackbar.make(this.view!!, result.exception.message.toString(), Snackbar.LENGTH_LONG).show()
                }
            }
        })

        viewModel.eventoFacebook.observe(this.viewLifecycleOwner, Observer {result ->
            when(result) {
                is Resource.Loading ->{
                    binding.cirRegisterButtonRegister.visibility = View.VISIBLE
                }
                is Resource.Success ->{
                    binding.cirRegisterButtonRegister.visibility = View.GONE
                    Snackbar.make(this.view!!,result.data.toString(), Snackbar.LENGTH_LONG).show()
                }
                is Resource.Failure ->{
                    binding.cirRegisterButtonRegister.visibility = View.GONE
                    Snackbar.make(this.view!!, result.exception.message.toString(), Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }


}

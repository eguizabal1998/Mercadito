package com.basicdeb.mercadito.ui.config

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.basicdeb.mercadito.MainActivity

import com.basicdeb.mercadito.R
import com.basicdeb.mercadito.domain.FireAuthUseCaseImpl
import com.basicdeb.mercadito.viewmodel.login.AuthFactory
import com.basicdeb.mercadito.data.FireAuthRepoImpl

class CerrarFragment() : Fragment() {

    private lateinit var viewModel: CerrarViewModel
    private lateinit var factory: AuthFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        factory = AuthFactory(FireAuthUseCaseImpl(FireAuthRepoImpl()))
        viewModel = ViewModelProvider(this,factory).get(CerrarViewModel::class.java)

        return inflater.inflate(R.layout.cerrar_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.cerrarSesion()

        val intent  = Intent(this.context,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        this.activity?.finish()

        // TODO: Use the ViewModel
    }

}

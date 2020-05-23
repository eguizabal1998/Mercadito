package com.basicdeb.mercadito.ui.estados

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.basicdeb.mercadito.R
import com.basicdeb.mercadito.viewmodel.estados.ProgresoViewModel

class ProgresoFragment : Fragment() {

    companion object {
        fun newInstance() = ProgresoFragment()
    }

    private lateinit var viewModel: ProgresoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.progreso_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ProgresoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}

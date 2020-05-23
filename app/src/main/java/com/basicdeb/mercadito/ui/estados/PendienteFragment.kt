package com.basicdeb.mercadito.ui.estados

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.basicdeb.mercadito.R
import com.basicdeb.mercadito.viewmodel.estados.PendienteViewModel

class PendienteFragment : Fragment() {

    companion object {
        fun newInstance() = PendienteFragment()
    }

    private lateinit var viewModel: PendienteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pendiente_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PendienteViewModel::class.java)
        // TODO: Use the ViewModel
    }

}

package com.basicdeb.mercadito.ui.config

import androidx.lifecycle.ViewModel
import com.basicdeb.mercadito.domain.FireAuthUseCase
import com.basicdeb.mercadito.domain.FiredbUseCase

class CerrarViewModel(private val fireAuthUseCase: FireAuthUseCase) : ViewModel() {

    fun cerrarSesion(){
        fireAuthUseCase.closeSesion()
    }
}

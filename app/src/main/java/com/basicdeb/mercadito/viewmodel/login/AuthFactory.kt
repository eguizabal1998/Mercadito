package com.basicdeb.mercadito.viewmodel.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.basicdeb.mercadito.domain.FireAuthUseCase

class AuthFactory(private val fireAuthUseCase: FireAuthUseCase): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(FireAuthUseCase::class.java).newInstance(fireAuthUseCase)
    }
}
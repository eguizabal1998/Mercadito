package com.basicdeb.mercadito.viewmodel.login

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basicdeb.mercadito.domain.FireAuthUseCase
import com.basicdeb.mercadito.vo.Resource
import kotlinx.coroutines.launch

class LoginViewModel(private val fireAuthUseCase: FireAuthUseCase) : ViewModel() {

    val correo = ObservableField<String>()
    val contra = ObservableField<String>()

    val eventoLogin: MutableLiveData<Resource<Any>> by lazy {
        MutableLiveData<Resource<Any>>()
    }

    fun checkField(){
        Log.i("login","check")
        eventoLogin.value = Resource.Loading()
        if(contra.get().isNullOrEmpty() || correo.get().isNullOrEmpty()){
            eventoLogin.value = Resource.Failure(Exception("Complete Los Campos"))
        }else{
            login()
        }
    }

    private fun login() {
        viewModelScope.launch {
            try {
                eventoLogin.value = fireAuthUseCase.login(correo.get().toString(),contra.get().toString())
                clean()
            }catch (e:Exception){
                eventoLogin.value = Resource.Failure(e)
                clean()
            }
        }
    }

    private fun clean(){
        contra.set("")
        correo.set("")
    }

    fun getUser():Boolean{
        return fireAuthUseCase.currentUser()
    }
}


package com.basicdeb.mercadito.viewmodel.negocio

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basicdeb.mercadito.domain.FiredbUseCase
import com.basicdeb.mercadito.objects.Producto
import com.basicdeb.mercadito.vo.Resource
import kotlinx.coroutines.launch

class NegocioViewModel(private val firedbUseCase: FiredbUseCase) : ViewModel() {
    // TODO: Implement the ViewModel

    val lista = mutableListOf<Producto>()

    lateinit var departamento :String

    val eventoObtenerLista: MutableLiveData<Resource<MutableList<Producto>>> by lazy {
        MutableLiveData<Resource<MutableList<Producto>>>()
    }

    val eventoBorrar: MutableLiveData<Resource<Any>> by lazy {
        MutableLiveData<Resource<Any>>()
    }

    val eventoChange: MutableLiveData<Resource<Any>> by lazy {
        MutableLiveData<Resource<Any>>()
    }

    fun getProductos(){
        eventoObtenerLista.value = Resource.Loading()
        viewModelScope.launch {
            try {
                eventoObtenerLista.value = firedbUseCase.getProductos(departamento)
            }catch (e:Exception){
                eventoObtenerLista.value = Resource.Failure(e)
            }
        }
    }

    fun deleteProducto(id:String){
        eventoBorrar.value = Resource.Loading()
        viewModelScope.launch {
            try {
                eventoBorrar.value = firedbUseCase.deleteProducto(id,departamento)
            }catch (e:Exception){
                eventoBorrar.value = Resource.Failure(e)
            }
        }
    }

    fun changeDispo(id:String,change:Boolean){
        eventoChange.value = Resource.Loading()
        viewModelScope.launch {
            try {
                eventoChange.value = firedbUseCase.changeDispo(id,departamento,change)
            }catch (e:Exception){
                eventoChange.value = Resource.Failure(e)
            }
        }
    }

}

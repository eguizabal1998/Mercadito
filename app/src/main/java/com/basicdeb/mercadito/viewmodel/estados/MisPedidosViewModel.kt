package com.basicdeb.mercadito.viewmodel.estados

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.basicdeb.mercadito.domain.FiredbUseCase
import com.basicdeb.mercadito.objects.Ordenes
import com.basicdeb.mercadito.vo.Resource
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MisPedidosViewModel(private val firedbUseCase: FiredbUseCase) : ViewModel() {


    val listaOrdenes = mutableListOf<Ordenes>()


    val Ordenes = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            firedbUseCase.getOrdenes().collect {ordenes ->
                emit(ordenes)
            }
        }catch (e:Exception){
            emit(Resource.Failure(e))
        }
    }

}

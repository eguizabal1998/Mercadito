package com.basicdeb.mercadito.viewmodel.negocio

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basicdeb.mercadito.domain.FiredbUseCase
import com.basicdeb.mercadito.objects.Producto
import com.basicdeb.mercadito.vo.Resource
import kotlinx.coroutines.launch

class ModificarViewModel(private val firedbUseCase: FiredbUseCase) : ViewModel() {

    val nombre = ObservableField<String>()
    val precio = ObservableField<String>()
    val unidad = ObservableField<String>()
    val descripcion = ObservableField<String>()

    lateinit var imagen: String
    lateinit var imagenArray:ByteArray
    lateinit var docId:String
    var disponible:Boolean = true

    var NegocioId: String = ""

    val eventoModificar: MutableLiveData<Resource<Any>> by lazy {
        MutableLiveData<Resource<Any>>()
    }


    fun checkCampos(){
        eventoModificar.value = Resource.Loading()
        if(nombre.get().isNullOrEmpty() || precio.get().isNullOrEmpty() || unidad.get().isNullOrEmpty() || descripcion.get().isNullOrEmpty()){
            eventoModificar.value = Resource.Failure(Exception("Complete los campos"))
        }else{
            agregarProducto()
        }
    }

    private fun agregarProducto(){
        val producto = Producto(nombre.get().toString(),
            descripcion.get().toString(),precio.get().toString().toFloat(),unidad.get().toString(),NegocioId,imagen,disponible,docId)
        viewModelScope.launch {
            try {
                eventoModificar.value = firedbUseCase.modProducto(producto,imagenArray,"")
            }catch (e:Exception){
                eventoModificar.value = Resource.Failure(e)
            }
        }
    }
}

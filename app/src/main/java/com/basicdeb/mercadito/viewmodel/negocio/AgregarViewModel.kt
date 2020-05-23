package com.basicdeb.mercadito.viewmodel.negocio

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basicdeb.mercadito.domain.FiredbUseCase
import com.basicdeb.mercadito.objects.Producto
import com.basicdeb.mercadito.vo.Resource
import kotlinx.coroutines.launch

class AgregarViewModel(private val firedbUseCase: FiredbUseCase) : ViewModel() {

    val nombre = ObservableField<String>()
    val precio = ObservableField<String>()
    val unidad = ObservableField<String>()
    val descripcion = ObservableField<String>()

    val eventoGuardar: MutableLiveData<Resource<Any>> by lazy {
        MutableLiveData<Resource<Any>>()
    }

    lateinit var image: ByteArray
    lateinit var departamento:String


    fun checkCampos(){
        eventoGuardar.value = Resource.Loading()
        if(nombre.get().isNullOrEmpty() || precio.get().isNullOrEmpty() || unidad.get().isNullOrEmpty() || descripcion.get().isNullOrEmpty()){
            eventoGuardar.value =Resource.Failure(Exception("Complete los campos"))
        }else{
            agregarProducto()
        }
    }

    private fun agregarProducto(){
        val producto = Producto(nombre.get().toString(),
            descripcion.get().toString(),
            precio.get().toString().toFloat(),
            unidad.get().toString(),
            "00",
            "00",
            true,
            "00")
        viewModelScope.launch {
            try {
                eventoGuardar.value = firedbUseCase.saveProductos(producto,image,departamento)
                clean()
            }catch (e:Exception){
                eventoGuardar.value = Resource.Failure(e)
            }
        }
    }

    private fun clean(){
        nombre.set("")
        precio.set("")
        unidad.set("")
        descripcion.set("")
        eventoGuardar.value = Resource.wait()
    }

}

package com.basicdeb.mercadito.viewmodel.perfil

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basicdeb.mercadito.domain.FiredbUseCase
import com.basicdeb.mercadito.objects.Perfil
import com.basicdeb.mercadito.vo.Resource
import kotlinx.coroutines.launch

class PerfilViewModel(private val firedbUseCase: FiredbUseCase) : ViewModel() {

    val nombre = ObservableField<String>()
    val descripcion = ObservableField<String>()
    val numero = ObservableField<String>()
    val facebook = ObservableField<String>()

    lateinit var departamento:String

    lateinit var image: ByteArray

    val eventoGuardar: MutableLiveData<Resource<Any>> by lazy {
        MutableLiveData<Resource<Any>>()
    }

    val eventoGetPerfil: MutableLiveData<Resource<Perfil?>> by lazy {
        MutableLiveData<Resource<Perfil?>>()
    }

    val eventoPortada: MutableLiveData<Resource<String>> by lazy {
        MutableLiveData<Resource<String>>()
    }

    fun checkNombre(){
        eventoGuardar.value = Resource.Loading()
        if(facebook.get().isNullOrEmpty()){
            facebook.set("0")
        }
        if(nombre.get().isNullOrEmpty() || descripcion.get().isNullOrEmpty() || numero.get().isNullOrEmpty()){
            eventoGuardar.value = Resource.Failure(Exception("El campo es requerido"))
        }else if (departamento == "Seleccione un Departamento"){
            eventoGuardar.value = Resource.Failure(Exception("Seleccione un departamento"))
        }else{
            guardar()
        }
    }

    private fun guardar(){
        val perfil = Perfil(nombre.get().toString(),descripcion.get().toString(),numero.get().toString().toInt(),
            facebook.get().toString(),departamento)
        viewModelScope.launch {
            try {
                eventoGuardar.value = firedbUseCase.guardar(perfil,image,departamento)
            }catch (e:Exception){
                eventoGuardar.value = Resource.Failure(e)
            }
        }
    }

    fun getPerfil(){
        eventoGetPerfil.value = Resource.Loading()
        viewModelScope.launch {
            try {
                eventoGetPerfil.value = firedbUseCase.getPerfil(departamento)
                eventoPortada.value = firedbUseCase.getPortada()
            }catch (e:Exception){
                eventoGuardar.value = Resource.Failure(e)
            }
        }
    }

}

package com.basicdeb.mercadito.viewmodel.config

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basicdeb.mercadito.domain.FiredbUseCase
import com.basicdeb.mercadito.objects.Horarios
import com.basicdeb.mercadito.vo.Resource
import kotlinx.coroutines.launch

class ConfigViewModel(private val firedbUseCase: FiredbUseCase) : ViewModel() {

    val lunes = ObservableBoolean()
    val martes = ObservableBoolean()
    val miercoles = ObservableBoolean()
    val jueves = ObservableBoolean()
    val viernes = ObservableBoolean()
    val sabado = ObservableBoolean()
    val domingo = ObservableBoolean()

    val horaA = ObservableInt()
    val minutoA = ObservableInt()
    val horaC = ObservableInt()
    val minutoC = ObservableInt()

    var departamento = ""

    val eventoGuardar: MutableLiveData<Resource<Any>> by lazy {
        MutableLiveData<Resource<Any>>()
    }

    val eventoObtener: MutableLiveData<Resource<Horarios>> by lazy {
        MutableLiveData<Resource<Horarios>>()
    }

    fun guardar(){

        val horarios = Horarios(lunes.get(),martes.get(),miercoles.get(),jueves.get(),viernes.get(),sabado.get(),
        domingo.get(),horaA.get(),minutoA.get(),horaC.get(),minutoC.get())

        eventoGuardar.value = Resource.Loading()
        viewModelScope.launch {
            try {
                eventoGuardar.value = firedbUseCase.guardarConfig(horarios,departamento)
            }catch (e:Exception){
                eventoGuardar.value = Resource.Failure(e)
            }
        }
    }

    fun obtener(){
        eventoObtener.value = Resource.Loading()
        viewModelScope.launch {
            try {
                eventoObtener.value = firedbUseCase.getConfig(departamento)
            }catch (e:Exception){
                eventoObtener.value = Resource.Failure(e)
            }
        }
    }


}

package com.basicdeb.mercadito.domain

import com.basicdeb.mercadito.data.FiredbRepo
import com.basicdeb.mercadito.objects.Horarios
import com.basicdeb.mercadito.objects.Ordenes
import com.basicdeb.mercadito.objects.Perfil
import com.basicdeb.mercadito.objects.Producto
import com.basicdeb.mercadito.vo.Resource
import kotlinx.coroutines.flow.Flow

class FiredbUseCaseImpl(private val firedbRepo: FiredbRepo): FiredbUseCase {

    override suspend fun guardar(Perfil: Perfil,imagen:ByteArray,departamento:String): Resource<Boolean> = firedbRepo.guardar(Perfil,imagen,departamento)

    override suspend fun getPerfil(departamento: String): Resource<Perfil?> = firedbRepo.getPerfil(departamento)

    override suspend fun guardarConfig(horarios: Horarios,departamento: String): Resource<Boolean> = firedbRepo.guardarConfig(horarios,departamento)

    override suspend fun getConfig(departamento: String): Resource<Horarios> = firedbRepo.getConfig(departamento)

    override suspend fun saveProductos(producto: Producto,imagen:ByteArray,departamento: String): Resource<Producto> = firedbRepo.saveProductos(producto,imagen,departamento)

    override suspend fun getProductos(departamento:String): Resource<MutableList<Producto>> = firedbRepo.getProductos(departamento)

    override suspend fun deleteProducto(id: String,departamento:String): Resource<String> = firedbRepo.deleteProducto(id,departamento)

    override suspend fun modProducto(producto: Producto,imagen:ByteArray,departamento:String): Resource<Producto> = firedbRepo.modProducto(producto,imagen,departamento)

    override suspend fun getPortada(): Resource<String> = firedbRepo.getPortada()

    override suspend fun changeDispo(id: String,departamento: String, change: Boolean): Resource<Boolean> =
        firedbRepo.changeDispo(id,departamento,change)

    override suspend fun getOrdenes(): Flow<Resource<MutableList<Ordenes>>> = firedbRepo.getOrdenes()
}
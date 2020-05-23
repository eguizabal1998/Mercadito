package com.basicdeb.mercadito.domain

import com.basicdeb.mercadito.objects.Horarios
import com.basicdeb.mercadito.objects.Ordenes
import com.basicdeb.mercadito.objects.Perfil
import com.basicdeb.mercadito.objects.Producto
import com.basicdeb.mercadito.vo.Resource
import kotlinx.coroutines.flow.Flow

interface FiredbUseCase {
    suspend fun guardar(Perfil: Perfil,imagen:ByteArray,departamento:String): Resource<Boolean>
    suspend fun getPerfil(departamento: String): Resource<Perfil?>
    suspend fun guardarConfig(horarios: Horarios,departamento: String): Resource<Boolean>
    suspend fun getConfig(departamento: String):Resource<Horarios>
    suspend fun saveProductos(producto: Producto,imagen:ByteArray,departamento: String):Resource<Producto>
    suspend fun getProductos(departamento:String):Resource<MutableList<Producto>>
    suspend fun deleteProducto(id:String,departamento:String):Resource<String>
    suspend fun modProducto(producto: Producto,imagen:ByteArray,departamento:String):Resource<Producto>
    suspend fun getPortada():Resource<String>
    suspend fun changeDispo(id: String,departamento: String,change:Boolean):Resource<Boolean>

    suspend fun getOrdenes(): Flow<Resource<MutableList<Ordenes>>>
}